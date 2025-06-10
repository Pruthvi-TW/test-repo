# Agentic Code Generation Workflow

A sophisticated multi-agent system built with **LangGraph** and **LangChain** that automatically generates production-ready applications in any programming language based on natural language prompts.

## 🚀 Features

- **Generic Language Support**: Automatically detects and generates code in Java, Python, Go, JavaScript, TypeScript, C#, Rust, and more
- **Framework Agnostic**: Supports Spring Boot, Django, Flask, Gin, Express, ASP.NET, and other popular frameworks
- **Multi-Agent Architecture**: 7 specialized agents working in sequence
- **LangGraph Orchestration**: State-managed workflow with conditional routing
- **Claude API Integration**: Powered by Anthropic's Claude for high-quality code generation
- **GitHub Integration**: Automatic repository creation and code push
- **Quality Assurance**: Built-in code evaluation and guardrails
- **Token Optimization**: Efficient API usage with context management

## 🏗️ Architecture

### Agent Workflow
```
Prompts (P1-P4) → Technology Detection → Agent Pipeline → GitHub Repository
```

### 7 Specialized Agents

1. **🔍 Prevalidation Agent**: Validates business context and technical requirements
2. **📋 Business Context Agent**: Processes and structures business requirements  
3. **⚡ Code Generation Agent**: Generates application code based on detected technology stack
4. **🔎 Code Evaluation Agent**: Reviews code quality, security, and best practices
5. **📦 Dependency Evaluation Agent**: Manages dependencies and build configurations
6. **🛡️ GuardRails Agent**: Enforces security and compliance standards
7. **🚀 Code Push Agent**: Handles GitHub repository creation and code deployment

## 📁 Project Structure

```
LCLG-2/
├── agents/                 # Agent implementations
│   ├── base_agent.py      # Base agent class with LangChain integration
│   ├── prevalidation_agent.py
│   ├── business_context_agent.py
│   ├── code_generation_agent.py
│   ├── code_evaluation_agent.py
│   ├── dependency_evaluation_agent.py
│   ├── guardrails_agent.py
│   └── code_push_agent.py
├── utils/                 # Utility functions
│   ├── __init__.py
│   └── prompt_reader.py   # Prompt processing and technology detection
├── Prompts/              # Input prompts (P1-P4)
│   ├── P1-PreTech.txt    # Technical guidelines
│   ├── P2-Business.txt   # Business requirements
│   ├── P3-PostTech.txt   # Post-technical requirements
│   └── P4-Mock.txt       # Additional specifications
├── config.py             # Configuration management
├── workflow_state.py     # LangGraph state definitions
├── workflow.py           # Main LangGraph workflow orchestration
├── main.py              # Entry point
├── test_workflow.py     # Setup verification script
├── requirements.txt     # Python dependencies
└── README.md           # This file
```

## 🛠️ Technology Stack

- **Python 3.8+**
- **LangGraph**: Workflow orchestration
- **LangChain**: LLM integration and prompt management
- **Anthropic Claude**: Code generation and analysis
- **PyGithub**: GitHub API integration
- **GitPython**: Git operations
- **Pydantic**: Data validation and serialization

## ⚙️ Setup

### 1. Install Dependencies

```bash
pip install -r requirements.txt
```

### 2. Configure API Keys

The Claude API key is already configured in `config.py`. For GitHub integration, set your GitHub token:

```bash
export GITHUB_TOKEN="your_github_token_here"
```

### 3. Verify Setup

```bash
python test_workflow.py
```

### 4. Run the Workflow

```bash
python main.py
```

## 📝 How It Works

### 1. Prompt Processing
The system reads prompts P1-P4 in sequence:
- **P1**: Technical guidelines and language specifications
- **P2**: Business requirements and domain logic
- **P3**: Post-technical requirements (logging, testing, etc.)
- **P4**: Additional specifications (mock services, etc.)

### 2. Technology Detection
Advanced regex-based detection identifies:
- Programming language (Java, Python, Go, etc.)
- Framework (Spring Boot, Django, Gin, etc.)
- Database (PostgreSQL, MySQL, MongoDB, etc.)
- Build tool (Maven, npm, go mod, etc.)

### 3. Agent Execution
Each agent processes the state sequentially:
- Validates requirements and feasibility
- Extracts business context and entities
- Generates complete application structure
- Creates production-ready code files
- Evaluates code quality and security
- Manages dependencies and configurations
- Applies compliance and security guardrails
- Pushes code to GitHub repository

### 4. Output Generation
The system produces:
- Complete application source code
- Build configuration files (pom.xml, package.json, etc.)
- Documentation (README, API docs)
- Test structure and examples
- GitHub repository with organized code

## 🎯 Example Usage

### Java Spring Boot Application
If your prompts specify Java and Spring Boot, the system will generate:
- Maven project structure
- Spring Boot application class
- REST controllers
- JPA entities
- Service layers
- Configuration files
- Unit and integration tests
- Complete pom.xml with dependencies

### Go Gin Application  
If your prompts specify Go and Gin, the system will generate:
- Go module structure
- Gin router setup
- Handler functions
- Struct definitions
- Database models
- Middleware
- go.mod with dependencies
- Docker configuration

### Python FastAPI Application
If your prompts specify Python and FastAPI, the system will generate:
- FastAPI application structure
- Pydantic models
- API endpoints
- Database integration
- Authentication middleware
- requirements.txt
- Docker and deployment configs

## 🔧 Configuration

### Key Configuration Options

```python
# config.py
CLAUDE_API_KEY = "your_claude_api_key"
GITHUB_REPO_URL = "https://github.com/username/repo.git"
CLAUDE_MODEL = "claude-3-sonnet-20240229"
MAX_TOKENS = 4000
```

### Supported Languages and Frameworks

| Language   | Frameworks                    | Build Tools        |
|------------|-------------------------------|-------------------|
| Java       | Spring Boot, Quarkus         | Maven, Gradle     |
| Python     | Django, Flask, FastAPI       | pip, Poetry       |
| Go         | Gin, Fiber, Echo             | go mod            |
| JavaScript | Express, NestJS              | npm, yarn         |
| TypeScript | NestJS, Express              | npm, yarn         |
| C#         | ASP.NET Core                 | dotnet            |
| Rust       | Actix, Rocket                | Cargo             |

## 📊 Output Examples

### Generated File Structure (Java Spring Boot)
```
ekyc-service/
├── src/
│   ├── main/
│   │   ├── java/com/ekyc/
│   │   │   ├── EkycApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── model/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
├── pom.xml
├── README.md
└── Dockerfile
```

### Quality Metrics
- **Code Quality Score**: 8.5/10
- **Security Compliance**: PASS
- **Test Coverage Potential**: HIGH
- **Documentation Quality**: EXCELLENT
- **Deployment Readiness**: READY

## 🔍 Monitoring and Logging

The workflow provides comprehensive logging:
- Agent execution times
- Token usage optimization
- Error handling and recovery
- Quality assessment scores
- Compliance check results

Logs are saved to `workflow.log` and results to timestamped JSON files.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add your improvements
4. Test with `python test_workflow.py`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:
1. Check the logs in `workflow.log`
2. Run `python test_workflow.py` to verify setup
3. Review the generated JSON results
4. Check agent-specific error messages

## 🎉 Success Stories

The system has successfully generated:
- ✅ Java Spring Boot microservices with PostgreSQL
- ✅ Python FastAPI applications with MongoDB
- ✅ Go Gin REST APIs with Redis
- ✅ Node.js Express applications with MySQL
- ✅ Complete CI/CD configurations
- ✅ Production-ready Docker setups

---

**Built with ❤️ using LangGraph, LangChain, and Claude AI**
