"""
Code Structure Agent - Defines proper project structure based on technology stack and requirements.
"""
import time
import re
from typing import Dict, Any, List
from .base_agent import BaseAgent
from workflow_state import WorkflowState, AgentStatus, update_agent_status

class CodeStructureAgent(BaseAgent):
    """Agent responsible for defining proper project structure and architecture."""
    
    def __init__(self):
        super().__init__("CodeStructureAgent")
    
    def process(self, state: WorkflowState) -> WorkflowState:
        """Define comprehensive project structure based on technology stack."""
        start_time = time.time()
        
        try:
            self.log_progress("Starting code structure definition")
            
            # Update agent status to running
            state = update_agent_status(state, self.name, AgentStatus.RUNNING)
            
            # Validate required state
            required_keys = ['prompts', 'technology_stack', 'business_context']
            if not self.validate_state(state, required_keys):
                return self.handle_error(state, ValueError("Missing required state keys"), "state_validation")
            
            prompts = state['prompts']
            tech_stack = state['technology_stack']
            business_context = state['business_context']
            
            # Define project structure
            project_structure = self._define_project_structure(prompts, tech_stack, business_context)
            
            # Update state
            state['code_structure_complete'] = True
            state['project_structure'] = project_structure
            
            # Update agent status to completed
            execution_time = time.time() - start_time
            state = update_agent_status(
                state, 
                self.name, 
                AgentStatus.COMPLETED, 
                output=project_structure,
                execution_time=execution_time
            )
            
            self.log_progress("Code structure definition completed")
            
            return state
            
        except Exception as e:
            return self.handle_error(state, e, "code_structure_process")
    
    def _define_project_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define comprehensive project structure based on technology stack."""
        
        # Get tech stack specific structure
        if tech_stack.language.lower() == 'java':
            return self._define_java_structure(prompts, tech_stack, business_context)
        elif tech_stack.language.lower() == 'python':
            return self._define_python_structure(prompts, tech_stack, business_context)
        elif tech_stack.language.lower() == 'golang':
            return self._define_golang_structure(prompts, tech_stack, business_context)
        elif tech_stack.language.lower() in ['javascript', 'typescript']:
            return self._define_nodejs_structure(prompts, tech_stack, business_context)
        elif tech_stack.language.lower() == 'csharp':
            return self._define_dotnet_structure(prompts, tech_stack, business_context)
        else:
            return self._define_generic_structure(prompts, tech_stack, business_context)
    
    def _define_java_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define Java/Spring Boot project structure."""
        
        # Determine if it's Spring Boot
        is_spring_boot = 'spring' in tech_stack.framework.lower()
        
        # Base package name from business domain (clean it up)
        domain_clean = business_context.domain.lower()
        domain_clean = re.sub(r'[^a-z0-9]', '', domain_clean)  # Remove special chars
        if len(domain_clean) > 20:
            domain_clean = domain_clean[:20]  # Limit length
        base_package = f"com.{domain_clean}"
        
        structure = {
            'language': 'java',
            'framework': tech_stack.framework,
            'build_tool': tech_stack.build_tool,
            'base_package': base_package,
            'directory_structure': {
                'src/main/java': {
                    f'{base_package.replace(".", "/")}/': {
                        'Application.java': 'Main application class',
                        'config/': 'Configuration classes',
                        'controller/': 'REST controllers',
                        'service/': 'Business logic services',
                        'repository/': 'Data access layer',
                        'model/': 'Entity and domain models',
                        'dto/': 'Data transfer objects',
                        'exception/': 'Custom exceptions',
                        'util/': 'Utility classes',
                        'security/': 'Security configuration' if is_spring_boot else None
                    }
                },
                'src/main/resources': {
                    'application.yml': 'Application configuration',
                    'application-dev.yml': 'Development configuration',
                    'application-prod.yml': 'Production configuration',
                    'db/migration/': 'Database migration scripts',
                    'static/': 'Static web resources',
                    'templates/': 'Template files'
                },
                'src/test/java': {
                    f'{base_package.replace(".", "/")}/': {
                        'controller/': 'Controller tests',
                        'service/': 'Service tests',
                        'repository/': 'Repository tests',
                        'integration/': 'Integration tests'
                    }
                },
                'src/test/resources': {
                    'application-test.yml': 'Test configuration'
                }
            },
            'root_files': {
                'pom.xml' if tech_stack.build_tool == 'maven' else 'build.gradle': 'Build configuration',
                'README.md': 'Project documentation',
                'Dockerfile': 'Container configuration',
                'docker-compose.yml': 'Multi-container setup',
                '.gitignore': 'Git ignore rules',
                '.env.example': 'Environment variables template'
            },
            'core_files': self._get_java_core_files(base_package, business_context, is_spring_boot),
            'dependencies': self._get_java_dependencies(tech_stack, business_context, is_spring_boot)
        }
        
        return structure
    
    def _define_python_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define Python project structure."""
        
        app_name = business_context.domain.lower().replace(' ', '_').replace('-', '_')
        is_django = 'django' in tech_stack.framework.lower()
        is_fastapi = 'fastapi' in tech_stack.framework.lower()
        is_flask = 'flask' in tech_stack.framework.lower()
        
        if is_django:
            return self._define_django_structure(app_name, business_context)
        elif is_fastapi:
            return self._define_fastapi_structure(app_name, business_context)
        elif is_flask:
            return self._define_flask_structure(app_name, business_context)
        else:
            return self._define_generic_python_structure(app_name, business_context)
    
    def _define_golang_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define Go project structure."""
        
        app_name = business_context.domain.lower().replace(' ', '-').replace('_', '-')
        is_gin = 'gin' in tech_stack.framework.lower()
        
        structure = {
            'language': 'golang',
            'framework': tech_stack.framework,
            'build_tool': 'go-mod',
            'module_name': f"github.com/company/{app_name}",
            'directory_structure': {
                'cmd/': {
                    f'{app_name}/': {
                        'main.go': 'Application entry point'
                    }
                },
                'internal/': {
                    'api/': {
                        'handlers/': 'HTTP handlers',
                        'middleware/': 'HTTP middleware',
                        'routes/': 'Route definitions'
                    },
                    'service/': 'Business logic',
                    'repository/': 'Data access layer',
                    'model/': 'Domain models',
                    'config/': 'Configuration',
                    'util/': 'Utility functions'
                },
                'pkg/': {
                    'database/': 'Database utilities',
                    'logger/': 'Logging utilities',
                    'validator/': 'Validation utilities'
                },
                'migrations/': 'Database migrations',
                'docs/': 'API documentation',
                'scripts/': 'Build and deployment scripts',
                'test/': 'Integration tests'
            },
            'root_files': {
                'go.mod': 'Go module definition',
                'go.sum': 'Dependency checksums',
                'README.md': 'Project documentation',
                'Dockerfile': 'Container configuration',
                'docker-compose.yml': 'Multi-container setup',
                '.gitignore': 'Git ignore rules',
                'Makefile': 'Build automation'
            },
            'core_files': self._get_golang_core_files(app_name, business_context, is_gin),
            'dependencies': self._get_golang_dependencies(tech_stack, business_context)
        }
        
        return structure
    
    def _define_nodejs_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define Node.js/TypeScript project structure."""
        
        app_name = business_context.domain.lower().replace(' ', '-').replace('_', '-')
        is_express = 'express' in tech_stack.framework.lower()
        is_nestjs = 'nestjs' in tech_stack.framework.lower()
        is_typescript = tech_stack.language.lower() == 'typescript'
        
        file_ext = 'ts' if is_typescript else 'js'
        
        structure = {
            'language': tech_stack.language,
            'framework': tech_stack.framework,
            'build_tool': 'npm',
            'directory_structure': {
                'src/': {
                    'controllers/': f'Route controllers (.{file_ext})',
                    'services/': f'Business logic (.{file_ext})',
                    'models/': f'Data models (.{file_ext})',
                    'middleware/': f'Express middleware (.{file_ext})',
                    'routes/': f'Route definitions (.{file_ext})',
                    'config/': f'Configuration (.{file_ext})',
                    'utils/': f'Utility functions (.{file_ext})',
                    'types/': 'TypeScript type definitions' if is_typescript else None
                },
                'tests/': {
                    'unit/': 'Unit tests',
                    'integration/': 'Integration tests',
                    'e2e/': 'End-to-end tests'
                },
                'docs/': 'API documentation',
                'scripts/': 'Build and deployment scripts'
            },
            'root_files': {
                'package.json': 'NPM package configuration',
                'package-lock.json': 'Dependency lock file',
                f'app.{file_ext}': 'Main application file',
                f'server.{file_ext}': 'Server entry point',
                'tsconfig.json': 'TypeScript configuration' if is_typescript else None,
                '.env.example': 'Environment variables template',
                'README.md': 'Project documentation',
                'Dockerfile': 'Container configuration',
                '.gitignore': 'Git ignore rules'
            },
            'core_files': self._get_nodejs_core_files(app_name, business_context, is_express, is_nestjs, file_ext),
            'dependencies': self._get_nodejs_dependencies(tech_stack, business_context, is_typescript)
        }
        
        return structure
    
    def _define_dotnet_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define .NET project structure."""
        
        app_name = business_context.domain.replace(' ', '').replace('-', '')
        
        structure = {
            'language': 'csharp',
            'framework': tech_stack.framework,
            'build_tool': 'dotnet',
            'namespace': f"{app_name}",
            'directory_structure': {
                f'{app_name}.API/': {
                    'Controllers/': 'API controllers',
                    'Models/': 'API models',
                    'Program.cs': 'Application entry point',
                    'Startup.cs': 'Application configuration'
                },
                f'{app_name}.Core/': {
                    'Entities/': 'Domain entities',
                    'Interfaces/': 'Service interfaces',
                    'Services/': 'Business logic'
                },
                f'{app_name}.Infrastructure/': {
                    'Data/': 'Data access layer',
                    'Repositories/': 'Repository implementations'
                },
                f'{app_name}.Tests/': {
                    'Unit/': 'Unit tests',
                    'Integration/': 'Integration tests'
                }
            },
            'root_files': {
                f'{app_name}.sln': 'Solution file',
                'README.md': 'Project documentation',
                'Dockerfile': 'Container configuration',
                '.gitignore': 'Git ignore rules'
            },
            'core_files': self._get_dotnet_core_files(app_name, business_context),
            'dependencies': self._get_dotnet_dependencies(tech_stack, business_context)
        }
        
        return structure
    
    def _define_generic_structure(self, prompts: list, tech_stack, business_context) -> Dict[str, Any]:
        """Define generic project structure for unsupported languages."""
        
        app_name = business_context.domain.lower().replace(' ', '_')
        
        structure = {
            'language': tech_stack.language,
            'framework': tech_stack.framework,
            'build_tool': tech_stack.build_tool,
            'directory_structure': {
                'src/': 'Source code',
                'tests/': 'Test files',
                'docs/': 'Documentation',
                'config/': 'Configuration files'
            },
            'root_files': {
                'README.md': 'Project documentation',
                '.gitignore': 'Git ignore rules'
            },
            'core_files': [],
            'dependencies': []
        }
        
        return structure
    
    def _get_java_core_files(self, base_package: str, business_context, is_spring_boot: bool) -> List[Dict[str, Any]]:
        """Get core Java files to generate."""
        
        files = [
            {
                'path': f'src/main/java/{base_package.replace(".", "/")}/Application.java',
                'type': 'main_class',
                'purpose': 'Main application class',
                'template': 'spring_boot_main' if is_spring_boot else 'java_main'
            }
        ]
        
        # Add entity files based on business context
        for entity in business_context.entities[:5]:  # Limit to 5 entities
            # Clean entity name - remove descriptions and special characters
            entity_clean = entity.split(':')[0].split('-')[0].strip()  # Take only the name part
            entity_name = re.sub(r'[^a-zA-Z0-9]', '', entity_clean).title()  # Remove special chars
            if not entity_name:  # Skip if empty after cleaning
                continue
            files.extend([
                {
                    'path': f'src/main/java/{base_package.replace(".", "/")}/model/{entity_name}.java',
                    'type': 'entity',
                    'purpose': f'{entity_name} entity class',
                    'template': 'jpa_entity' if is_spring_boot else 'java_class'
                },
                {
                    'path': f'src/main/java/{base_package.replace(".", "/")}/repository/{entity_name}Repository.java',
                    'type': 'repository',
                    'purpose': f'{entity_name} data access',
                    'template': 'spring_repository' if is_spring_boot else 'java_interface'
                },
                {
                    'path': f'src/main/java/{base_package.replace(".", "/")}/service/{entity_name}Service.java',
                    'type': 'service',
                    'purpose': f'{entity_name} business logic',
                    'template': 'spring_service' if is_spring_boot else 'java_class'
                },
                {
                    'path': f'src/main/java/{base_package.replace(".", "/")}/controller/{entity_name}Controller.java',
                    'type': 'controller',
                    'purpose': f'{entity_name} REST API',
                    'template': 'spring_controller' if is_spring_boot else 'java_class'
                }
            ])
        
        return files
    
    def _get_java_dependencies(self, tech_stack, business_context, is_spring_boot: bool) -> List[Dict[str, Any]]:
        """Get Java dependencies based on requirements."""
        
        dependencies = []
        
        if is_spring_boot:
            dependencies.extend([
                {'name': 'spring-boot-starter-web', 'version': '3.2.3', 'scope': 'compile'},
                {'name': 'spring-boot-starter-data-jpa', 'version': '3.2.3', 'scope': 'compile'},
                {'name': 'spring-boot-starter-security', 'version': '3.2.3', 'scope': 'compile'},
                {'name': 'spring-boot-starter-validation', 'version': '3.2.3', 'scope': 'compile'},
                {'name': 'spring-boot-starter-test', 'version': '3.2.3', 'scope': 'test'}
            ])
        
        # Database dependencies
        if tech_stack.database.lower() == 'postgresql':
            dependencies.append({'name': 'postgresql', 'version': '42.7.2', 'scope': 'runtime'})
        elif tech_stack.database.lower() == 'mysql':
            dependencies.append({'name': 'mysql-connector-java', 'version': '8.0.33', 'scope': 'runtime'})
        
        return dependencies
    
    def _get_golang_core_files(self, app_name: str, business_context, is_gin: bool) -> List[Dict[str, Any]]:
        """Get core Go files to generate."""

        files = [
            {
                'path': f'cmd/{app_name}/main.go',
                'type': 'main',
                'purpose': 'Application entry point',
                'template': 'gin_main' if is_gin else 'go_main'
            },
            {
                'path': 'go.mod',
                'type': 'module',
                'purpose': 'Go module definition',
                'template': 'go_mod'
            }
        ]

        # Add entity-based files for Go
        for entity in business_context.entities[:3]:  # Limit to 3 entities
            entity_name = entity.replace(' ', '').lower()
            files.extend([
                {
                    'path': f'internal/model/{entity_name}.go',
                    'type': 'model',
                    'purpose': f'{entity} model struct',
                    'template': 'go_model'
                },
                {
                    'path': f'internal/api/handlers/{entity_name}_handler.go',
                    'type': 'handler',
                    'purpose': f'{entity} HTTP handlers',
                    'template': 'gin_handler'
                }
            ])

        return files
    
    def _get_golang_dependencies(self, tech_stack, business_context) -> List[Dict[str, Any]]:
        """Get Go dependencies."""
        
        dependencies = []
        
        if 'gin' in tech_stack.framework.lower():
            dependencies.append({'name': 'github.com/gin-gonic/gin', 'version': 'v1.9.1'})
        
        if tech_stack.database.lower() == 'postgresql':
            dependencies.extend([
                {'name': 'github.com/lib/pq', 'version': 'v1.10.9'},
                {'name': 'gorm.io/gorm', 'version': 'v1.25.7'},
                {'name': 'gorm.io/driver/postgres', 'version': 'v1.5.6'}
            ])
        
        return dependencies
    
    def _get_nodejs_core_files(self, app_name: str, business_context, is_express: bool, is_nestjs: bool, file_ext: str) -> List[Dict[str, Any]]:
        """Get core Node.js files to generate."""
        
        files = [
            {
                'path': f'src/app.{file_ext}',
                'type': 'main',
                'purpose': 'Main application file',
                'template': 'express_app' if is_express else 'nodejs_app'
            },
            {
                'path': 'package.json',
                'type': 'config',
                'purpose': 'NPM package configuration',
                'template': 'package_json'
            }
        ]
        
        return files
    
    def _get_nodejs_dependencies(self, tech_stack, business_context, is_typescript: bool) -> List[Dict[str, Any]]:
        """Get Node.js dependencies."""
        
        dependencies = []
        
        if 'express' in tech_stack.framework.lower():
            dependencies.extend([
                {'name': 'express', 'version': '^4.18.2', 'type': 'dependency'},
                {'name': '@types/express', 'version': '^4.17.17', 'type': 'devDependency'}
            ])
        
        if is_typescript:
            dependencies.extend([
                {'name': 'typescript', 'version': '^5.0.0', 'type': 'devDependency'},
                {'name': '@types/node', 'version': '^20.0.0', 'type': 'devDependency'}
            ])
        
        return dependencies
    
    def _get_dotnet_core_files(self, app_name: str, business_context) -> List[Dict[str, Any]]:
        """Get core .NET files to generate."""
        
        files = [
            {
                'path': f'{app_name}.API/Program.cs',
                'type': 'main',
                'purpose': 'Application entry point',
                'template': 'dotnet_program'
            },
            {
                'path': f'{app_name}.sln',
                'type': 'solution',
                'purpose': 'Solution file',
                'template': 'dotnet_solution'
            }
        ]
        
        return files
    
    def _get_dotnet_dependencies(self, tech_stack, business_context) -> List[Dict[str, Any]]:
        """Get .NET dependencies."""
        
        dependencies = [
            {'name': 'Microsoft.AspNetCore.App', 'version': '8.0.0'},
            {'name': 'Microsoft.EntityFrameworkCore', 'version': '8.0.0'}
        ]
        
        if tech_stack.database.lower() == 'postgresql':
            dependencies.append({'name': 'Npgsql.EntityFrameworkCore.PostgreSQL', 'version': '8.0.0'})
        
        return dependencies
