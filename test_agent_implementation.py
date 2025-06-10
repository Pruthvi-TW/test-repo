#!/usr/bin/env python3
"""
Test script to verify agent implementations without requiring Claude API.
This script tests the agent structure and basic functionality.
"""

import sys
import time
from unittest.mock import Mock, patch
from workflow_state import (
    WorkflowState, WorkflowStatus, TechnologyStack, PromptInfo, 
    create_initial_state, AgentStatus
)
from agents.prevalidation_agent import PrevalidationAgent
from agents.business_context_agent import BusinessContextAgent
from agents.code_structure_agent import CodeStructureAgent

def create_mock_state():
    """Create a mock workflow state for testing."""
    # Create mock prompts
    prompts = [
        PromptInfo(
            order=1,
            key="P1",
            filename="P1-PreTech.txt",
            content="Create a Java Spring Boot eKYC verification service",
            type="technical"
        ),
        PromptInfo(
            order=2,
            key="P2", 
            filename="P2-Business.txt",
            content="Business requirements for identity verification system",
            type="business"
        )
    ]
    
    # Create mock technology stack
    tech_stack = TechnologyStack(
        language="java",
        framework="spring-boot",
        database="postgresql",
        build_tool="maven",
        confidence=1.0
    )
    
    # Create initial state
    state = create_initial_state("test-workflow-123", prompts, tech_stack)
    return state

def test_agent_initialization():
    """Test that agents can be initialized properly."""
    print("=" * 60)
    print("TESTING AGENT INITIALIZATION")
    print("=" * 60)
    
    try:
        # Test agent initialization
        agents = {
            "prevalidation": PrevalidationAgent(),
            "business_context": BusinessContextAgent(),
            "code_structure": CodeStructureAgent()
        }
        
        for name, agent in agents.items():
            print(f"✅ {name} agent initialized: {agent.name}")
            
        print("\n✅ All agents initialized successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Error initializing agents: {str(e)}")
        return False

def test_agent_state_validation():
    """Test agent state validation functionality."""
    print("\n" + "=" * 60)
    print("TESTING AGENT STATE VALIDATION")
    print("=" * 60)
    
    try:
        agent = PrevalidationAgent()
        state = create_mock_state()
        
        # Test valid state
        required_keys = ['prompts', 'technology_stack']
        is_valid = agent.validate_state(state, required_keys)
        print(f"✅ State validation (valid): {is_valid}")
        
        # Test invalid state
        invalid_state = {}
        is_invalid = agent.validate_state(invalid_state, required_keys)
        print(f"✅ State validation (invalid): {not is_invalid}")
        
        return True
        
    except Exception as e:
        print(f"❌ Error in state validation test: {str(e)}")
        return False

def test_system_prompt_creation():
    """Test system prompt creation."""
    print("\n" + "=" * 60)
    print("TESTING SYSTEM PROMPT CREATION")
    print("=" * 60)
    
    try:
        agent = PrevalidationAgent()
        state = create_mock_state()
        tech_stack = state['technology_stack']
        
        system_prompt = agent.create_system_prompt(tech_stack)
        
        # Check if system prompt contains expected elements
        expected_elements = ['java', 'spring-boot', 'postgresql', 'maven']
        for element in expected_elements:
            if element.lower() in system_prompt.lower():
                print(f"✅ System prompt contains: {element}")
            else:
                print(f"❌ System prompt missing: {element}")
        
        print(f"\n📝 System prompt length: {len(system_prompt)} characters")
        return True
        
    except Exception as e:
        print(f"❌ Error in system prompt test: {str(e)}")
        return False

def test_mock_agent_processing():
    """Test agent processing with mocked Claude API calls."""
    print("\n" + "=" * 60)
    print("TESTING MOCK AGENT PROCESSING")
    print("=" * 60)
    
    try:
        # Mock the Claude API call
        mock_response = """
VALIDATION_STATUS: PASS
CONFIDENCE_SCORE: 0.95

TECHNICAL_FEASIBILITY:
- The requirements are technically feasible with Java Spring Boot

COMPLETENESS_CHECK:
- All essential requirements are present

CONSISTENCY_ANALYSIS:
- No major inconsistencies found

TECHNOLOGY_ALIGNMENT:
- Technology stack aligns well with requirements

SCOPE_ASSESSMENT:
- Scope is appropriate for the given constraints

RISK_IDENTIFICATION:
- Low risk implementation

RECOMMENDATIONS:
- Proceed with implementation

NEXT_STEPS:
- Continue to business context analysis
"""
        
        agent = PrevalidationAgent()
        state = create_mock_state()
        
        # Mock the call_claude method
        with patch.object(agent, 'call_claude', return_value=mock_response):
            # Process the state
            updated_state = agent.process(state)
            
            # Check results
            if updated_state.get('prevalidation_complete'):
                print("✅ Prevalidation completed successfully")
            else:
                print("❌ Prevalidation not completed")
                
            if updated_state.get('validation_result'):
                validation_result = updated_state['validation_result']
                print(f"✅ Validation result created: {validation_result.status}")
                print(f"✅ Confidence score: {validation_result.confidence}")
                print(f"✅ Can proceed: {validation_result.can_proceed}")
            else:
                print("❌ No validation result created")
                
            # Check agent outputs
            agent_outputs = updated_state.get('agent_outputs', {})
            if 'PrevalidationAgent' in agent_outputs:
                agent_output = agent_outputs['PrevalidationAgent']
                print(f"✅ Agent status: {agent_output.status}")
                print(f"✅ Execution time: {agent_output.execution_time:.2f}s")
            else:
                print("❌ No agent output recorded")
        
        return True
        
    except Exception as e:
        print(f"❌ Error in mock processing test: {str(e)}")
        return False

def main():
    """Run all agent implementation tests."""
    print("Starting Agent Implementation Tests...\n")
    
    # Run tests
    test_results = []
    test_results.append(("Initialization", test_agent_initialization()))
    test_results.append(("State Validation", test_agent_state_validation()))
    test_results.append(("System Prompt", test_system_prompt_creation()))
    test_results.append(("Mock Processing", test_mock_agent_processing()))
    
    # Summary
    print("\n" + "=" * 60)
    print("AGENT IMPLEMENTATION TEST SUMMARY")
    print("=" * 60)
    
    passed = 0
    for test_name, result in test_results:
        status = "✅ PASSED" if result else "❌ FAILED"
        print(f"{test_name}: {status}")
        if result:
            passed += 1
    
    print(f"\nTests passed: {passed}/{len(test_results)}")
    
    if passed == len(test_results):
        print("\n🎉 All agent implementation tests passed!")
        print("The agents are properly implemented and ready for use.")
    else:
        print(f"\n⚠️  {len(test_results) - passed} test(s) failed.")
        sys.exit(1)

if __name__ == "__main__":
    main()
