"""
Code Generation Agent - Generates code based on requirements and technology stack.
"""
import time
from typing import Dict, Any, List
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, GeneratedFile, CodeStructure, update_agent_status

class CodeGenerationAgent(BaseAgent):
    """Agent responsible for generating code based on requirements."""
    
    def __init__(self):
        super().__init__("CodeGenerationAgent")
    
    def process(self, state: Dict[str, Any]) -> Dict[str, Any]:
        """Generate code based on business context and requirements."""
        try:
            self.log_progress("Starting code generation process")
            
            # Validate required state
            required_keys = ['prompts', 'technology_stack', 'business_context', 'project_structure']
            if not self.validate_state(state, required_keys):
                return self.handle_error(ValueError("Missing required state keys"), "state_validation")
            
            prompts = state['prompts']
            tech_stack = state['technology_stack']
            business_context = state['business_context']
            project_structure = state['project_structure']

            # Use the project structure from CodeStructureAgent
            code_structure = project_structure
            
            # Generate individual code files using the structured approach
            generated_files = self._generate_code_files_from_structure(code_structure, tech_stack, business_context)
            
            # Update state
            state.update({
                'code_generation_complete': True,
                'code_structure': code_structure,
                'generated_files': generated_files,
                'code_generation_agent_output': {
                    'structure': code_structure,
                    'files': generated_files
                }
            })
            
            self.log_progress(f"Code generation completed. Generated {len(generated_files)} files")
            
            return state
            
        except Exception as e:
            return self.handle_error(e, "code_generation_process")
    
    def _generate_code_structure(self, prompts: list, tech_stack: Dict[str, Any], business_context: Dict[str, Any]) -> Dict[str, Any]:
        """Generate the overall code structure and architecture."""
        prompt_content = "\n\n".join([f"=== {p['key']}: {p['type']} ===\n{p['content']}" for p in prompts])
        
        structure_prompt = f"""
Based on the following requirements and business context, design a complete project structure for a {tech_stack.get('language', 'generic')} application using {tech_stack.get('framework', 'appropriate framework')}:

TECHNOLOGY STACK:
- Language: {tech_stack.get('language')}
- Framework: {tech_stack.get('framework')}
- Database: {tech_stack.get('database')}
- Build Tool: {tech_stack.get('build_tool')}

BUSINESS CONTEXT:
- Domain: {business_context.get('domain', 'Unknown')}
- Entities: {', '.join(business_context.get('entities', [])[:5])}
- Processes: {len(business_context.get('processes', []))} business processes

REQUIREMENTS:
{prompt_content}

Please provide a complete project structure including:

1. **Directory Structure**: Complete folder hierarchy
2. **Core Files**: Main application files and their purposes
3. **Configuration Files**: Build, deployment, and application config
4. **Test Structure**: Testing framework and test organization
5. **Documentation**: README, API docs, etc.
6. **Dependencies**: Required libraries and frameworks

Format your response as:

PROJECT_STRUCTURE:
```
project-root/
├── src/
│   ├── main/
│   │   ├── [language-specific structure]
│   └── test/
├── config/
├── docs/
└── [other directories]
```

CORE_FILES:
- File 1: [path] - [purpose]
- File 2: [path] - [purpose]
[Continue for all core files]

CONFIGURATION_FILES:
- Config 1: [path] - [purpose]
- Config 2: [path] - [purpose]

DEPENDENCIES:
- Dependency 1: [name] - [purpose]
- Dependency 2: [name] - [purpose]

ARCHITECTURE_PATTERNS:
- Pattern 1: [name] - [application]
- Pattern 2: [name] - [application]
"""
        
        system_prompt = self.create_system_prompt(tech_stack)
        structure_response = self.call_claude(structure_prompt, system_prompt)
        
        return self._parse_structure_response(structure_response, tech_stack)
    
    def _generate_code_files(self, code_structure: Dict[str, Any], tech_stack: Dict[str, Any], business_context: Dict[str, Any]) -> List[Dict[str, Any]]:
        """Generate individual code files based on the structure."""
        generated_files = []
        
        # Get the list of files to generate from the structure
        files_to_generate = code_structure.get('core_files', [])
        
        for file_info in files_to_generate[:10]:  # Limit to first 10 files to manage token usage
            try:
                file_content = self._generate_single_file(file_info, tech_stack, business_context)
                
                generated_files.append({
                    'path': file_info.get('path', 'unknown'),
                    'purpose': file_info.get('purpose', 'Unknown purpose'),
                    'content': file_content,
                    'type': self._determine_file_type(file_info.get('path', ''))
                })
                
                self.log_progress(f"Generated file: {file_info.get('path', 'unknown')}")
                
            except Exception as e:
                self.log_progress(f"Failed to generate file {file_info.get('path', 'unknown')}: {str(e)}", "warning")
                continue
        
        return generated_files

    def _generate_code_files_from_structure(self, project_structure: Dict[str, Any], tech_stack, business_context) -> List[Dict[str, Any]]:
        """Generate code files using the structured approach from CodeStructureAgent."""
        from utils.code_templates import CodeTemplates

        generated_files = []

        # Get core files from project structure
        core_files = project_structure.get('core_files', [])
        self.log_progress(f"Project structure language: {project_structure.get('language')}")
        self.log_progress(f"Core files count: {len(core_files)}")

        for file_info in core_files[:8]:  # Limit to 8 files for token efficiency
            try:
                file_path = file_info.get('path', '')
                file_purpose = file_info.get('purpose', '')
                template_name = file_info.get('template', '')

                # Prepare context for template
                context = {
                    'package': project_structure.get('base_package', 'com.example'),
                    'app_name': business_context.domain.replace(' ', '').lower(),
                    'entity_name': self._extract_entity_name(file_path),
                    'class_name': self._extract_class_name(file_path),
                    'module_name': project_structure.get('module_name', 'github.com/company/app'),
                    'language': tech_stack.language,
                    'framework': tech_stack.framework,
                    'database': tech_stack.database,
                    'build_tool': tech_stack.build_tool,
                    'domain': business_context.domain,
                    'entities': business_context.entities[:3]  # First 3 entities
                }

                # Generate file content using template or Claude
                if template_name and template_name != '':
                    self.log_progress(f"Using template '{template_name}' for {file_path}")
                    file_content = CodeTemplates.get_template(template_name, context)
                else:
                    self.log_progress(f"Using Claude generation for {file_path}")
                    file_content = self._generate_single_file_with_claude(file_info, tech_stack, business_context)

                generated_files.append({
                    'path': file_path,
                    'purpose': file_purpose,
                    'content': file_content,
                    'type': self._determine_file_type(file_path),
                    'template_used': template_name or 'claude_generated'
                })

                self.log_progress(f"Generated file: {file_path} ({template_name or 'claude'})")

            except Exception as e:
                self.log_progress(f"Failed to generate file {file_info.get('path', 'unknown')}: {str(e)}", "warning")
                continue

        return generated_files

    def _extract_entity_name(self, file_path: str) -> str:
        """Extract entity name from file path."""
        if '/model/' in file_path or '/entity/' in file_path:
            filename = file_path.split('/')[-1]
            return filename.split('.')[0]
        return 'Entity'

    def _extract_class_name(self, file_path: str) -> str:
        """Extract class name from file path."""
        filename = file_path.split('/')[-1]
        return filename.split('.')[0]

    def _generate_single_file_with_claude(self, file_info: Dict[str, Any], tech_stack, business_context) -> str:
        """Generate file content using Claude when no template is available."""
        file_path = file_info.get('path', '')
        file_purpose = file_info.get('purpose', '')

        generation_prompt = f"""
Generate complete, production-ready code for the following file:

FILE: {file_path}
PURPOSE: {file_purpose}

TECHNOLOGY STACK:
- Language: {tech_stack.language}
- Framework: {tech_stack.framework}
- Database: {tech_stack.database}
- Build Tool: {tech_stack.build_tool}

BUSINESS CONTEXT:
- Domain: {business_context.domain}
- Key Entities: {', '.join(business_context.entities[:3])}

REQUIREMENTS:
1. Follow best practices for {tech_stack.language} and {tech_stack.framework}
2. Include proper error handling and logging
3. Add comprehensive comments and documentation
4. Implement security best practices
5. Make the code testable and maintainable
6. Include input validation where appropriate
7. Follow industry coding standards
8. Use proper design patterns
9. Include proper imports and dependencies

Please provide ONLY the file content without any explanations or markdown formatting.
The response should be ready to save directly to the file.
"""

        system_prompt = f"""You are an expert {tech_stack.language} developer specializing in {tech_stack.framework}.
Generate clean, production-ready code that follows industry best practices and Augment-level quality standards.
Focus on:
- Code quality and maintainability
- Proper error handling and logging
- Security considerations
- Performance optimization
- Comprehensive documentation
- Testability
- Industry-standard architecture patterns"""

        return self.call_claude(generation_prompt, system_prompt)

    def _generate_single_file(self, file_info: Dict[str, Any], tech_stack: Dict[str, Any], business_context: Dict[str, Any]) -> str:
        """Generate content for a single file."""
        file_path = file_info.get('path', '')
        file_purpose = file_info.get('purpose', '')
        
        generation_prompt = f"""
Generate complete, production-ready code for the following file:

FILE: {file_path}
PURPOSE: {file_purpose}

TECHNOLOGY STACK:
- Language: {tech_stack.get('language')}
- Framework: {tech_stack.get('framework')}
- Database: {tech_stack.get('database')}
- Build Tool: {tech_stack.get('build_tool')}

BUSINESS CONTEXT:
- Domain: {business_context.get('domain', 'Unknown')}
- Key Entities: {', '.join(business_context.get('entities', [])[:3])}

REQUIREMENTS:
1. Follow best practices for {tech_stack.get('language')} and {tech_stack.get('framework')}
2. Include proper error handling and logging
3. Add comprehensive comments and documentation
4. Implement security best practices
5. Make the code testable and maintainable
6. Include input validation where appropriate
7. Follow the coding standards for the technology stack

Please provide ONLY the file content without any explanations or markdown formatting.
The response should be ready to save directly to the file.
"""
        
        system_prompt = f"""You are an expert {tech_stack.get('language')} developer. Generate clean, production-ready code that follows industry best practices. Focus on:
- Code quality and maintainability
- Proper error handling
- Security considerations
- Performance optimization
- Comprehensive documentation
- Testability"""
        
        return self.call_claude(generation_prompt, system_prompt)
    
    def _parse_structure_response(self, response: str, tech_stack: Dict[str, Any]) -> Dict[str, Any]:
        """Parse the structure response from Claude."""
        structure = {
            'directory_structure': '',
            'core_files': [],
            'config_files': [],
            'dependencies': [],
            'architecture_patterns': []
        }
        
        lines = response.split('\n')
        current_section = None
        
        for line in lines:
            line = line.strip()
            if line.startswith('PROJECT_STRUCTURE:'):
                current_section = 'directory_structure'
            elif line.startswith('CORE_FILES:'):
                current_section = 'core_files'
            elif line.startswith('CONFIGURATION_FILES:'):
                current_section = 'config_files'
            elif line.startswith('DEPENDENCIES:'):
                current_section = 'dependencies'
            elif line.startswith('ARCHITECTURE_PATTERNS:'):
                current_section = 'architecture_patterns'
            elif line and current_section:
                if current_section == 'directory_structure':
                    structure['directory_structure'] += line + '\n'
                elif line.startswith('-') and ':' in line:
                    parts = line[1:].split(':', 1)
                    if len(parts) == 2:
                        item = {
                            'name': parts[0].strip(),
                            'description': parts[1].strip()
                        }
                        if current_section == 'core_files':
                            item['path'] = parts[0].strip()
                            item['purpose'] = parts[1].strip()
                        structure[current_section].append(item)
        
        return structure
    
    def _determine_file_type(self, file_path: str) -> str:
        """Determine the type of file based on its path and extension."""
        if not file_path:
            return 'unknown'
        
        extension = file_path.split('.')[-1].lower() if '.' in file_path else ''
        
        type_mapping = {
            'java': 'source',
            'py': 'source',
            'go': 'source',
            'js': 'source',
            'ts': 'source',
            'cs': 'source',
            'rs': 'source',
            'kt': 'source',
            'scala': 'source',
            'php': 'source',
            'xml': 'config',
            'yml': 'config',
            'yaml': 'config',
            'json': 'config',
            'properties': 'config',
            'toml': 'config',
            'md': 'documentation',
            'txt': 'documentation',
            'sql': 'database'
        }
        
        return type_mapping.get(extension, 'other')
