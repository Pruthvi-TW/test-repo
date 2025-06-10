"""
GuardRails Agent - Ensures code quality, security, and compliance standards.
"""
import time
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, update_agent_status

class GuardRailsAgent(BaseAgent):
    """Agent responsible for enforcing guardrails and compliance."""
    
    def __init__(self):
        super().__init__("GuardRailsAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Apply guardrails and compliance checks to the generated code."""
        start_time = time.time()
        
        try:
            self.log_progress("Starting guardrails evaluation process")
            
            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)
            
            # Validate required state
            required_keys = ['generated_files', 'evaluation_results', 'technology_stack']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")
            
            generated_files = state['generated_files']
            evaluation_results = state['evaluation_results']
            tech_stack = state['technology_stack']
            
            # Apply guardrails
            guardrails_results = self._apply_guardrails(generated_files, evaluation_results, tech_stack)
            
            # Update state
            state['guardrails_complete'] = True
            state['guardrails_results'] = guardrails_results
            
            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state, 
                self.name, 
                AgentStatus.COMPLETED, 
                output=guardrails_results,
                execution_time=execution_time
            )
            
            self.log_progress("GuardRails evaluation completed")
            
            return state
            
        except Exception as e:
            return self.handle_error(state, e, "guardrails_process")
    
    def _apply_guardrails(self, generated_files: list, evaluation_results: Dict[str, Any], tech_stack) -> Dict[str, Any]:
        """Apply guardrails and compliance checks."""
        
        guardrails_prompt = f"""
Apply comprehensive guardrails and compliance checks to this {tech_stack.language} application:

TECHNOLOGY STACK:
- Language: {tech_stack.language}
- Framework: {tech_stack.framework}

CURRENT EVALUATION SCORES:
- Overall Score: {evaluation_results.get('overall_score', 0)}/10
- Security Score: {evaluation_results.get('scores', {}).get('security', 0)}/10
- Code Quality Score: {evaluation_results.get('scores', {}).get('code_quality', 0)}/10

GENERATED FILES COUNT: {len(generated_files)}

GUARDRAILS TO CHECK:
1. **Security Compliance**
   - Input validation
   - SQL injection prevention
   - XSS protection
   - Authentication/Authorization
   - Data encryption
   - Secure configuration

2. **Code Quality Standards**
   - Coding conventions
   - Error handling
   - Logging practices
   - Performance considerations
   - Memory management

3. **Business Logic Validation**
   - Business rule implementation
   - Data integrity
   - Transaction handling
   - Audit trail

4. **Deployment Readiness**
   - Configuration management
   - Environment variables
   - Health checks
   - Monitoring hooks

5. **Compliance Requirements**
   - Data privacy (GDPR, etc.)
   - Industry standards
   - Documentation requirements

Please evaluate and provide:

GUARDRAILS_STATUS: [PASS/FAIL/WARNING]
SECURITY_COMPLIANCE: [PASS/FAIL/WARNING] - [explanation]
CODE_QUALITY_COMPLIANCE: [PASS/FAIL/WARNING] - [explanation]
BUSINESS_LOGIC_COMPLIANCE: [PASS/FAIL/WARNING] - [explanation]
DEPLOYMENT_READINESS: [PASS/FAIL/WARNING] - [explanation]
REGULATORY_COMPLIANCE: [PASS/FAIL/WARNING] - [explanation]

CRITICAL_ISSUES:
- [Issue 1 if any]
- [Issue 2 if any]

RECOMMENDATIONS:
- [Recommendation 1]
- [Recommendation 2]

APPROVAL_STATUS: [APPROVED/REJECTED/CONDITIONAL]
APPROVAL_CONDITIONS:
- [Condition 1 if conditional]
- [Condition 2 if conditional]
"""
        
        system_prompt = f"You are a senior security and compliance officer with expertise in {tech_stack.language} applications."
        
        guardrails_response = self.call_claude(guardrails_prompt, system_prompt)
        
        return self._parse_guardrails_response(guardrails_response)
    
    def _parse_guardrails_response(self, response: str) -> Dict[str, Any]:
        """Parse the guardrails evaluation response."""
        result = {
            'status': 'UNKNOWN',
            'compliance_checks': {},
            'critical_issues': [],
            'recommendations': [],
            'approval_status': 'PENDING',
            'approval_conditions': [],
            'overall_compliance_score': 0
        }
        
        lines = response.split('\n')
        current_section = None
        
        compliance_categories = [
            'security_compliance', 'code_quality_compliance', 
            'business_logic_compliance', 'deployment_readiness', 
            'regulatory_compliance'
        ]
        
        for line in lines:
            line = line.strip()
            if line.startswith('GUARDRAILS_STATUS:'):
                result['status'] = line.split(':', 1)[1].strip()
            elif line.startswith('APPROVAL_STATUS:'):
                result['approval_status'] = line.split(':', 1)[1].strip()
            elif any(line.startswith(f'{cat.upper()}:') for cat in compliance_categories):
                for cat in compliance_categories:
                    if line.startswith(f'{cat.upper()}:'):
                        parts = line.split(':', 1)[1].strip().split(' - ', 1)
                        result['compliance_checks'][cat] = {
                            'status': parts[0].strip(),
                            'explanation': parts[1].strip() if len(parts) > 1 else ''
                        }
                        break
            elif line.startswith('CRITICAL_ISSUES:'):
                current_section = 'critical_issues'
            elif line.startswith('RECOMMENDATIONS:'):
                current_section = 'recommendations'
            elif line.startswith('APPROVAL_CONDITIONS:'):
                current_section = 'approval_conditions'
            elif line.startswith('-') and current_section:
                result[current_section].append(line[1:].strip())
        
        # Calculate overall compliance score
        pass_count = sum(1 for check in result['compliance_checks'].values() 
                        if check.get('status') == 'PASS')
        total_checks = len(result['compliance_checks'])
        if total_checks > 0:
            result['overall_compliance_score'] = round((pass_count / total_checks) * 10, 1)
        
        return result
