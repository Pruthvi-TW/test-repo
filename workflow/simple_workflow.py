"""Simple workflow without LangGraph for automated code generation and GitHub integration."""

import logging
import os
from config import get_config, WorkflowConfig
from claude_agent import ClaudeAgent
from github_agent import GitHubAgent

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class SimpleCodeGenerationWorkflow:
    """Simple workflow for automated code generation and GitHub push."""
    
    def __init__(self, config: WorkflowConfig):
        """Initialize the workflow."""
        self.config = config
        self.claude_agent = ClaudeAgent(config)
        self.github_agent = GitHubAgent(config)
    
    def read_prompt(self) -> str:
        """Read prompt from the specified file."""
        try:
            logger.info("Reading prompt from file")
            
            prompt_path = self.config.prompt_file_path
            if not os.path.exists(prompt_path):
                raise FileNotFoundError(f"Prompt file not found: {prompt_path}")
            
            with open(prompt_path, 'r', encoding='utf-8') as file:
                prompt = file.read().strip()
            
            if not prompt:
                raise ValueError("Prompt file is empty")
            
            logger.info(f"Successfully read prompt: {prompt[:100]}...")
            return prompt
            
        except Exception as e:
            logger.error(f"Error reading prompt: {str(e)}")
            raise Exception(f"Failed to read prompt: {str(e)}")
    
    def generate_code(self, prompt: str) -> dict:
        """Generate code using Claude API."""
        try:
            logger.info("Generating code with Claude")
            
            code_data = self.claude_agent.generate_code(prompt)
            
            # Validate generated code
            if not self.claude_agent.validate_code(code_data["code"]):
                raise ValueError("Generated code failed validation")
            
            logger.info(f"Successfully generated code: {code_data['filename']}")
            return code_data
            
        except Exception as e:
            logger.error(f"Error generating code: {str(e)}")
            raise Exception(f"Failed to generate code: {str(e)}")
    
    def push_to_github(self, code_data: dict) -> dict:
        """Push generated code to GitHub repository."""
        try:
            logger.info("Pushing code to GitHub")
            
            github_result = self.github_agent.push_generated_code(code_data)
            
            if not github_result["success"]:
                raise Exception(github_result.get("error", "Unknown GitHub error"))
            
            logger.info(f"Successfully pushed to GitHub: {github_result['file_url']}")
            return github_result
            
        except Exception as e:
            logger.error(f"Error pushing to GitHub: {str(e)}")
            raise Exception(f"Failed to push to GitHub: {str(e)}")
    
    def run(self) -> dict:
        """
        Run the complete workflow.
        
        Returns:
            Dictionary with workflow results
        """
        try:
            logger.info("Starting automated code generation workflow")
            
            # Step 1: Read prompt
            prompt = self.read_prompt()
            
            # Step 2: Generate code
            code_data = self.generate_code(prompt)
            
            # Step 3: Push to GitHub
            github_result = self.push_to_github(code_data)
            
            # Return success result
            return {
                "success": True,
                "prompt": prompt,
                "generated_file": code_data["filename"],
                "github_url": github_result["file_url"],
                "commit_url": github_result["commit_url"],
                "description": code_data["description"]
            }
                
        except Exception as e:
            logger.error(f"Workflow execution failed: {str(e)}")
            return {
                "success": False,
                "error": str(e)
            }


def create_simple_workflow() -> SimpleCodeGenerationWorkflow:
    """Create and return a configured simple workflow instance."""
    config = get_config()
    return SimpleCodeGenerationWorkflow(config)
