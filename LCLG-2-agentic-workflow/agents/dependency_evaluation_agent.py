"""
Dependency Evaluation Agent - Evaluates and manages project dependencies.
"""
import time
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, update_agent_status

class DependencyEvaluationAgent(BaseAgent):
    """Agent responsible for evaluating project dependencies."""
    
    def __init__(self):
        super().__init__("DependencyEvaluationAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Evaluate project dependencies and create dependency files."""
        start_time = time.time()
        
        try:
            self.log_progress("Starting dependency evaluation process")
            
            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)
            
            # Validate required state
            required_keys = ['technology_stack', 'code_structure', 'business_context']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")
            
            tech_stack = state['technology_stack']
            code_structure = state['code_structure']
            business_context = state['business_context']
            
            # Analyze dependencies
            dependency_analysis = self._analyze_dependencies(tech_stack, code_structure, business_context)
            
            # Update state
            state['dependency_evaluation_complete'] = True
            state['dependency_analysis'] = dependency_analysis
            
            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state, 
                self.name, 
                AgentStatus.COMPLETED, 
                output=dependency_analysis,
                execution_time=execution_time
            )
            
            self.log_progress("Dependency evaluation completed")
            
            return state
            
        except Exception as e:
            return self.handle_error(state, e, "dependency_evaluation_process")
    
    def _analyze_dependencies(self, tech_stack, code_structure, business_context) -> Dict[str, Any]:
        """Analyze and recommend dependencies for the project."""
        
        dependency_prompt = f"""
Analyze the dependencies needed for a {tech_stack.language} application using {tech_stack.framework}:

TECHNOLOGY STACK:
- Language: {tech_stack.language}
- Framework: {tech_stack.framework}
- Database: {tech_stack.database}
- Build Tool: {tech_stack.build_tool}

BUSINESS CONTEXT:
- Domain: {business_context.domain}
- Key Features: {', '.join(business_context.entities[:5])}

PROJECT STRUCTURE:
{self._format_project_structure(code_structure)[:500]}

Please provide:
1. Core dependencies required for the framework
2. Database-related dependencies
3. Testing dependencies
4. Security dependencies
5. Logging and monitoring dependencies
6. Build and deployment dependencies

Format as:

CORE_DEPENDENCIES:
- dependency1: version - purpose
- dependency2: version - purpose

DATABASE_DEPENDENCIES:
- dependency1: version - purpose

TESTING_DEPENDENCIES:
- dependency1: version - purpose

SECURITY_DEPENDENCIES:
- dependency1: version - purpose

LOGGING_DEPENDENCIES:
- dependency1: version - purpose

BUILD_DEPENDENCIES:
- dependency1: version - purpose

DEPENDENCY_FILE_CONTENT:
[Provide the complete dependency file content for {tech_stack.build_tool}]
"""
        
        system_prompt = f"You are an expert in {tech_stack.language} dependency management using {tech_stack.build_tool}."
        
        dependency_response = self.call_claude(dependency_prompt, system_prompt)
        
        return self._parse_dependency_response(dependency_response, tech_stack)
    
    def _parse_dependency_response(self, response: str, tech_stack) -> Dict[str, Any]:
        """Parse the dependency analysis response."""
        analysis = {
            'core_dependencies': [],
            'database_dependencies': [],
            'testing_dependencies': [],
            'security_dependencies': [],
            'logging_dependencies': [],
            'build_dependencies': [],
            'dependency_file_content': '',
            'build_tool': tech_stack.build_tool,
            'total_dependencies': 0
        }
        
        lines = response.split('\n')
        current_section = None
        
        for line in lines:
            line = line.strip()
            if line.startswith('CORE_DEPENDENCIES:'):
                current_section = 'core_dependencies'
            elif line.startswith('DATABASE_DEPENDENCIES:'):
                current_section = 'database_dependencies'
            elif line.startswith('TESTING_DEPENDENCIES:'):
                current_section = 'testing_dependencies'
            elif line.startswith('SECURITY_DEPENDENCIES:'):
                current_section = 'security_dependencies'
            elif line.startswith('LOGGING_DEPENDENCIES:'):
                current_section = 'logging_dependencies'
            elif line.startswith('BUILD_DEPENDENCIES:'):
                current_section = 'build_dependencies'
            elif line.startswith('DEPENDENCY_FILE_CONTENT:'):
                current_section = 'dependency_file_content'
            elif line and current_section:
                if current_section == 'dependency_file_content':
                    analysis['dependency_file_content'] += line + '\n'
                elif line.startswith('-') and current_section != 'dependency_file_content':
                    analysis[current_section].append(line[1:].strip())
        
        # Count total dependencies
        for key in ['core_dependencies', 'database_dependencies', 'testing_dependencies', 
                   'security_dependencies', 'logging_dependencies', 'build_dependencies']:
            analysis['total_dependencies'] += len(analysis[key])
        
        return analysis

    def _format_project_structure(self, code_structure) -> str:
        """Format project structure for display."""
        if isinstance(code_structure, dict):
            # Handle dictionary format
            structure_info = []

            # Add basic info
            language = code_structure.get('language', 'unknown')
            framework = code_structure.get('framework', 'unknown')
            structure_info.append(f"Language: {language}")
            structure_info.append(f"Framework: {framework}")

            # Add directory structure if available
            directory_structure = code_structure.get('directory_structure', {})
            if directory_structure:
                structure_info.append("\nDirectory Structure:")
                if isinstance(directory_structure, dict):
                    for path, description in directory_structure.items():
                        structure_info.append(f"  {path}: {description}")
                else:
                    structure_info.append(str(directory_structure))

            # Add core files info
            core_files = code_structure.get('core_files', [])
            if core_files:
                structure_info.append(f"\nCore Files: {len(core_files)} files planned")
                for file_info in core_files[:5]:  # Show first 5
                    if isinstance(file_info, dict):
                        path = file_info.get('path', 'unknown')
                        purpose = file_info.get('purpose', 'unknown')
                        structure_info.append(f"  - {path}: {purpose}")

            return "\n".join(structure_info)
        else:
            # Handle object format (fallback)
            return getattr(code_structure, 'directory_structure', str(code_structure))
