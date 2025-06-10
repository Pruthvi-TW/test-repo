# Agentic Code Generation Workflow - Setup Guide

## 🚀 Quick Start

This agentic workflow system uses LangChain/LangGraph to orchestrate 8 specialized AI agents that work together to generate production-ready code from natural language prompts.

### Prerequisites

1. **Python 3.8+** installed
2. **Claude API Key** from Anthropic (https://console.anthropic.com/)
3. **GitHub Token** (optional, for automatic repository creation)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Pruthvi-TW/test-repo.git
   cd test-repo
   ```

2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Set up environment variables:**
   ```bash
   cp .env.example .env
   # Edit .env file with your API keys
   ```

4. **Configure your Claude API key in `.env`:**
   ```
   CLAUDE_API_KEY=sk-ant-api03-your-actual-claude-api-key-here
   GITHUB_TOKEN=your-github-token-here
   ```

### Running the System

1. **Test the workflow structure:**
   ```bash
   python test_workflow_structure.py
   ```

2. **Run the full agentic workflow:**
   ```bash
   python main.py
   ```

## 🤖 Agent Architecture

The system consists of 8 specialized agents working in sequence:

1. **PrevalidationAgent** - Validates and preprocesses input prompts
2. **BusinessContextAgent** - Extracts business requirements and context
3. **CodeStructureAgent** - Defines project architecture and structure
4. **CodeGenerationAgent** - Generates actual code files
5. **CodeEvaluationAgent** - Reviews and validates generated code
6. **DependencyEvaluationAgent** - Manages dependencies and configurations
7. **GuardRailsAgent** - Applies security and quality checks
8. **CodePushAgent** - Commits and pushes code to GitHub repositories

## 📁 Project Structure

```
LCLG-2/
├── agents/                 # Individual agent implementations
├── Prompts/               # Input prompt files
├── generated_code/        # Output directory for generated applications
├── utils/                 # Utility functions and templates
├── config.py             # Configuration management
├── workflow.py           # Main workflow orchestration
├── workflow_state.py     # LangGraph state management
├── main.py              # Entry point
└── requirements.txt     # Python dependencies
```

## 🎯 Supported Technologies

The system automatically detects and generates code for:

**Languages:** Java, Python, Go, JavaScript, TypeScript, C#, Rust, Kotlin, Scala, PHP

**Frameworks:** Spring Boot, Django, Flask, FastAPI, Gin, Fiber, Express, NestJS, ASP.NET, Laravel, Rails

## 📝 Usage Examples

### Example 1: Java Spring Boot Application
Place your prompts in the `Prompts/` directory and run:
```bash
python main.py
```

The system will:
- Detect Java/Spring Boot from your prompts
- Generate a complete microservices application
- Create proper project structure with Maven/Gradle
- Include tests, documentation, and Docker configuration
- Push to your specified GitHub repository

### Example 2: Python FastAPI Service
The workflow automatically adapts to generate Python code when it detects Python-related requirements in your prompts.

## 🔧 Configuration

Edit `config.py` to customize:
- Output directories
- Supported languages and frameworks
- Agent timeouts and retry settings
- GitHub repository settings

## 📊 Monitoring and Logs

- Workflow execution logs are saved to `workflow.log`
- Detailed results are saved as JSON in `generated_code/`
- Each agent's output is tracked and can be reviewed

## 🛠️ Troubleshooting

### Common Issues:

1. **"Invalid Claude API Key"**
   - Ensure your API key is correctly set in `.env`
   - Verify the key format starts with `sk-ant-api03-`

2. **"Workflow structure test failed"**
   - Run `python test_workflow_structure.py` to diagnose
   - Check that all prompt files exist in `Prompts/` directory

3. **"GitHub push failed"**
   - Verify your GitHub token has proper permissions
   - Ensure the target repository exists and is accessible

## 🎉 Success!

Once running successfully, you'll see:
- Generated code in `generated_code/` directory
- Automatic GitHub repository creation/updates
- Comprehensive logging and monitoring
- Production-ready applications with proper structure

The system is now ready to generate applications in any supported language based on your natural language prompts!
