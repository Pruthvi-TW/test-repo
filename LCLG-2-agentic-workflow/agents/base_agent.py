"""
Base agent class for all specialized agents in the workflow.
"""
from abc import ABC, abstractmethod
from typing import Dict, Any, List
import time
import logging
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage
from config import Config
from workflow_state import WorkflowState, AgentStatus, update_agent_status

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class BaseAgent(ABC):
    """Base class for all agents in the workflow."""
    
    def __init__(self, name: str):
        self.name = name
        self.llm = ChatAnthropic(
            anthropic_api_key=Config.CLAUDE_API_KEY,
            model=Config.CLAUDE_MODEL,
            max_tokens=Config.CLAUDE_MAX_TOKENS
        )
        self.model = Config.CLAUDE_MODEL
        self.max_tokens = Config.CLAUDE_MAX_TOKENS
        
    @abstractmethod
    def process(self, state: WorkflowState) -> WorkflowState:
        """Process the current state and return updated state."""
        pass
    
    def call_claude(self, prompt: str, system_prompt: str = None) -> str:
        """Make a call to Claude API using LangChain with token optimization."""
        try:
            messages = []

            if system_prompt:
                messages.append(SystemMessage(content=system_prompt))

            messages.append(HumanMessage(content=prompt))

            start_time = time.time()
            response = self.llm.invoke(messages)
            execution_time = time.time() - start_time

            # Log execution time and basic info
            logger.info(f"{self.name} - Execution time: {execution_time:.2f}s")

            return response.content

        except Exception as e:
            logger.error(f"Error calling Claude API in {self.name}: {str(e)}")
            raise
    
    def create_system_prompt(self, technology_stack) -> str:
        """Create a system prompt based on detected technology stack."""
        language = technology_stack.language if hasattr(technology_stack, 'language') else 'unknown'
        framework = technology_stack.framework if hasattr(technology_stack, 'framework') else 'unknown'
        database = technology_stack.database if hasattr(technology_stack, 'database') else 'unknown'
        build_tool = technology_stack.build_tool if hasattr(technology_stack, 'build_tool') else 'unknown'
        
        system_prompt = f"""You are an expert software engineer specializing in {language.title()} development.

Technology Stack Context:
- Programming Language: {language}
- Framework: {framework}
- Database: {database}
- Build Tool: {build_tool}

Your expertise includes:
- Writing clean, maintainable, and efficient {language} code
- Following best practices for {framework} development
- Implementing proper error handling and logging
- Creating comprehensive tests
- Managing dependencies with {build_tool}
- Database design and optimization with {database}

Always provide production-ready code that follows industry standards and best practices for the specified technology stack."""
        
        return system_prompt
    
    def validate_state(self, state: WorkflowState, required_keys: List[str]) -> bool:
        """Validate that the state contains all required keys."""
        for key in required_keys:
            if key not in state:
                logger.error(f"{self.name} - Missing required state key: {key}")
                return False
        return True
    
    def log_progress(self, message: str, level: str = "info"):
        """Log progress with agent name prefix."""
        log_func = getattr(logger, level.lower(), logger.info)
        log_func(f"[{self.name}] {message}")
    
    def handle_error(self, state: WorkflowState, error: Exception, context: str = "") -> WorkflowState:
        """Handle errors and update state accordingly."""
        error_msg = f"Error in {self.name}: {str(error)}"
        if context:
            error_msg += f" (Context: {context})"

        logger.error(error_msg)

        # Add error to state
        error_info = {
            'agent': self.name,
            'error_message': error_msg,
            'context': context,
            'timestamp': time.time()
        }
        state["errors"].append(error_info)

        # Update agent status
        state = update_agent_status(
            state,
            self.name,
            AgentStatus.FAILED,
            error_message=error_msg
        )

        return state
