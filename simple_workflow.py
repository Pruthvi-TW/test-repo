"""
Simplified Agentic Workflow without LangGraph dependency issues.
Uses direct agent orchestration with LangChain integration.
"""
import uuid
import time
import logging
from typing import Dict, Any, List

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

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class SimpleAgenticWorkflow:
    """Simplified workflow orchestrator using direct agent execution."""
    
    def __init__(self):
        self.prompt_reader = PromptReader()
        self.agents = [
            PrevalidationAgent(),
            BusinessContextAgent(),
            CodeStructureAgent(),
            CodeGenerationAgent(),
            CodeEvaluationAgent(),
            DependencyEvaluationAgent(),
            GuardRailsAgent(),
            CodePushAgent()
        ]
    
    def run(self) -> Dict[str, Any]:
        """Run the complete agentic workflow."""
        try:
            logger.info("🚀 Starting Agentic Code Generation Workflow")
            logger.info("=" * 60)
            
            # Read and process prompts
            prompts = self._prepare_prompts()
            logger.info(f"📋 Loaded {len(prompts)} prompts: {[p.key for p in prompts]}")
            
            # Detect technology stack
            tech_stack = self._detect_technology_stack(prompts)
            logger.info(f"🛠️  Detected Technology Stack:")
            logger.info(f"   Language: {tech_stack.language}")
            logger.info(f"   Framework: {tech_stack.framework}")
            logger.info(f"   Database: {tech_stack.database}")
            logger.info(f"   Build Tool: {tech_stack.build_tool}")
            logger.info(f"   Confidence: {tech_stack.confidence}")
            
            # Create initial state
            workflow_id = str(uuid.uuid4())
            state = create_initial_state(workflow_id, prompts, tech_stack)
            state["status"] = WorkflowStatus.IN_PROGRESS
            
            logger.info(f"🆔 Workflow ID: {workflow_id}")
            logger.info("=" * 60)
            
            # Execute agents sequentially
            for i, agent in enumerate(self.agents, 1):
                logger.info(f"🤖 [{i}/{len(self.agents)}] Executing {agent.name}...")
                
                start_time = time.time()
                
                try:
                    # Execute agent
                    state = agent.process(state)
                    
                    execution_time = time.time() - start_time
                    
                    # Check for errors
                    if state.get("errors") and len(state["errors"]) > 0:
                        last_error = state["errors"][-1]
                        if last_error.get("agent") == agent.name:
                            logger.error(f"❌ {agent.name} failed: {last_error.get('error_message')}")
                            break
                    
                    logger.info(f"✅ {agent.name} completed successfully ({execution_time:.2f}s)")
                    
                    # Log agent-specific results
                    self._log_agent_results(agent.name, state)
                    
                except Exception as e:
                    logger.error(f"❌ {agent.name} failed with exception: {str(e)}")
                    state["errors"].append({
                        "agent": agent.name,
                        "error_message": str(e),
                        "timestamp": time.time()
                    })
                    break
                
                logger.info("-" * 40)
            
            # Finalize workflow
            state["status"] = WorkflowStatus.COMPLETED if not state.get("errors") else WorkflowStatus.FAILED
            state["execution_end_time"] = time.time()
            
            logger.info("=" * 60)
            logger.info("🎉 Workflow Execution Completed!")
            
            return self._format_final_output(state)
            
        except Exception as e:
            logger.error(f"💥 Workflow execution failed: {str(e)}")
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
    
    def _log_agent_results(self, agent_name: str, state: WorkflowState):
        """Log specific results for each agent."""
        
        if agent_name == "PrevalidationAgent":
            validation_result = state.get("validation_result")
            if validation_result:
                logger.info(f"   📊 Validation Status: {validation_result.status}")
                logger.info(f"   📊 Can Proceed: {validation_result.can_proceed}")
        
        elif agent_name == "BusinessContextAgent":
            business_context = state.get("business_context")
            if business_context:
                logger.info(f"   🏢 Business Domain: {business_context.domain}")
                logger.info(f"   📦 Entities: {len(business_context.entities)} found")
        
        elif agent_name == "CodeStructureAgent":
            project_structure = state.get("project_structure")
            if project_structure:
                logger.info(f"   🏗️  Project Structure: {project_structure.get('language')} {project_structure.get('framework')}")
                logger.info(f"   📁 Core Files: {len(project_structure.get('core_files', []))} planned")
        
        elif agent_name == "CodeGenerationAgent":
            generated_files = state.get("generated_files", [])
            logger.info(f"   📝 Generated Files: {len(generated_files)}")
            for file_obj in generated_files[:3]:  # Show first 3 files
                if isinstance(file_obj, dict):
                    logger.info(f"      - {file_obj.get('path', 'unknown')} ({file_obj.get('type', 'unknown')})")
                else:
                    logger.info(f"      - {file_obj.path} ({file_obj.type})")
        
        elif agent_name == "CodeEvaluationAgent":
            evaluation_results = state.get("evaluation_results", {})
            overall_score = evaluation_results.get("overall_score", 0)
            logger.info(f"   📊 Code Quality Score: {overall_score}/10")
        
        elif agent_name == "DependencyEvaluationAgent":
            dependency_analysis = state.get("dependency_analysis", {})
            total_deps = dependency_analysis.get("total_dependencies", 0)
            logger.info(f"   📦 Dependencies Analyzed: {total_deps}")
        
        elif agent_name == "GuardRailsAgent":
            guardrails_results = state.get("guardrails_results", {})
            approval_status = guardrails_results.get("approval_status", "UNKNOWN")
            logger.info(f"   🛡️  GuardRails Status: {approval_status}")
        
        elif agent_name == "CodePushAgent":
            repository_info = state.get("repository_info", {})
            repo_status = repository_info.get("status", "unknown")
            logger.info(f"   🚀 Repository Status: {repo_status}")
            if repository_info.get("repository_url"):
                logger.info(f"   🔗 Repository URL: {repository_info['repository_url']}")
    
    def _format_final_output(self, final_state: WorkflowState) -> Dict[str, Any]:
        """Format the final workflow output."""
        if not final_state:
            return {"success": False, "error": "No final state available"}
        
        success = final_state.get("status") == WorkflowStatus.COMPLETED
        
        result = {
            "success": success,
            "workflow_id": final_state.get("workflow_id"),
            "technology_stack": final_state.get("technology_stack"),
            "generated_files": final_state.get("generated_files", []),
            "repository_info": final_state.get("repository_info", {}),
            "execution_time": final_state.get("execution_end_time", 0) - final_state.get("execution_start_time", 0),
            "agent_outputs": final_state.get("agent_outputs", {}),
            "errors": final_state.get("errors", []),
            "warnings": final_state.get("warnings", [])
        }
        
        # Add summary statistics
        result["summary"] = {
            "total_agents": len(self.agents),
            "successful_agents": len([a for a in self.agents if a.name in final_state.get("agent_outputs", {})]),
            "total_files_generated": len(final_state.get("generated_files", [])),
            "total_errors": len(final_state.get("errors", [])),
            "workflow_duration": result["execution_time"]
        }
        
        return result

def main():
    """Main function to run the simplified workflow."""
    workflow = SimpleAgenticWorkflow()
    result = workflow.run()
    
    # Print final summary
    print("\n" + "=" * 80)
    print("🎯 FINAL WORKFLOW SUMMARY")
    print("=" * 80)
    print(f"✅ Success: {result.get('success')}")
    print(f"🆔 Workflow ID: {result.get('workflow_id')}")
    print(f"⏱️  Total Time: {result.get('execution_time', 0):.2f} seconds")
    print(f"📁 Files Generated: {len(result.get('generated_files', []))}")
    print(f"❌ Errors: {len(result.get('errors', []))}")
    
    if result.get('repository_info', {}).get('repository_url'):
        print(f"🔗 Repository: {result['repository_info']['repository_url']}")
    
    print("=" * 80)
    
    return result

if __name__ == "__main__":
    main()
