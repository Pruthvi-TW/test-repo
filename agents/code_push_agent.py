"""
Code Push Agent - Handles pushing generated code to GitHub repository.
"""
import time
import os
from typing import Dict, Any
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, update_agent_status
from config import Config
import git
from github import Github

class CodePushAgent(BaseAgent):
    """Agent responsible for pushing code to GitHub repository."""
    
    def __init__(self):
        super().__init__("CodePushAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Push the generated code to GitHub repository."""
        start_time = time.time()
        
        try:
            self.log_progress("Starting code push process")
            
            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)
            
            # Validate required state
            required_keys = ['generated_files', 'guardrails_results', 'dependency_analysis']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")
            
            # Check if guardrails passed
            guardrails_results = state['guardrails_results']
            if not self._should_proceed_with_push(guardrails_results):
                return self.handle_error(state, ValueError("GuardRails checks failed - cannot push code"), "guardrails_failed")
            
            generated_files = state['generated_files']
            dependency_analysis = state['dependency_analysis']
            tech_stack = state['technology_stack']
            
            # Create repository structure and push code
            repository_info = self._push_to_github(generated_files, dependency_analysis, tech_stack, state)
            
            # Update state
            state['code_push_complete'] = True
            state['repository_info'] = repository_info
            
            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state, 
                self.name, 
                AgentStatus.COMPLETED, 
                output=repository_info,
                execution_time=execution_time
            )
            
            self.log_progress(f"Code push completed. Repository: {repository_info.get('repository_url', 'Unknown')}")
            
            return state
            
        except Exception as e:
            return self.handle_error(state, e, "code_push_process")
    
    def _should_proceed_with_push(self, guardrails_results: Dict[str, Any]) -> bool:
        """Check if guardrails allow code push."""
        approval_status = guardrails_results.get('approval_status', 'REJECTED')
        return approval_status in ['APPROVED', 'CONDITIONAL']
    
    def _push_to_github(self, generated_files: list, dependency_analysis: Dict[str, Any],
                       tech_stack, state: WorkflowState) -> Dict[str, Any]:
        """Push generated code to GitHub repository."""

        try:
            # Create local directory structure with timestamp
            timestamp = time.strftime("%Y%m%d_%H%M%S")
            project_name = f"{tech_stack.language}-{tech_stack.framework}-app-{timestamp}"
            local_path = Config.get_temp_path(project_name)

            # Clean and create directory
            if os.path.exists(local_path):
                import shutil
                shutil.rmtree(local_path)
            os.makedirs(local_path, exist_ok=True)
            
            # Write generated files
            self._write_files_to_disk(generated_files, local_path)

            # Write dependency file
            self._write_dependency_file(dependency_analysis, local_path, tech_stack)

            # Create README
            self._create_readme(local_path, state)

            # Instead of creating a new repo, add to existing repo
            return self._add_to_existing_repo(local_path, project_name, tech_stack, generated_files)

        except Exception as e:
            self.log_progress(f"Error in code push process: {str(e)}", "error")
            raise

    def _add_to_existing_repo(self, local_path: str, project_name: str, tech_stack, generated_files: list) -> Dict[str, Any]:
        """Add generated code to the existing repository."""
        try:
            # Get the current repository root
            current_dir = os.getcwd()

            # Create a new directory in generated_code for this application
            output_dir = Config.get_output_path(project_name)

            # Copy files from temp to generated_code
            import shutil
            if os.path.exists(output_dir):
                shutil.rmtree(output_dir)
            shutil.copytree(local_path, output_dir)

            # Try to add and commit to the existing repo
            try:
                import git
                repo = git.Repo(current_dir)

                # Add the new files
                repo.git.add(output_dir)

                # Commit
                commit_message = f"Add generated {tech_stack.language} {tech_stack.framework} application: {project_name}"
                repo.index.commit(commit_message)

                # Push to origin
                if Config.GITHUB_TOKEN:
                    origin = repo.remote('origin')
                    origin.push()

                    return {
                        'status': 'success',
                        'repository_url': Config.GITHUB_REPO_URL,
                        'local_path': output_dir,
                        'commit_hash': str(repo.head.commit),
                        'files_pushed': len(generated_files) + 2,
                        'branch': 'main',
                        'project_name': project_name
                    }
                else:
                    return {
                        'status': 'committed_locally',
                        'repository_url': Config.GITHUB_REPO_URL,
                        'local_path': output_dir,
                        'commit_hash': str(repo.head.commit),
                        'files_created': len(generated_files) + 2,
                        'note': 'Committed locally - no GitHub token for push',
                        'project_name': project_name
                    }

            except Exception as git_error:
                self.log_progress(f"Git operations failed: {str(git_error)}", "warning")
                return {
                    'status': 'files_saved',
                    'repository_url': Config.GITHUB_REPO_URL,
                    'local_path': output_dir,
                    'files_created': len(generated_files) + 2,
                    'note': 'Files saved locally - Git operations failed',
                    'project_name': project_name,
                    'error': str(git_error)
                }

        except Exception as e:
            self.log_progress(f"Error saving files: {str(e)}", "error")
            raise
    
    def _write_files_to_disk(self, generated_files: list, base_path: str):
        """Write generated files to disk."""
        for file_obj in generated_files:
            if isinstance(file_obj, dict):
                path = file_obj.get('path', 'unknown')
                content = file_obj.get('content', '')
            else:
                path = file_obj.path
                content = file_obj.content

            file_path = os.path.join(base_path, path)

            # Create directory if it doesn't exist
            os.makedirs(os.path.dirname(file_path), exist_ok=True)

            # Write file content
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)

            self.log_progress(f"Written file: {path}")
    
    def _write_dependency_file(self, dependency_analysis: Dict[str, Any], base_path: str, tech_stack):
        """Write the dependency file (pom.xml, package.json, etc.)."""
        build_tool = tech_stack.build_tool
        dependency_content = dependency_analysis.get('dependency_file_content', '')
        
        if not dependency_content:
            return
        
        # Determine filename based on build tool
        filename_mapping = {
            'maven': 'pom.xml',
            'gradle': 'build.gradle',
            'npm': 'package.json',
            'yarn': 'package.json',
            'pip': 'requirements.txt',
            'poetry': 'pyproject.toml',
            'cargo': 'Cargo.toml',
            'go-mod': 'go.mod',
            'composer': 'composer.json'
        }
        
        filename = filename_mapping.get(build_tool, 'dependencies.txt')
        file_path = os.path.join(base_path, filename)
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(dependency_content)
        
        self.log_progress(f"Written dependency file: {filename}")
    
    def _create_readme(self, base_path: str, state: WorkflowState):
        """Create a README file for the project."""
        tech_stack = state['technology_stack']
        business_context = state.get('business_context')
        
        readme_content = f"""# {tech_stack.language.title()} {tech_stack.framework.title()} Application

## Overview
This application was generated using an agentic workflow system.

### Technology Stack
- **Language**: {tech_stack.language}
- **Framework**: {tech_stack.framework}
- **Database**: {tech_stack.database}
- **Build Tool**: {tech_stack.build_tool}

### Business Domain
- **Domain**: {business_context.domain if business_context else 'Unknown'}

### Generated Files
{self._format_file_list(state.get('generated_files', []))}

### Dependencies
Total dependencies analyzed: {state.get('dependency_analysis', {}).get('total_dependencies', 0)}

### Code Quality
- **Overall Evaluation Score**: {state.get('evaluation_results', {}).get('overall_score', 0)}/10
- **GuardRails Status**: {state.get('guardrails_results', {}).get('status', 'Unknown')}

### Getting Started
1. Install dependencies using {tech_stack.build_tool}
2. Configure your database connection
3. Run the application
4. Access the API documentation

### Generated by
Agentic Workflow System - Workflow ID: {state.get('workflow_id', 'Unknown')}
Generated on: {time.strftime('%Y-%m-%d %H:%M:%S')}
"""
        
        readme_path = os.path.join(base_path, 'README.md')
        with open(readme_path, 'w', encoding='utf-8') as f:
            f.write(readme_content)
        
        self.log_progress("Created README.md")
    
    def _format_file_list(self, generated_files: list) -> str:
        """Format the list of generated files for README."""
        if not generated_files:
            return "No files generated."
        
        file_list = []
        for file_obj in generated_files:
            if isinstance(file_obj, dict):
                path = file_obj.get('path', 'unknown')
                purpose = file_obj.get('purpose', 'unknown')
            else:
                path = file_obj.path
                purpose = file_obj.purpose
            file_list.append(f"- `{path}` - {purpose}")

        return "\n".join(file_list)
