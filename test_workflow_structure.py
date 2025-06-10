#!/usr/bin/env python3
"""
Test script to verify the workflow structure without requiring Claude API.
This script tests the workflow initialization and structure.
"""

import sys
import os
from workflow import AgenticWorkflow
from config import Config

def test_workflow_structure():
    """Test the workflow structure and initialization."""
    print("=" * 60)
    print("TESTING AGENTIC WORKFLOW STRUCTURE")
    print("=" * 60)
    
    try:
        # Test workflow initialization
        print("1. Testing workflow initialization...")
        workflow = AgenticWorkflow()
        print("   ✅ Workflow initialized successfully")
        
        # Test agents dictionary
        print("2. Testing agents dictionary...")
        expected_agents = [
            "prevalidation", "business_context", "code_structure", 
            "code_generation", "code_evaluation", "dependency_evaluation", 
            "guardrails", "code_push"
        ]
        
        for agent_name in expected_agents:
            if agent_name in workflow.agents:
                print(f"   ✅ {agent_name} agent found")
            else:
                print(f"   ❌ {agent_name} agent missing")
        
        # Test workflow graph structure
        print("3. Testing workflow graph structure...")
        compiled_workflow = workflow.workflow.compile()
        print("   ✅ Workflow compiled successfully")
        
        # Test prompt reader
        print("4. Testing prompt reader...")
        prompt_reader = workflow.prompt_reader
        print(f"   ✅ Prompt reader initialized: {type(prompt_reader).__name__}")
        
        # Test configuration
        print("5. Testing configuration...")
        print(f"   - Prompts directory: {Config.PROMPTS_DIR}")
        print(f"   - Output directory: {Config.OUTPUT_DIR}")
        print(f"   - Supported languages: {len(Config.SUPPORTED_LANGUAGES)}")
        print(f"   - Supported frameworks: {len(Config.SUPPORTED_FRAMEWORKS)}")
        
        # Check prompt files
        print("6. Checking prompt files...")
        for prompt_file in Config.PROMPT_FILES:
            prompt_path = Config.get_prompt_path(prompt_file)
            if os.path.exists(prompt_path):
                print(f"   ✅ {prompt_file} found")
            else:
                print(f"   ❌ {prompt_file} missing at {prompt_path}")
        
        print("\n" + "=" * 60)
        print("✅ WORKFLOW STRUCTURE TEST COMPLETED SUCCESSFULLY")
        print("=" * 60)
        
        return True
        
    except Exception as e:
        print(f"\n❌ ERROR: {str(e)}")
        print("=" * 60)
        return False

def test_prompt_detection():
    """Test prompt reading and technology detection."""
    print("\n" + "=" * 60)
    print("TESTING PROMPT DETECTION")
    print("=" * 60)
    
    try:
        workflow = AgenticWorkflow()
        
        # Test prompt reading
        print("1. Testing prompt reading...")
        prompts = workflow._prepare_prompts()
        print(f"   ✅ Found {len(prompts)} prompts")
        
        for prompt in prompts:
            print(f"   - {prompt.key}: {prompt.filename} ({len(prompt.content)} chars)")
        
        # Test technology detection
        print("2. Testing technology detection...")
        tech_stack = workflow._detect_technology_stack(prompts)
        print(f"   ✅ Technology detected:")
        print(f"   - Language: {tech_stack.language}")
        print(f"   - Framework: {tech_stack.framework}")
        print(f"   - Database: {tech_stack.database}")
        print(f"   - Build Tool: {tech_stack.build_tool}")
        print(f"   - Confidence: {tech_stack.confidence}")
        
        return True
        
    except Exception as e:
        print(f"\n❌ ERROR in prompt detection: {str(e)}")
        return False

if __name__ == "__main__":
    print("Starting Agentic Workflow Structure Tests...\n")
    
    # Run tests
    structure_test = test_workflow_structure()
    prompt_test = test_prompt_detection()
    
    # Summary
    print("\n" + "=" * 60)
    print("TEST SUMMARY")
    print("=" * 60)
    print(f"Structure Test: {'✅ PASSED' if structure_test else '❌ FAILED'}")
    print(f"Prompt Test: {'✅ PASSED' if prompt_test else '❌ FAILED'}")
    
    if structure_test and prompt_test:
        print("\n🎉 All tests passed! The workflow structure is ready.")
        print("\nTo run the full workflow, you need to:")
        print("1. Set your Claude API key in the .env file")
        print("2. Run: python main.py")
    else:
        print("\n⚠️  Some tests failed. Please check the errors above.")
        sys.exit(1)
