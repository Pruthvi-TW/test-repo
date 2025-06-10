"""
Main entry point for the Agentic Code Generation Workflow.
"""
import json
import time
import logging
from workflow import AgenticWorkflow
from config import Config

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('workflow.log'),
        logging.StreamHandler()
    ]
)

logger = logging.getLogger(__name__)

def main():
    """Main function to run the agentic workflow."""
    try:
        logger.info("=" * 60)
        logger.info("STARTING AGENTIC CODE GENERATION WORKFLOW")
        logger.info("=" * 60)
        
        # Validate configuration
        if not Config.validate_config():
            logger.error("Configuration validation failed. Please check your config.")
            return
        
        logger.info("Configuration validated successfully")
        
        # Create and run workflow
        workflow = AgenticWorkflow()
        
        logger.info("Workflow created, starting execution...")
        start_time = time.time()
        
        # Execute the workflow
        result = workflow.run()
        
        execution_time = time.time() - start_time
        
        # Log results
        logger.info("=" * 60)
        logger.info("WORKFLOW EXECUTION COMPLETED")
        logger.info("=" * 60)
        logger.info(f"Execution time: {execution_time:.2f} seconds")
        logger.info(f"Success: {result.get('success', False)}")
        
        if result.get('success'):
            logger.info(f"Workflow ID: {result.get('workflow_id')}")
            logger.info(f"Technology Stack: {result.get('technology_stack')}")
            logger.info(f"Generated Files: {len(result.get('generated_files', []))}")
            logger.info(f"Repository: {result.get('repository_info', {}).get('repository_url', 'N/A')}")
            
            # Print summary
            print_workflow_summary(result)
            
        else:
            logger.error(f"Workflow failed: {result.get('error', 'Unknown error')}")
            
        # Save detailed results
        save_results(result)
        
    except Exception as e:
        logger.error(f"Fatal error in main workflow: {str(e)}")
        raise

def print_workflow_summary(result):
    """Print a summary of the workflow execution."""
    print("\n" + "=" * 80)
    print("🚀 AGENTIC WORKFLOW EXECUTION SUMMARY")
    print("=" * 80)
    
    # Basic info
    print(f"✅ Status: {'SUCCESS' if result.get('success') else 'FAILED'}")
    print(f"🆔 Workflow ID: {result.get('workflow_id', 'Unknown')}")
    print(f"⏱️  Execution Time: {result.get('execution_time', 0):.2f} seconds")
    
    # Technology stack
    tech_stack = result.get('technology_stack', {})
    if hasattr(tech_stack, 'language'):
        print(f"\n🛠️  Technology Stack:")
        print(f"   Language: {tech_stack.language}")
        print(f"   Framework: {tech_stack.framework}")
        print(f"   Database: {tech_stack.database}")
        print(f"   Build Tool: {tech_stack.build_tool}")
        print(f"   Confidence: {tech_stack.confidence}")
    
    # Generated files
    generated_files = result.get('generated_files', [])
    print(f"\n📁 Generated Files: {len(generated_files)}")
    for i, file_obj in enumerate(generated_files[:5], 1):  # Show first 5 files
        if hasattr(file_obj, 'path'):
            print(f"   {i}. {file_obj.path} ({file_obj.type})")
    
    if len(generated_files) > 5:
        print(f"   ... and {len(generated_files) - 5} more files")
    
    # Repository info
    repo_info = result.get('repository_info', {})
    if repo_info:
        print(f"\n📦 Repository:")
        print(f"   URL: {repo_info.get('repository_url', 'N/A')}")
        print(f"   Status: {repo_info.get('status', 'Unknown')}")
        print(f"   Local Path: {repo_info.get('local_path', 'N/A')}")
    
    # Agent execution summary
    agent_outputs = result.get('agent_outputs', {})
    if agent_outputs:
        print(f"\n🤖 Agent Execution Summary:")
        for agent_name, output in agent_outputs.items():
            if hasattr(output, 'status'):
                status_emoji = "✅" if output.status == "completed" else "❌"
                print(f"   {status_emoji} {agent_name}: {output.status} ({output.execution_time:.2f}s)")
    
    # Errors and warnings
    errors = result.get('errors', [])
    warnings = result.get('warnings', [])
    
    if errors:
        print(f"\n❌ Errors ({len(errors)}):")
        for error in errors[:3]:  # Show first 3 errors
            print(f"   - {error.get('error_message', 'Unknown error')}")
    
    if warnings:
        print(f"\n⚠️  Warnings ({len(warnings)}):")
        for warning in warnings[:3]:  # Show first 3 warnings
            print(f"   - {warning}")
    
    print("=" * 80)
    print("🎉 Workflow execution completed! Check the logs for detailed information.")
    print("=" * 80 + "\n")

def save_results(result):
    """Save workflow results to a JSON file."""
    try:
        # Convert result to JSON-serializable format
        json_result = convert_to_json_serializable(result)
        
        # Save to file
        timestamp = time.strftime("%Y%m%d_%H%M%S")
        filename = f"workflow_result_{timestamp}.json"
        filepath = Config.get_output_path(filename)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(json_result, f, indent=2, ensure_ascii=False)
        
        logger.info(f"Results saved to: {filepath}")
        
    except Exception as e:
        logger.error(f"Failed to save results: {str(e)}")

def convert_to_json_serializable(obj):
    """Convert objects to JSON-serializable format."""
    if hasattr(obj, '__dict__'):
        return {key: convert_to_json_serializable(value) for key, value in obj.__dict__.items()}
    elif isinstance(obj, dict):
        return {key: convert_to_json_serializable(value) for key, value in obj.items()}
    elif isinstance(obj, list):
        return [convert_to_json_serializable(item) for item in obj]
    elif isinstance(obj, (str, int, float, bool, type(None))):
        return obj
    else:
        return str(obj)

if __name__ == "__main__":
    main()
