#!/usr/bin/env python3
"""
Debug version of the main workflow with detailed error reporting.
"""
import sys
import traceback
import time
from workflow import AgenticWorkflow
from config import Config

def main():
    """Run the workflow with detailed debugging."""
    print("🚀 Starting Agentic Workflow with Debug Mode")
    print("=" * 60)
    
    try:
        # Step 1: Validate configuration
        print("Step 1: Validating configuration...")
        if not Config.validate_config():
            print("❌ Configuration validation failed")
            return False
        print("✅ Configuration validated")
        
        # Step 2: Initialize workflow
        print("\nStep 2: Initializing workflow...")
        workflow = AgenticWorkflow()
        print("✅ Workflow initialized")
        
        # Step 3: Check agents
        print(f"\nStep 3: Checking agents ({len(workflow.agents)} found)...")
        for name in workflow.agents.keys():
            print(f"   - {name}")
        print("✅ All agents loaded")
        
        # Step 4: Test prompt reading
        print("\nStep 4: Testing prompt reading...")
        prompts = workflow._prepare_prompts()
        print(f"✅ Found {len(prompts)} prompts")
        
        # Step 5: Test technology detection
        print("\nStep 5: Testing technology detection...")
        tech_stack = workflow._detect_technology_stack(prompts)
        print(f"✅ Technology detected: {tech_stack.language}/{tech_stack.framework}")
        
        # Step 6: Run workflow
        print("\nStep 6: Running full workflow...")
        print("This may take a few minutes as it calls Claude API...")
        
        start_time = time.time()
        result = workflow.run()
        execution_time = time.time() - start_time
        
        print(f"\n✅ Workflow completed in {execution_time:.2f} seconds")
        print(f"Success: {result.get('success', False)}")
        
        if result.get('success'):
            print(f"Generated files: {len(result.get('generated_files', []))}")
            print(f"Repository: {result.get('repository_info', {}).get('repository_url', 'N/A')}")
        else:
            print(f"Error: {result.get('error', 'Unknown error')}")
            
        return result.get('success', False)
        
    except KeyboardInterrupt:
        print("\n⚠️ Workflow interrupted by user")
        return False
    except Exception as e:
        print(f"\n❌ Fatal error: {str(e)}")
        print("\nFull traceback:")
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = main()
    if success:
        print("\n🎉 Workflow completed successfully!")
    else:
        print("\n💥 Workflow failed!")
        sys.exit(1)
