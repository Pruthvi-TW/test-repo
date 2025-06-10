"""
Prevalidation Agent - Validates business context and technical requirements.
"""
import time
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, ValidationResult, update_agent_status

class PrevalidationAgent(BaseAgent):
    """Agent responsible for prevalidating business context and technical requirements."""
    
    def __init__(self):
        super().__init__("PrevalidationAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Validate the prompts and technology stack before proceeding."""
        start_time = time.time()

        try:
            self.log_progress("Starting prevalidation process")

            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)

            # Validate required state
            required_keys = ['prompts', 'technology_stack']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")

            prompts = state['prompts']
            tech_stack = state['technology_stack']

            # Create validation prompt
            validation_prompt = self._create_validation_prompt(prompts, tech_stack)
            system_prompt = self.create_system_prompt(tech_stack)

            # Call Claude for validation
            validation_result = self.call_claude(validation_prompt, system_prompt)

            # Parse validation result
            validation_status = self._parse_validation_result(validation_result)

            # Create ValidationResult object
            validation_obj = ValidationResult(
                status=validation_status['status'],
                confidence=validation_status['confidence'],
                can_proceed=validation_status['can_proceed'],
                requires_attention=validation_status['requires_attention'],
                details=validation_result
            )

            # Update state
            state['prevalidation_complete'] = True
            state['validation_result'] = validation_obj

            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state,
                self.name,
                AgentStatus.COMPLETED,
                output=validation_result,
                execution_time=execution_time
            )

            self.log_progress(f"Prevalidation completed with status: {validation_status['status']}")

            return state

        except Exception as e:
            return self.handle_error(state, e, "prevalidation_process")
    
    def _create_validation_prompt(self, prompts: list, tech_stack) -> str:
        """Create a prompt for validating the requirements."""
        prompt_content = "\n\n".join([f"=== {p.key}: {p.type} ===\n{p.content}" for p in prompts])
        
        validation_prompt = f"""
Please analyze the following requirements and provide a comprehensive validation:

DETECTED TECHNOLOGY STACK:
- Language: {tech_stack.language}
- Framework: {tech_stack.framework}
- Database: {tech_stack.database}
- Build Tool: {tech_stack.build_tool}
- Confidence: {tech_stack.confidence}

REQUIREMENTS TO VALIDATE:
{prompt_content}

VALIDATION TASKS:
1. **Technical Feasibility**: Assess if the requirements are technically feasible with the detected technology stack
2. **Completeness Check**: Identify any missing critical requirements or specifications
3. **Consistency Analysis**: Check for contradictions or inconsistencies in the requirements
4. **Technology Alignment**: Verify that the detected technology stack aligns with the requirements
5. **Scope Assessment**: Evaluate if the scope is appropriate for the given constraints
6. **Risk Identification**: Identify potential risks or challenges in implementation

Please provide your analysis in the following format:

VALIDATION_STATUS: [PASS/FAIL/WARNING]
CONFIDENCE_SCORE: [0.0-1.0]

TECHNICAL_FEASIBILITY:
- [Your assessment]

COMPLETENESS_CHECK:
- [Missing requirements if any]

CONSISTENCY_ANALYSIS:
- [Any inconsistencies found]

TECHNOLOGY_ALIGNMENT:
- [Assessment of technology stack alignment]

SCOPE_ASSESSMENT:
- [Scope evaluation]

RISK_IDENTIFICATION:
- [Potential risks and mitigation strategies]

RECOMMENDATIONS:
- [Any recommendations for improvement]

NEXT_STEPS:
- [Recommended next steps for the workflow]
"""
        return validation_prompt
    
    def _parse_validation_result(self, validation_result: str) -> Dict[str, Any]:
        """Parse the validation result from Claude."""
        lines = validation_result.split('\n')
        
        status = "UNKNOWN"
        confidence = 0.0
        
        for line in lines:
            if line.startswith("VALIDATION_STATUS:"):
                status = line.split(":", 1)[1].strip()
            elif line.startswith("CONFIDENCE_SCORE:"):
                try:
                    confidence = float(line.split(":", 1)[1].strip())
                except ValueError:
                    confidence = 0.0
        
        return {
            'status': status,
            'confidence': confidence,
            'can_proceed': status in ['PASS', 'WARNING'],
            'requires_attention': status in ['WARNING', 'FAIL']
        }
