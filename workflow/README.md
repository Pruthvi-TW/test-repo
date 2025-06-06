# Automated Java Spring Boot Code Generation Workflow

An advanced automated workflow using LangGraph and LangChain that reads multiple prompts (P1-P4), generates comprehensive Java Spring Boot applications using Claude API, and pushes structured code to GitHub repositories.

## 🚀 Features

- 🤖 **Multi-Prompt AI Generation**: Processes 4 prompts (P1-P4) in sequence for comprehensive code generation
- 🏗️ **Enterprise Java Applications**: Generates complete Spring Boot applications with proper package structure
- � **eKYC Specialization**: Specialized for generating eKYC (Aadhaar verification) applications
- 🔄 **Multi-Call Strategy**: Uses 5 separate Claude API calls for comprehensive coverage
- 🚀 **GitHub Integration**: Automatically pushes structured projects to repositories
- ⚙️ **Production-Ready**: Generates enterprise-grade code with proper configurations
- 📊 **Comprehensive Logging**: Detailed execution tracking and debugging

## 🎯 Generated Application Features

- **Main eKYC Service** (Port 8080): Complete business logic implementation
- **Mock UIDAI Service** (Port 8082): Realistic simulation service for testing
- **Multi-Module Maven**: Parent and child POM structure
- **No Lombok**: Explicit getters/setters as per requirements
- **Spring Boot 3.2.3**: Latest stable version with Java 21
- **PostgreSQL Integration**: Database configuration and migrations
- **Docker Support**: Complete containerization setup

## 📋 Prerequisites

- Python 3.8+
- Claude API key (Anthropic)
- GitHub personal access token
- GitHub repository for code storage
- Java 21 (for running generated applications)
- Maven 3.6+ (for building generated applications)
- PostgreSQL 12+ (for generated applications)

## 🛠️ Installation

1. **Clone or download this project**

2. **Install Python dependencies**:
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

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `ANTHROPIC_API_KEY` | Your Claude API key | Yes | - |
| `GITHUB_TOKEN` | GitHub personal access token | Yes | - |
| `GITHUB_REPO_OWNER` | GitHub username/organization | Yes | - |
| `GITHUB_REPO_NAME` | Repository name | Yes | - |
| `PROMPTS_DIRECTORY` | Directory containing prompt files | No | `Prompts` |
| `OUTPUT_DIRECTORY` | Directory for generated code | No | `generated_code` |
| `COMMIT_MESSAGE_PREFIX` | Prefix for commit messages | No | `[AUTO-GENERATED]` |

### Getting API Keys

1. **Claude API Key**:
   - Visit [Anthropic Console](https://console.anthropic.com/)
   - Create an account and generate an API key
   - Ensure you have sufficient credits for code generation

2. **GitHub Token**:
   - Go to GitHub Settings > Developer settings > Personal access tokens
   - Generate a new token with `repo` permissions
   - Copy the token immediately (it won't be shown again)

## 📝 Prompt Structure

The workflow reads 4 specific prompts in sequence:

```
Prompts/
├── P1-PreTech.txt     # Technical guidelines and constraints
├── P2-Business.txt    # Business flow and requirements
├── P3-PostTech.txt    # Implementation details and audit requirements
└── P4-Mock.txt        # Mock service specifications
```

### Sample Prompt Content:
- **P1**: Java version, Spring Boot version, database requirements, coding standards
- **P2**: Complete business flow (3-phase eKYC process)
- **P3**: Audit logging, security requirements, validation rules
- **P4**: Mock UIDAI service behavior and API specifications

## 🚀 Usage

1. **Prepare your prompts** in the `Prompts/` directory:
   - Ensure all 4 files (P1-P4) contain relevant content
   - Follow the eKYC application requirements format

2. **Run the automated workflow**:
   ```bash
   python main.py
   ```

3. **Monitor the execution**:
   - Watch console output for progress updates
   - 5 separate Claude API calls will be made
   - Each call generates specific components

4. **Check the results**:
   - Generated project will be pushed to your GitHub repository
   - Check console output for GitHub URLs and file counts
   - Review the `workflow.log` file for detailed execution logs

## 🔄 Workflow Steps

The automated workflow follows these detailed steps:

### **Phase 1: Multi-Prompt Reading**
1. **Read P1-PreTech.txt**: Technical guidelines and constraints
2. **Read P2-Business.txt**: Business flow and requirements
3. **Read P3-PostTech.txt**: Implementation and audit requirements
4. **Read P4-Mock.txt**: Mock service specifications

### **Phase 2: Multi-Call Code Generation**
1. **Call 1 - Core Infrastructure**: Entities, repositories, database config, migrations
2. **Call 2 - Services & Controllers**: Business logic, REST APIs, exception handling
3. **Call 3 - DTOs & Utilities**: Data transfer objects, validation, utility classes
4. **Call 4 - Mock Service**: Complete UIDAI simulation service
5. **Call 5 - Tests & Configuration**: Unit tests, integration tests, configurations

### **Phase 3: GitHub Integration**
1. **Create Project Structure**: Organized folder hierarchy
2. **Upload Files**: All generated components with proper paths
3. **Generate Documentation**: README with setup instructions
4. **Commit Changes**: Timestamped commits with descriptive messages

## 📁 Project File Structure

```
LCLG/                           # Workflow Directory
├── main.py                     # 🚀 Main entry point
├── workflow.py                 # 🔄 Multi-call workflow orchestration
├── claude_agent.py             # 🤖 Claude API integration (5 specialized calls)
├── github_agent.py             # 🐙 GitHub API integration
├── config.py                   # ⚙️ Configuration management
├── requirements.txt            # 📦 Python dependencies
├── .env.example               # 📋 Environment variables template
├── .env                       # 🔐 Your environment variables (create this)
├── README.md                  # 📖 This documentation
├── Prompts/                   # 📝 Input prompts directory
│   ├── P1-PreTech.txt         # Technical guidelines
│   ├── P2-Business.txt        # Business requirements
│   ├── P3-PostTech.txt        # Implementation details
│   └── P4-Mock.txt            # Mock service specs
└── Generated Projects/        # 🏗️ Output (in GitHub repo)
    └── ekyc_application_YYYYMMDD_HHMMSS/
        ├── pom.xml (parent)
        ├── README.md
        ├── docker-compose.yml
        ├── ekyc-service/
        │   ├── pom.xml
        │   └── src/main/java/com/ekyc/
        │       ├── EkycVerificationApplication.java
        │       ├── controller/
        │       ├── service/
        │       ├── dto/
        │       └── util/
        └── mock-uidai-service/
            ├── pom.xml
            └── src/main/java/com/mockuidai/
```

## 📊 Example Output

When successful, you'll see output like:

```
🎉 SUCCESS! Structured Java Spring Boot Application Generated!
============================================================
📝 Prompts processed: P1, P2, P3, P4
🏗️ Project name: ekyc_application_20250606_110345
📁 Files created: 16
🔗 Project URL: https://github.com/Pruthvi-TW/test-repo/tree/main/generated_code/ekyc_application_20250606_110345
📄 Description: Complete eKYC application with main service and mock UIDAI service
============================================================
```

### Generated Application Structure:
```
ekyc_application_20250606_110345/
├── pom.xml                                    # Parent Maven configuration
├── README.md                                  # Project documentation
├── docker-compose.yml                        # Docker setup
├── ekyc-service/                             # Main eKYC Service (Port 8080)
│   ├── pom.xml
│   ├── src/main/java/com/ekyc/
│   │   ├── EkycVerificationApplication.java  # Main application class
│   │   ├── controller/                       # REST API endpoints
│   │   ├── service/                          # Business logic
│   │   ├── dto/                             # Data transfer objects
│   │   └── util/                            # Utility classes
│   └── src/test/java/com/ekyc/              # Test cases
└── mock-uidai-service/                       # Mock UIDAI Service (Port 8082)
    ├── pom.xml
    └── src/main/java/com/mockuidai/          # Mock service implementation
```

## � Running Generated Applications

After generation, you can run the applications:

```bash
# Navigate to generated project
cd generated_code/ekyc_application_YYYYMMDD_HHMMSS

# Build the project
mvn clean install

# Run main eKYC service (Port 8080)
cd ekyc-service
mvn spring-boot:run

# Run mock UIDAI service (Port 8082) - in another terminal
cd mock-uidai-service
mvn spring-boot:run
```

## 🐛 Troubleshooting

### Common Issues

1. **Missing API Keys**: Ensure all required environment variables are set in `.env`
2. **GitHub Permissions**: Verify your GitHub token has `repo` permissions
3. **Repository Access**: Ensure the specified repository exists and is accessible
4. **Empty Prompts**: Check that all 4 prompt files (P1-P4) contain content
5. **Token Limits**: Claude API has rate limits - wait between runs if needed
6. **Network Issues**: Ensure stable internet connection for API calls

### Debugging

1. **Check Logs**: Review `workflow.log` for detailed execution logs
2. **Verify Prompts**: Ensure all 4 prompt files exist and have content
3. **Test API Keys**: Verify Claude and GitHub API keys are valid
4. **Check Repository**: Ensure target GitHub repository is accessible

### Error Messages

- **"Failed to read prompts"**: Check that P1-P4 files exist in Prompts/ directory
- **"API key invalid"**: Verify your Claude API key in `.env` file
- **"Repository not found"**: Check GitHub repository name and permissions
- **"Token limit exceeded"**: Wait and retry, or check your Claude API usage

## 🔧 Customization

### Modifying Generated Applications

You can customize the generated applications by:

1. **Editing Prompts**: Modify P1-P4 files to change requirements
2. **Adding New Components**: Extend the multi-call strategy in `claude_agent.py`
3. **Changing Frameworks**: Update prompts to use different Spring Boot versions
4. **Database Changes**: Modify P1 to use different databases (MySQL, Oracle, etc.)

### Extending the Workflow

You can extend the workflow by:

1. **Adding New API Calls**: Extend the 5-call strategy for more components
2. **Custom Agents**: Implement specialized agents for different technologies
3. **New Project Types**: Modify prompts for different application types
4. **Integration Testing**: Add automated testing of generated applications

### Configuration Options

```python
# In claude_agent.py - Adjust token limits
max_tokens=8192  # Maximum for Claude 3.5 Sonnet

# In workflow.py - Add new generation phases
def _generate_additional_components(self, prompts):
    # Add your custom generation logic
    pass
```

## 🎯 Current Limitations

### Known Issues
- **Package Completeness**: Some generated projects may have missing packages (config, exception, model, repository)
- **Compilation Errors**: Generated code may reference non-existent classes
- **Infrastructure Gaps**: Database entities and configurations may be incomplete

### Improvement Areas
- **Better Multi-Call Coordination**: Ensure all referenced classes are generated
- **Validation**: Add post-generation validation to check for compilation errors
- **Template System**: Implement template-based generation for consistency

## 🚀 Future Enhancements

### Planned Features
- **Frontend Generation**: Complete React/Angular frontend for eKYC applications
- **Microservices**: Generate complete microservices architecture
- **Cloud Deployment**: Add Kubernetes and cloud deployment configurations
- **API Documentation**: Enhanced OpenAPI/Swagger documentation generation
- **Performance Testing**: Automated performance test generation

### Contributing
1. Fork the repository
2. Create feature branches for improvements
3. Test with different prompt combinations
4. Submit pull requests with detailed descriptions

## 📈 Performance Metrics

### Typical Generation Times
- **Single Prompt**: ~30-40 seconds
- **Multi-Prompt (P1-P4)**: ~3-5 minutes
- **Complete Application**: 15-25 files generated
- **Code Quality**: Enterprise-grade with proper structure

### API Usage
- **Claude API Calls**: 5 calls per generation
- **Token Usage**: ~35,000-40,000 tokens total
- **GitHub API**: 15-25 file uploads per project

## 📄 License

This project is open source under the MIT License. Feel free to modify and distribute as needed.

## 🤝 Support

For issues and questions:

1. **Check Logs**: Review `workflow.log` for detailed error information
2. **Verify Configuration**: Ensure `.env` file is properly configured
3. **Test Components**: Verify individual components (Claude API, GitHub API)
4. **Community Support**: Create issues in the GitHub repository
5. **Documentation**: Refer to this README for troubleshooting steps

### Contact
- **Repository**: https://github.com/Pruthvi-TW/test-repo
- **Generated Projects**: Check the `generated_code/` directory
- **Logs**: Review `workflow.log` for execution details
