"""
Test script to verify the agentic workflow setup.
"""
import sys
import os

def test_imports():
    """Test if all required modules can be imported."""
    print("Testing imports...")
    
    try:
        from config import Config
        print("✅ Config imported successfully")
    except ImportError as e:
        print(f"❌ Failed to import Config: {e}")
        return False
    
    try:
        from utils.prompt_reader import PromptReader
        print("✅ PromptReader imported successfully")
    except ImportError as e:
        print(f"❌ Failed to import PromptReader: {e}")
        return False
    
    try:
        from workflow_state import WorkflowState, TechnologyStack, PromptInfo
        print("✅ WorkflowState imported successfully")
    except ImportError as e:
        print(f"❌ Failed to import WorkflowState: {e}")
        return False
    
    return True

def test_prompt_reading():
    """Test prompt reading functionality."""
    print("\nTesting prompt reading...")
    
    try:
        from utils.prompt_reader import PromptReader
        
        reader = PromptReader()
        prompts = reader.get_ordered_prompts()
        
        print(f"✅ Found {len(prompts)} prompts")
        for prompt in prompts:
            print(f"   - {prompt['key']}: {prompt['type']}")
        
        # Test technology detection
        tech_stack = reader.detect_technology_stack(prompts)
        print(f"✅ Detected technology stack:")
        print(f"   - Language: {tech_stack.get('language')}")
        print(f"   - Framework: {tech_stack.get('framework')}")
        print(f"   - Database: {tech_stack.get('database')}")
        print(f"   - Confidence: {tech_stack.get('confidence')}")
        
        return True
        
    except Exception as e:
        print(f"❌ Error in prompt reading: {e}")
        return False

def test_config():
    """Test configuration validation."""
    print("\nTesting configuration...")
    
    try:
        from config import Config
        
        print(f"✅ Claude API Key: {'Set' if Config.CLAUDE_API_KEY else 'Not set'}")
        print(f"✅ GitHub Repo URL: {Config.GITHUB_REPO_URL}")
        print(f"✅ Prompts Directory: {Config.PROMPTS_DIR}")
        
        # Test config validation
        is_valid = Config.validate_config()
        print(f"✅ Configuration valid: {is_valid}")
        
        return is_valid
        
    except Exception as e:
        print(f"❌ Error in configuration test: {e}")
        return False

def test_agent_creation():
    """Test agent creation."""
    print("\nTesting agent creation...")
    
    try:
        # Test if we can create agents without errors
        from agents.prevalidation_agent import PrevalidationAgent
        from agents.business_context_agent import BusinessContextAgent
        
        prevalidation_agent = PrevalidationAgent()
        business_agent = BusinessContextAgent()
        
        print("✅ Agents created successfully")
        print(f"   - Prevalidation Agent: {prevalidation_agent.name}")
        print(f"   - Business Context Agent: {business_agent.name}")
        
        return True
        
    except Exception as e:
        print(f"❌ Error creating agents: {e}")
        return False

def main():
    """Run all tests."""
    print("🚀 AGENTIC WORKFLOW SETUP TEST")
    print("=" * 50)
    
    tests = [
        ("Import Test", test_imports),
        ("Configuration Test", test_config),
        ("Prompt Reading Test", test_prompt_reading),
        ("Agent Creation Test", test_agent_creation)
    ]
    
    passed = 0
    total = len(tests)
    
    for test_name, test_func in tests:
        print(f"\n📋 Running {test_name}...")
        try:
            if test_func():
                passed += 1
                print(f"✅ {test_name} PASSED")
            else:
                print(f"❌ {test_name} FAILED")
        except Exception as e:
            print(f"❌ {test_name} FAILED with exception: {e}")
    
    print("\n" + "=" * 50)
    print(f"📊 TEST SUMMARY: {passed}/{total} tests passed")
    
    if passed == total:
        print("🎉 All tests passed! The workflow setup is ready.")
        print("\n💡 Next steps:")
        print("   1. Install dependencies: pip install -r requirements.txt")
        print("   2. Set GitHub token in environment (optional)")
        print("   3. Run the workflow: python main.py")
    else:
        print("⚠️  Some tests failed. Please fix the issues before running the workflow.")
        return 1
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
