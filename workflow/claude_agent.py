"""Claude API agent for code generation."""

import logging
from typing import Dict, Any
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage
from config import WorkflowConfig

logger = logging.getLogger(__name__)


class ClaudeAgent:
    """Agent for interacting with Claude API to generate code."""
    
    def __init__(self, config: WorkflowConfig):
        """Initialize the Claude agent."""
        self.config = config
        self.llm = ChatAnthropic(
            anthropic_api_key=config.anthropic_api_key,
            model="claude-3-5-sonnet-20241022",
            temperature=0.1,
            max_tokens=4000,
        )
    
    def generate_code(self, prompt: str) -> Dict[str, Any]:
        """
        Generate code based on the given prompt.
        
        Args:
            prompt: The prompt describing what code to generate
            
        Returns:
            Dictionary containing generated code and metadata
        """
        try:
            logger.info("Generating code with Claude API")
            
            system_message = SystemMessage(content="""
You are an expert software developer. Generate clean, well-documented, and production-ready code based on the user's prompt.

Requirements:
1. Write complete, functional code
2. Include proper error handling
3. Add comprehensive documentation and comments
4. Follow best practices and coding standards
5. Include example usage if applicable
6. Suggest appropriate file names for the generated code

Format your response as follows:
FILENAME: [suggested filename]
CODE:
[your generated code here]

DESCRIPTION:
[brief description of what the code does]
""")
            
            human_message = HumanMessage(content=prompt)
            
            response = self.llm.invoke([system_message, human_message])
            
            # Parse the response
            content = response.content
            
            # Extract filename, code, and description
            lines = content.split('\n')
            filename = "generated_code.py"  # default
            code = ""
            description = ""
            
            current_section = None
            for line in lines:
                if line.startswith("FILENAME:"):
                    filename = line.replace("FILENAME:", "").strip()
                elif line.startswith("CODE:"):
                    current_section = "code"
                elif line.startswith("DESCRIPTION:"):
                    current_section = "description"
                elif current_section == "code":
                    code += line + "\n"
                elif current_section == "description":
                    description += line + "\n"
            
            result = {
                "filename": filename,
                "code": code.strip(),
                "description": description.strip(),
                "prompt": prompt,
                "raw_response": content
            }
            
            logger.info(f"Successfully generated code: {filename}")
            return result
            
        except Exception as e:
            logger.error(f"Error generating code: {str(e)}")
            raise Exception(f"Failed to generate code: {str(e)}")
    
    def validate_code(self, code: str) -> bool:
        """
        Basic validation of generated code.
        
        Args:
            code: The generated code to validate
            
        Returns:
            True if code appears valid, False otherwise
        """
        try:
            # Basic checks
            if not code.strip():
                return False
            
            # Check for common Python syntax (basic validation)
            if code.strip().startswith(('def ', 'class ', 'import ', 'from ')):
                return True
            
            # Check for other common programming constructs
            if any(keyword in code for keyword in ['function', 'var ', 'const ', 'let ', 'public ', 'private ']):
                return True
                
            return len(code.strip()) > 10  # At least some content
            
        except Exception:
            return False
