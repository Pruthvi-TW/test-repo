# Automated Code Generation Workflow

An automated workflow using LangGraph and LangChain that reads prompts from a file, generates code using Claude API, and pushes the generated code to a GitHub repository.

## Features

- 🤖 **AI-Powered Code Generation**: Uses Claude API for intelligent code generation
- 📝 **Prompt-Based**: Reads prompts from configurable text files
- 🔄 **Automated Workflow**: LangGraph-based workflow with error handling
- 🚀 **GitHub Integration**: Automatically pushes generated code to repositories
- ⚙️ **Configurable**: Environment-based configuration management
- 📊 **Logging**: Comprehensive logging for debugging and monitoring

## Prerequisites

- Python 3.8+
- Claude API key (Anthropic)
- GitHub personal access token
- GitHub repository for code storage

## Installation

1. **Clone or download this project**

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Set up environment variables**:
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` file with your credentials:
   ```env
   ANTHROPIC_API_KEY=your_claude_api_key_here
   GITHUB_TOKEN=your_github_token_here
   GITHUB_REPO_OWNER=your_github_username
   GITHUB_REPO_NAME=your_repository_name
   ```

## Configuration

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `ANTHROPIC_API_KEY` | Your Claude API key | Yes |
| `GITHUB_TOKEN` | GitHub personal access token | Yes |
| `GITHUB_REPO_OWNER` | GitHub username/organization | Yes |
| `GITHUB_REPO_NAME` | Repository name | Yes |
| `PROMPT_FILE_PATH` | Path to prompt file | No (default: `Prompts/prompt.txt`) |
| `OUTPUT_DIRECTORY` | Directory for generated code | No (default: `generated_code`) |
| `COMMIT_MESSAGE_PREFIX` | Prefix for commit messages | No (default: `[AUTO-GENERATED]`) |

### Getting API Keys

1. **Claude API Key**:
   - Visit [Anthropic Console](https://console.anthropic.com/)
   - Create an account and generate an API key

2. **GitHub Token**:
   - Go to GitHub Settings > Developer settings > Personal access tokens
   - Generate a new token with `repo` permissions

## Usage

1. **Write your prompt** in `Prompts/prompt.txt`:
   ```
   Create a Python function that calculates fibonacci numbers with memoization
   ```

2. **Run the workflow**:
   ```bash
   python main.py
   ```

3. **Check the results**:
   - Generated code will be pushed to your GitHub repository
   - Check the console output for GitHub URLs
   - Review the `workflow.log` file for detailed logs

## Workflow Steps

The automated workflow follows these steps:

1. **Read Prompt**: Reads the prompt from the specified file
2. **Generate Code**: Uses Claude API to generate code based on the prompt
3. **Validate Code**: Performs basic validation of the generated code
4. **Push to GitHub**: Creates/updates files in the GitHub repository
5. **Error Handling**: Handles errors at each step with detailed logging

## File Structure

```
LCLG/
├── main.py                 # Main entry point
├── workflow.py             # LangGraph workflow definition
├── claude_agent.py         # Claude API integration
├── github_agent.py         # GitHub API integration
├── config.py               # Configuration management
├── requirements.txt        # Python dependencies
├── .env.example           # Environment variables template
├── .env                   # Your environment variables (create this)
├── README.md              # This file
├── Prompts/
│   └── prompt.txt         # Your prompt file
└── generated_code/        # Generated code (in GitHub repo)
```

## Example Output

When successful, you'll see output like:

```
🎉 SUCCESS! Code generated and pushed to GitHub
==================================================
📝 Generated file: fibonacci_calculator.py
🔗 GitHub URL: https://github.com/username/repo/blob/main/generated_code/20241201_143022_fibonacci_calculator.py
📋 Commit URL: https://github.com/username/repo/commit/abc123...
📄 Description: Python function for calculating fibonacci numbers with memoization
==================================================
```

## Troubleshooting

### Common Issues

1. **Missing API Keys**: Ensure all required environment variables are set
2. **GitHub Permissions**: Verify your GitHub token has `repo` permissions
3. **Repository Access**: Ensure the specified repository exists and is accessible
4. **Empty Prompt**: Check that your prompt file contains content

### Logs

Check `workflow.log` for detailed execution logs and error messages.

## Customization

### Adding New Workflow Steps

You can extend the workflow by:

1. Adding new nodes to the `workflow.py` file
2. Implementing custom agents for different services
3. Modifying the state structure in `WorkflowState`

### Changing Code Generation Model

To use a different Claude model, modify the `claude_agent.py` file:

```python
self.llm = ChatAnthropic(
    anthropic_api_key=config.anthropic_api_key,
    model="claude-3-opus-20240229",  # Change model here
    temperature=0.1,
    max_tokens=4000,
)
```

## License

This project is open source. Feel free to modify and distribute as needed.

## Support

For issues and questions:
1. Check the logs in `workflow.log`
2. Verify your configuration in `.env`
3. Ensure all dependencies are installed correctly
