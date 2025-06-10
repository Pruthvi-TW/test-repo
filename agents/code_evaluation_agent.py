"""
Code Evaluation Agent - Evaluates generated code for quality and compliance.
"""
import time
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, update_agent_status

class CodeEvaluationAgent(BaseAgent):
    """Agent responsible for evaluating generated code."""
    
    def __init__(self):
        super().__init__("CodeEvaluationAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Evaluate the generated code for quality and compliance."""
        start_time = time.time()
        
        try:
            self.log_progress("Starting code evaluation process")
            
            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)
            
            # Validate required state
            required_keys = ['generated_files', 'technology_stack']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")
            
            generated_files = state['generated_files']
            tech_stack = state['technology_stack']
            
            # Evaluate code quality
            evaluation_results = self._evaluate_code_quality(generated_files, tech_stack)
            
            # Update state
            state['code_evaluation_complete'] = True
            state['evaluation_results'] = evaluation_results
            
            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state, 
                self.name, 
                AgentStatus.COMPLETED, 
                output=evaluation_results,
                execution_time=execution_time
            )
            
            self.log_progress("Code evaluation completed")
            
            return state
            
        except Exception as e:
            return self.handle_error(state, e, "code_evaluation_process")
    
    def _evaluate_code_quality(self, generated_files: list, tech_stack) -> Dict[str, Any]:
        """Evaluate the quality of generated code."""
        evaluation_prompt = f"""
Evaluate the following generated code for a {tech_stack.language} application:

TECHNOLOGY STACK:
- Language: {tech_stack.language}
- Framework: {tech_stack.framework}

GENERATED FILES:
{self._format_files_for_evaluation(generated_files)}

Please evaluate the code based on:
1. Code Quality and Best Practices
2. Security Considerations
3. Performance Implications
4. Maintainability
5. Test Coverage Potential
6. Documentation Quality

Provide a score (1-10) for each category and overall recommendations.

FORMAT:
OVERALL_SCORE: [1-10]
CODE_QUALITY: [1-10] - [brief explanation]
SECURITY: [1-10] - [brief explanation]
PERFORMANCE: [1-10] - [brief explanation]
MAINTAINABILITY: [1-10] - [brief explanation]
TESTABILITY: [1-10] - [brief explanation]
DOCUMENTATION: [1-10] - [brief explanation]

RECOMMENDATIONS:
- [Recommendation 1]
- [Recommendation 2]

ISSUES_FOUND:
- [Issue 1]
- [Issue 2]
"""
        
        system_prompt = f"You are a senior {tech_stack.language} code reviewer with expertise in {tech_stack.framework}."
        
        evaluation_response = self.call_claude(evaluation_prompt, system_prompt)
        
        return self._parse_evaluation_response(evaluation_response)
    
    def _format_files_for_evaluation(self, generated_files: list) -> str:
        """Format generated files for evaluation."""
        formatted = []
        for file_obj in generated_files[:3]:  # Limit to first 3 files
            if isinstance(file_obj, dict):
                path = file_obj.get('path', 'unknown')
                purpose = file_obj.get('purpose', 'unknown')
                content = file_obj.get('content', '')
            else:
                path = file_obj.path
                purpose = file_obj.purpose
                content = file_obj.content

            formatted.append(f"=== {path} ===")
            formatted.append(f"Purpose: {purpose}")
            formatted.append(f"Content (first 500 chars):")
            formatted.append(content[:500] + "..." if len(content) > 500 else content)
            formatted.append("")
        
        return "\n".join(formatted)
    
    def _parse_evaluation_response(self, response: str) -> Dict[str, Any]:
        """Parse the evaluation response."""
        result = {
            'overall_score': 0,
            'scores': {},
            'recommendations': [],
            'issues': []
        }
        
        lines = response.split('\n')
        current_section = None
        
        for line in lines:
            line = line.strip()
            if line.startswith('OVERALL_SCORE:'):
                try:
                    result['overall_score'] = int(line.split(':')[1].strip())
                except:
                    result['overall_score'] = 0
            elif ':' in line and any(category in line for category in ['CODE_QUALITY', 'SECURITY', 'PERFORMANCE', 'MAINTAINABILITY', 'TESTABILITY', 'DOCUMENTATION']):
                parts = line.split(':', 1)
                if len(parts) == 2:
                    category = parts[0].strip().lower()
                    try:
                        score = int(parts[1].split('-')[0].strip())
                        result['scores'][category] = score
                    except:
                        result['scores'][category] = 0
            elif line.startswith('RECOMMENDATIONS:'):
                current_section = 'recommendations'
            elif line.startswith('ISSUES_FOUND:'):
                current_section = 'issues'
            elif line.startswith('-') and current_section:
                result[current_section].append(line[1:].strip())
        
        return result
