"""
Configuration management for the agentic workflow system.
"""
import os
from typing import Dict, Any
from dotenv import load_dotenv

load_dotenv()

class Config:
    """Configuration class for the agentic workflow system."""
    
    # Claude API Configuration
    CLAUDE_API_KEY = os.getenv("CLAUDE_API_KEY", "")  # Set this in environment or .env file
    CLAUDE_MODEL = "claude-3-5-sonnet-20241022"
    CLAUDE_MAX_TOKENS = 4000

    # GitHub Configuration
    GITHUB_REPO_URL = "https://github.com/Pruthvi-TW/test-repo.git"
    GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "")  # Set this in environment or .env file
    GITHUB_REPO_NAME = "test-repo"
    GITHUB_OWNER = "Pruthvi-TW"
    
    # Prompt Configuration
    PROMPTS_DIR = "Prompts"
    PROMPT_FILES = ["P1-PreTech.txt", "P2-Business.txt", "P3-PostTech.txt", "P4-Mock.txt"]
    
    # Output Configuration
    OUTPUT_DIR = "generated_code"
    TEMP_DIR = "temp"
    
    # Agent Configuration
    AGENT_TIMEOUT = 300  # 5 minutes
    MAX_RETRIES = 3
    
    # Code Generation Configuration (Generic)
    DEFAULT_LANGUAGE = "auto-detect"  # Will be detected from prompts
    SUPPORTED_LANGUAGES = [
        "java", "python", "golang", "javascript", "typescript",
        "csharp", "rust", "kotlin", "scala", "php"
    ]
    SUPPORTED_FRAMEWORKS = [
        "spring-boot", "django", "flask", "fastapi", "gin", "fiber",
        "express", "nestjs", "asp.net", "laravel", "rails"
    ]
    
    @classmethod
    def get_prompt_path(cls, prompt_file: str) -> str:
        """Get the full path to a prompt file."""
        return os.path.join(cls.PROMPTS_DIR, prompt_file)
    
    @classmethod
    def get_output_path(cls, filename: str) -> str:
        """Get the full path for output files."""
        os.makedirs(cls.OUTPUT_DIR, exist_ok=True)
        return os.path.join(cls.OUTPUT_DIR, filename)
    
    @classmethod
    def get_temp_path(cls, filename: str) -> str:
        """Get the full path for temporary files."""
        os.makedirs(cls.TEMP_DIR, exist_ok=True)
        return os.path.join(cls.TEMP_DIR, filename)
    
    @classmethod
    def validate_config(cls) -> bool:
        """Validate that all required configuration is present."""
        required_configs = [
            cls.CLAUDE_API_KEY,
            cls.GITHUB_REPO_URL,
        ]
        
        for config in required_configs:
            if not config:
                return False
        
        # Check if prompt files exist
        for prompt_file in cls.PROMPT_FILES:
            if not os.path.exists(cls.get_prompt_path(prompt_file)):
                return False
        
        return True
