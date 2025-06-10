"""
LangGraph State Management for the Agentic Workflow.
"""
from typing import Dict, Any, List, Optional, TypedDict
from pydantic import BaseModel, Field
from enum import Enum

class WorkflowStatus(str, Enum):
    """Workflow execution status."""
    PENDING = "pending"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    FAILED = "failed"
    PAUSED = "paused"

class AgentStatus(str, Enum):
    """Individual agent execution status."""
    NOT_STARTED = "not_started"
    RUNNING = "running"
    COMPLETED = "completed"
    FAILED = "failed"
    SKIPPED = "skipped"

class TechnologyStack(BaseModel):
    """Technology stack detection results."""
    language: str = "unknown"
    framework: str = "unknown"
    database: str = "unknown"
    build_tool: str = "unknown"
    confidence: float = 0.0
    additional_tools: List[str] = Field(default_factory=list)

class PromptInfo(BaseModel):
    """Information about a prompt."""
    order: int
    key: str
    filename: str
    content: str
    type: str

class ValidationResult(BaseModel):
    """Validation result from prevalidation agent."""
    status: str
    confidence: float
    can_proceed: bool
    requires_attention: bool
    details: str = ""

class BusinessContext(BaseModel):
    """Structured business context."""
    domain: str = "unknown"
    entities: List[str] = Field(default_factory=list)
    processes: List[str] = Field(default_factory=list)
    integrations: List[str] = Field(default_factory=list)
    business_rules: List[str] = Field(default_factory=list)
    user_personas: List[str] = Field(default_factory=list)
    success_criteria: Dict[str, Any] = Field(default_factory=dict)
    technical_implications: Dict[str, Any] = Field(default_factory=dict)

class GeneratedFile(BaseModel):
    """Information about a generated code file."""
    path: str
    purpose: str
    content: str
    type: str
    size: int = 0

class CodeStructure(BaseModel):
    """Overall code structure and architecture."""
    directory_structure: str = ""
    core_files: List[Dict[str, Any]] = Field(default_factory=list)
    config_files: List[Dict[str, Any]] = Field(default_factory=list)
    dependencies: List[Dict[str, Any]] = Field(default_factory=list)
    architecture_patterns: List[Dict[str, Any]] = Field(default_factory=list)

class AgentOutput(BaseModel):
    """Output from an individual agent."""
    agent_name: str
    status: AgentStatus
    output: Any = None
    error_message: Optional[str] = None
    execution_time: float = 0.0
    token_usage: Dict[str, int] = Field(default_factory=dict)

class WorkflowState(TypedDict):
    """
    LangGraph state for the agentic workflow.
    This represents the complete state that flows between agents.
    """
    # Workflow metadata
    workflow_id: str
    status: WorkflowStatus
    current_agent: str
    execution_start_time: float
    execution_end_time: Optional[float]
    
    # Input data
    prompts: List[PromptInfo]
    technology_stack: TechnologyStack
    
    # Agent execution tracking
    agent_outputs: Dict[str, AgentOutput]
    agent_sequence: List[str]
    current_agent_index: int
    
    # Agent-specific results
    prevalidation_complete: bool
    validation_result: Optional[ValidationResult]
    
    business_context_complete: bool
    business_context: Optional[BusinessContext]

    code_structure_complete: bool
    project_structure: Optional[Dict[str, Any]]

    code_generation_complete: bool
    code_structure: Optional[CodeStructure]
    generated_files: List[GeneratedFile]
    
    code_evaluation_complete: bool
    evaluation_results: Dict[str, Any]
    
    dependency_evaluation_complete: bool
    dependency_analysis: Dict[str, Any]
    
    guardrails_complete: bool
    guardrails_results: Dict[str, Any]
    
    code_push_complete: bool
    repository_info: Dict[str, Any]
    
    # Error handling
    errors: List[Dict[str, Any]]
    warnings: List[Dict[str, Any]]
    
    # Configuration
    config: Dict[str, Any]
    
    # Final output
    final_output: Optional[Dict[str, Any]]

def create_initial_state(workflow_id: str, prompts: List[PromptInfo], tech_stack: TechnologyStack) -> WorkflowState:
    """Create the initial workflow state."""
    import time
    
    return WorkflowState(
        # Workflow metadata
        workflow_id=workflow_id,
        status=WorkflowStatus.PENDING,
        current_agent="prevalidation",
        execution_start_time=time.time(),
        execution_end_time=None,
        
        # Input data
        prompts=prompts,
        technology_stack=tech_stack,
        
        # Agent execution tracking
        agent_outputs={},
        agent_sequence=[
            "prevalidation",
            "business_context",
            "code_structure",
            "code_generation",
            "code_evaluation",
            "dependency_evaluation",
            "guardrails",
            "code_push"
        ],
        current_agent_index=0,
        
        # Agent-specific results
        prevalidation_complete=False,
        validation_result=None,
        
        business_context_complete=False,
        business_context=None,

        code_structure_complete=False,
        project_structure=None,

        code_generation_complete=False,
        code_structure=None,
        generated_files=[],
        
        code_evaluation_complete=False,
        evaluation_results={},
        
        dependency_evaluation_complete=False,
        dependency_analysis={},
        
        guardrails_complete=False,
        guardrails_results={},
        
        code_push_complete=False,
        repository_info={},
        
        # Error handling
        errors=[],
        warnings=[],
        
        # Configuration
        config={},
        
        # Final output
        final_output=None
    )

def update_agent_status(state: WorkflowState, agent_name: str, status: AgentStatus,
                       output: Any = None, error_message: str = None,
                       execution_time: float = 0.0, token_usage: Dict[str, int] = None) -> WorkflowState:
    """Update the status of a specific agent in the workflow state."""
    import time

    agent_output = AgentOutput(
        agent_name=agent_name,
        status=status,
        output=output,
        error_message=error_message,
        execution_time=execution_time,
        token_usage=token_usage or {}
    )

    state["agent_outputs"][agent_name] = agent_output

    # Update current agent if this agent completed successfully
    if status == AgentStatus.COMPLETED and state["current_agent"] == agent_name:
        current_index = state["current_agent_index"]
        if current_index + 1 < len(state["agent_sequence"]):
            state["current_agent_index"] = current_index + 1
            state["current_agent"] = state["agent_sequence"][current_index + 1]
        else:
            state["status"] = WorkflowStatus.COMPLETED
            state["execution_end_time"] = time.time()

    return state
