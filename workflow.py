"""
LangGraph Workflow for Agentic Code Generation.
"""
import uuid
import time
from typing import Dict, Any, List
from langgraph.graph import StateGraph, END
from langgraph.checkpoint.memory import MemorySaver

from workflow_state import (
    WorkflowState, WorkflowStatus, AgentStatus, TechnologyStack, PromptInfo,
    create_initial_state, update_agent_status
)
from utils.prompt_reader import PromptReader
from agents.prevalidation_agent import PrevalidationAgent
from agents.business_context_agent import BusinessContextAgent
from agents.code_structure_agent import CodeStructureAgent
from agents.code_generation_agent import CodeGenerationAgent
from agents.code_evaluation_agent import CodeEvaluationAgent
from agents.dependency_evaluation_agent import DependencyEvaluationAgent
from agents.guardrails_agent import GuardRailsAgent
from agents.code_push_agent import CodePushAgent

import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class AgenticWorkflow:
    """Main workflow orchestrator using LangGraph."""
    
    def __init__(self):
        self.prompt_reader = PromptReader()
        self.agents = {
            "prevalidation": PrevalidationAgent(),
            "business_context": BusinessContextAgent(),
            "code_structure": CodeStructureAgent(),
            "code_generation": CodeGenerationAgent(),
            "code_evaluation": CodeEvaluationAgent(),
            "dependency_evaluation": DependencyEvaluationAgent(),
            "guardrails": GuardRailsAgent(),
            "code_push": CodePushAgent()
        }
        self.workflow = self._create_workflow()
    
    def _create_workflow(self) -> StateGraph:
        """Create the LangGraph workflow."""
        workflow = StateGraph(WorkflowState)
        
        # Add nodes for each agent
        workflow.add_node("prevalidation_agent", self._prevalidation_node)
        workflow.add_node("business_context_agent", self._business_context_node)
        workflow.add_node("code_structure_agent", self._code_structure_node)
        workflow.add_node("code_generation_agent", self._code_generation_node)
        workflow.add_node("code_evaluation_agent", self._code_evaluation_node)
        workflow.add_node("dependency_evaluation_agent", self._dependency_evaluation_node)
        workflow.add_node("guardrails_agent", self._guardrails_node)
        workflow.add_node("code_push_agent", self._code_push_node)
        
        # Define the workflow edges
        workflow.set_entry_point("prevalidation_agent")

        # Sequential flow with conditional routing
        workflow.add_conditional_edges(
            "prevalidation_agent",
            self._should_continue_after_prevalidation,
            {
                "continue": "business_context_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "business_context_agent",
            self._should_continue_after_business_context,
            {
                "continue": "code_structure_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "code_structure_agent",
            self._should_continue_after_code_structure,
            {
                "continue": "code_generation_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "code_generation_agent",
            self._should_continue_after_code_generation,
            {
                "continue": "code_evaluation_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "code_evaluation_agent",
            self._should_continue_after_code_evaluation,
            {
                "continue": "dependency_evaluation_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "dependency_evaluation_agent",
            self._should_continue_after_dependency_evaluation,
            {
                "continue": "guardrails_agent",
                "stop": END
            }
        )

        workflow.add_conditional_edges(
            "guardrails_agent",
            self._should_continue_after_guardrails,
            {
                "continue": "code_push_agent",
                "stop": END
            }
        )

        workflow.add_edge("code_push_agent", END)
        
        return workflow
    
    def run(self) -> Dict[str, Any]:
        """Run the complete agentic workflow."""
        try:
            logger.info("Starting agentic workflow execution")
            
            # Read and process prompts
            prompts = self._prepare_prompts()
            
            # Detect technology stack
            tech_stack = self._detect_technology_stack(prompts)
            
            # Create initial state
            workflow_id = str(uuid.uuid4())
            initial_state = create_initial_state(workflow_id, prompts, tech_stack)
            
            # Compile and run workflow
            app = self.workflow.compile(checkpointer=MemorySaver())

            # Execute the workflow with proper configuration
            config = {"configurable": {"thread_id": workflow_id}}
            final_state = None
            for state in app.stream(initial_state, config=config):
                final_state = state
                logger.info(f"Workflow step completed: {state.get('current_agent', 'unknown')}")
            
            logger.info("Agentic workflow execution completed")
            return self._format_final_output(final_state)
            
        except Exception as e:
            logger.error(f"Workflow execution failed: {str(e)}")
            return {
                "success": False,
                "error": str(e),
                "timestamp": time.time()
            }
    
    def _prepare_prompts(self) -> List[PromptInfo]:
        """Read and prepare prompts for processing."""
        ordered_prompts = self.prompt_reader.get_ordered_prompts()
        
        prompt_infos = []
        for prompt_data in ordered_prompts:
            prompt_info = PromptInfo(
                order=prompt_data['order'],
                key=prompt_data['key'],
                filename=prompt_data['filename'],
                content=prompt_data['content'],
                type=prompt_data['type']
            )
            prompt_infos.append(prompt_info)
        
        return prompt_infos
    
    def _detect_technology_stack(self, prompts: List[PromptInfo]) -> TechnologyStack:
        """Detect technology stack from prompts."""
        # Convert PromptInfo objects to dict format for compatibility
        prompt_dicts = [
            {
                'key': p.key,
                'content': p.content,
                'type': p.type,
                'order': p.order
            }
            for p in prompts
        ]
        
        tech_detection = self.prompt_reader.detect_technology_stack(prompt_dicts)
        
        return TechnologyStack(
            language=tech_detection.get('language', 'unknown'),
            framework=tech_detection.get('framework', 'unknown'),
            database=tech_detection.get('database', 'unknown'),
            build_tool=tech_detection.get('build_tool', 'unknown'),
            confidence=tech_detection.get('confidence', 0.0)
        )
    
    # Node functions for each agent
    def _prevalidation_node(self, state: WorkflowState) -> WorkflowState:
        """Execute prevalidation agent."""
        return self.agents["prevalidation"].process(state)
    
    def _business_context_node(self, state: WorkflowState) -> WorkflowState:
        """Execute business context agent."""
        return self.agents["business_context"].process(state)

    def _code_structure_node(self, state: WorkflowState) -> WorkflowState:
        """Execute code structure agent."""
        return self.agents["code_structure"].process(state)

    def _code_generation_node(self, state: WorkflowState) -> WorkflowState:
        """Execute code generation agent."""
        return self.agents["code_generation"].process(state)
    
    def _code_evaluation_node(self, state: WorkflowState) -> WorkflowState:
        """Execute code evaluation agent."""
        return self.agents["code_evaluation"].process(state)
    
    def _dependency_evaluation_node(self, state: WorkflowState) -> WorkflowState:
        """Execute dependency evaluation agent."""
        return self.agents["dependency_evaluation"].process(state)
    
    def _guardrails_node(self, state: WorkflowState) -> WorkflowState:
        """Execute guardrails agent."""
        return self.agents["guardrails"].process(state)
    
    def _code_push_node(self, state: WorkflowState) -> WorkflowState:
        """Execute code push agent."""
        return self.agents["code_push"].process(state)
    
    # Conditional routing functions
    def _should_continue_after_prevalidation(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after prevalidation."""
        if state.get("prevalidation_complete") and not state.get("errors"):
            validation_result = state.get("validation_result")
            if validation_result and validation_result.can_proceed:
                return "continue"
        return "stop"
    
    def _should_continue_after_business_context(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after business context."""
        return "continue" if state.get("business_context_complete") and not state.get("errors") else "stop"

    def _should_continue_after_code_structure(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after code structure."""
        return "continue" if state.get("code_structure_complete") and not state.get("errors") else "stop"

    def _should_continue_after_code_generation(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after code generation."""
        return "continue" if state.get("code_generation_complete") and not state.get("errors") else "stop"
    
    def _should_continue_after_code_evaluation(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after code evaluation."""
        return "continue" if state.get("code_evaluation_complete") and not state.get("errors") else "stop"
    
    def _should_continue_after_dependency_evaluation(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after dependency evaluation."""
        return "continue" if state.get("dependency_evaluation_complete") and not state.get("errors") else "stop"
    
    def _should_continue_after_guardrails(self, state: WorkflowState) -> str:
        """Determine if workflow should continue after guardrails."""
        return "continue" if state.get("guardrails_complete") and not state.get("errors") else "stop"
    
    def _format_final_output(self, final_state: WorkflowState) -> Dict[str, Any]:
        """Format the final workflow output."""
        if not final_state:
            return {"success": False, "error": "No final state available"}
        
        return {
            "success": final_state.get("status") == WorkflowStatus.COMPLETED,
            "workflow_id": final_state.get("workflow_id"),
            "technology_stack": final_state.get("technology_stack"),
            "generated_files": final_state.get("generated_files", []),
            "repository_info": final_state.get("repository_info", {}),
            "execution_time": final_state.get("execution_end_time", 0) - final_state.get("execution_start_time", 0),
            "agent_outputs": final_state.get("agent_outputs", {}),
            "errors": final_state.get("errors", []),
            "warnings": final_state.get("warnings", [])
        }
