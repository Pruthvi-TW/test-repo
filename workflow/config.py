"""Configuration management for the automated workflow."""

import os
from typing import Optional
from dotenv import load_dotenv
from pydantic import BaseModel, Field

# Load environment variables
load_dotenv()


class WorkflowConfig(BaseModel):
    """Configuration settings for the automated workflow."""
    
    # Claude API settings
    anthropic_api_key: str = Field(..., description="Anthropic API key for Claude")
    
    # GitHub settings
    github_token: str = Field(..., description="GitHub personal access token")
    github_repo_owner: str = Field(..., description="GitHub repository owner")
    github_repo_name: str = Field(..., description="GitHub repository name")
    
    # Workflow settings
    prompt_file_path: str = Field(default="Prompts/prompt.txt", description="Path to prompt file")
    output_directory: str = Field(default="generated_code", description="Directory for generated code")
    commit_message_prefix: str = Field(default="[AUTO-GENERATED]", description="Prefix for commit messages")
    
    @classmethod
    def from_env(cls) -> "WorkflowConfig":
        """Create configuration from environment variables."""
        return cls(
            anthropic_api_key=os.getenv("ANTHROPIC_API_KEY", ""),
            github_token=os.getenv("GITHUB_TOKEN", ""),
            github_repo_owner=os.getenv("GITHUB_REPO_OWNER", ""),
            github_repo_name=os.getenv("GITHUB_REPO_NAME", ""),
            prompt_file_path=os.getenv("PROMPT_FILE_PATH", "Prompts/prompt.txt"),
            output_directory=os.getenv("OUTPUT_DIRECTORY", "generated_code"),
            commit_message_prefix=os.getenv("COMMIT_MESSAGE_PREFIX", "[AUTO-GENERATED]"),
        )
    
    def validate_config(self) -> bool:
        """Validate that all required configuration is present."""
        required_fields = [
            self.anthropic_api_key,
            self.github_token,
            self.github_repo_owner,
            self.github_repo_name,
        ]
        return all(field.strip() for field in required_fields)


def get_config() -> WorkflowConfig:
    """Get the workflow configuration."""
    config = WorkflowConfig.from_env()
    if not config.validate_config():
        raise ValueError(
            "Missing required configuration. Please check your .env file and ensure all required fields are set."
        )
    return config
