"""
Business Context Agent - Processes and structures business requirements.
"""
import time
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, BusinessContext, update_agent_status

class BusinessContextAgent(BaseAgent):
    """Agent responsible for processing business context and requirements."""
    
    def __init__(self):
        super().__init__("BusinessContextAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Process business requirements and create structured context."""
        start_time = time.time()

        try:
            self.log_progress("Starting business context processing")

            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)

            # Validate required state
            required_keys = ['prompts', 'technology_stack', 'prevalidation_complete']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")

            prompts = state['prompts']
            tech_stack = state['technology_stack']

            # Create business analysis prompt
            business_prompt = self._create_business_analysis_prompt(prompts, tech_stack)
            system_prompt = self.create_system_prompt(tech_stack)

            # Call Claude for business analysis
            business_analysis = self.call_claude(business_prompt, system_prompt)

            # Parse business context
            business_context_dict = self._parse_business_context(business_analysis)

            # Create BusinessContext object
            business_context = BusinessContext(
                domain=business_context_dict.get('domain', 'unknown'),
                entities=business_context_dict.get('entities', []),
                processes=business_context_dict.get('processes', []),
                integrations=business_context_dict.get('integrations', []),
                business_rules=business_context_dict.get('business_rules', []),
                user_personas=business_context_dict.get('user_personas', []),
                success_criteria=business_context_dict.get('success_criteria', {}),
                technical_implications=business_context_dict.get('technical_implications', {})
            )

            # Update state
            state['business_context_complete'] = True
            state['business_context'] = business_context

            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state,
                self.name,
                AgentStatus.COMPLETED,
                output=business_analysis,
                execution_time=execution_time
            )

            self.log_progress("Business context processing completed")

            return state

        except Exception as e:
            return self.handle_error(state, e, "business_context_process")
    
    def _create_business_analysis_prompt(self, prompts: list, tech_stack) -> str:
        """Create a prompt for analyzing business requirements."""
        prompt_content = "\n\n".join([f"=== {p.key}: {p.type} ===\n{p.content}" for p in prompts])
        
        business_prompt = f"""
Analyze the following requirements and extract structured business context for a {tech_stack.language} application using {tech_stack.framework}:

REQUIREMENTS:
{prompt_content}

ANALYSIS TASKS:
1. **Business Domain**: Identify the primary business domain and use case
2. **Core Entities**: Extract main business entities and their relationships
3. **Business Processes**: Map out key business processes and workflows
4. **Data Flow**: Understand how data flows through the system
5. **Integration Points**: Identify external systems and APIs
6. **Business Rules**: Extract business logic and validation rules
7. **User Personas**: Identify different types of users and their needs
8. **Success Criteria**: Define what success looks like for this system

Please provide your analysis in the following structured format:

BUSINESS_DOMAIN:
- Primary Domain: [e.g., Financial Services, E-commerce, Healthcare]
- Use Case: [Main use case description]
- Industry Context: [Relevant industry context]

CORE_ENTITIES:
- Entity 1: [Name and description]
- Entity 2: [Name and description]
- [Continue for all entities]

ENTITY_RELATIONSHIPS:
- [Describe relationships between entities]

BUSINESS_PROCESSES:
1. Process Name: [Description]
   - Steps: [List key steps]
   - Inputs: [Required inputs]
   - Outputs: [Expected outputs]
   - Business Rules: [Applicable rules]

DATA_FLOW:
- Input Sources: [Where data comes from]
- Processing Steps: [How data is processed]
- Output Destinations: [Where data goes]
- Data Validation: [Validation requirements]

INTEGRATION_POINTS:
- External API 1: [Name, purpose, data exchange]
- External API 2: [Name, purpose, data exchange]
- [Continue for all integrations]

BUSINESS_RULES:
- Rule 1: [Description and implementation requirement]
- Rule 2: [Description and implementation requirement]
- [Continue for all rules]

USER_PERSONAS:
- User Type 1: [Description, needs, permissions]
- User Type 2: [Description, needs, permissions]
- [Continue for all user types]

SUCCESS_CRITERIA:
- Functional: [What the system must do]
- Non-Functional: [Performance, security, etc.]
- Business: [Business value and metrics]

TECHNICAL_IMPLICATIONS:
- Architecture Patterns: [Recommended patterns for this domain]
- Security Requirements: [Security considerations]
- Scalability Needs: [Scalability requirements]
- Compliance: [Regulatory or compliance requirements]
"""
        return business_prompt
    
    def _parse_business_context(self, business_analysis: str) -> Dict[str, Any]:
        """Parse the business analysis into structured context."""
        context = {
            'domain': 'unknown',
            'entities': [],
            'processes': [],
            'integrations': [],
            'business_rules': [],
            'user_personas': [],
            'success_criteria': {},
            'technical_implications': {}
        }
        
        # Simple parsing - in production, you might want more sophisticated parsing
        lines = business_analysis.split('\n')
        current_section = None
        
        for line in lines:
            line = line.strip()
            if line.startswith('BUSINESS_DOMAIN:'):
                current_section = 'domain'
            elif line.startswith('CORE_ENTITIES:'):
                current_section = 'entities'
            elif line.startswith('BUSINESS_PROCESSES:'):
                current_section = 'processes'
            elif line.startswith('INTEGRATION_POINTS:'):
                current_section = 'integrations'
            elif line.startswith('BUSINESS_RULES:'):
                current_section = 'business_rules'
            elif line.startswith('USER_PERSONAS:'):
                current_section = 'user_personas'
            elif line.startswith('SUCCESS_CRITERIA:'):
                current_section = 'success_criteria'
            elif line.startswith('TECHNICAL_IMPLICATIONS:'):
                current_section = 'technical_implications'
            elif line and current_section:
                # Add content to appropriate section
                if current_section == 'domain' and 'Primary Domain:' in line:
                    context['domain'] = line.split(':', 1)[1].strip()
                elif current_section in ['entities', 'processes', 'integrations', 'business_rules', 'user_personas']:
                    if line.startswith('-') or line.startswith('•'):
                        context[current_section].append(line[1:].strip())
        
        return context
