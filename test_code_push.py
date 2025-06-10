#!/usr/bin/env python3
"""
Test the CodePushAgent functionality.
"""
import os
import time
from workflow_state import (
    WorkflowState, TechnologyStack, GeneratedFile, 
    create_initial_state, PromptInfo
)
from agents.code_push_agent import CodePushAgent

def test_code_push_agent():
    """Test the CodePushAgent with mock data."""
    print("🧪 Testing CodePushAgent")
    print("=" * 50)
    
    try:
        # Create mock data
        prompts = [
            PromptInfo(
                order=1,
                key="P1",
                filename="test.txt",
                content="Test content",
                type="test"
            )
        ]
        
        tech_stack = TechnologyStack(
            language="java",
            framework="spring-boot",
            database="postgresql",
            build_tool="maven",
            confidence=1.0
        )
        
        # Create initial state
        state = create_initial_state("test-123", prompts, tech_stack)
        
        # Add mock generated files
        generated_files = [
            GeneratedFile(
                path="src/main/java/com/test/Application.java",
                purpose="Main application class",
                content="package com.test;\n\npublic class Application {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}",
                type="java",
                size=100
            ),
            GeneratedFile(
                path="src/main/java/com/test/model/User.java",
                purpose="User entity",
                content="package com.test.model;\n\npublic class User {\n    private String name;\n    // getters and setters\n}",
                type="java",
                size=80
            )
        ]
        
        # Add required state data
        state['generated_files'] = generated_files
        state['guardrails_results'] = {
            'approval_status': 'APPROVED',
            'status': 'PASSED'
        }
        state['dependency_analysis'] = {
            'dependency_file_content': '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.test</groupId>
    <artifactId>test-app</artifactId>
    <version>1.0.0</version>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.7.0</version>
        </dependency>
    </dependencies>
</project>''',
            'total_dependencies': 1
        }
        state['evaluation_results'] = {
            'overall_score': 8
        }
        
        # Test the agent
        agent = CodePushAgent()
        print("✅ CodePushAgent initialized")
        
        # Process the state
        result_state = agent.process(state)
        print("✅ CodePushAgent processing completed")
        
        # Check results
        if result_state.get('code_push_complete'):
            print("✅ Code push marked as complete")
        else:
            print("❌ Code push not completed")
            
        repository_info = result_state.get('repository_info', {})
        if repository_info:
            print(f"✅ Repository info: {repository_info.get('status', 'unknown')}")
            print(f"   Local path: {repository_info.get('local_path', 'unknown')}")
            print(f"   Project name: {repository_info.get('project_name', 'unknown')}")
            print(f"   Files: {repository_info.get('files_created', 0)}")
        else:
            print("❌ No repository info found")
            
        return True
        
    except Exception as e:
        print(f"❌ Error: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_code_push_agent()
    if success:
        print("\n🎉 CodePushAgent test completed successfully!")
    else:
        print("\n💥 CodePushAgent test failed!")
