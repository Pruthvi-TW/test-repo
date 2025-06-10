"""Simple main entry point for the automated code generation workflow."""

import logging
import sys
from simple_workflow import create_simple_workflow
from config import get_config

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('workflow.log')
    ]
)

logger = logging.getLogger(__name__)


def main():
    """Main function to run the automated workflow."""
    try:
        logger.info("=" * 50)
        logger.info("Starting Simple Automated Code Generation Workflow")
        logger.info("=" * 50)
        
        # Load and validate configuration
        logger.info("Loading configuration...")
        config = get_config()
        logger.info(f"Configuration loaded successfully")
        logger.info(f"Prompt file: {config.prompt_file_path}")
        logger.info(f"Target repository: {config.github_repo_owner}/{config.github_repo_name}")
        
        # Create and run workflow
        logger.info("Creating workflow...")
        workflow = create_simple_workflow()
        
        logger.info("Running workflow...")
        result = workflow.run()
        
        # Display results
        logger.info("=" * 50)
        if result["success"]:
            logger.info("✅ Workflow completed successfully!")
            logger.info(f"📝 Generated file: {result['generated_file']}")
            logger.info(f"🔗 GitHub file URL: {result['github_url']}")
            logger.info(f"📋 Commit URL: {result['commit_url']}")
            logger.info(f"📄 Description: {result['description']}")
            
            print("\n" + "=" * 50)
            print("🎉 SUCCESS! Code generated and pushed to GitHub")
            print("=" * 50)
            print(f"📝 Generated file: {result['generated_file']}")
            print(f"🔗 GitHub URL: {result['github_url']}")
            print(f"📋 Commit URL: {result['commit_url']}")
            print(f"📄 Description: {result['description']}")
            print("=" * 50)
            
        else:
            logger.error("❌ Workflow failed!")
            logger.error(f"Error message: {result['error']}")
            
            print("\n" + "=" * 50)
            print("❌ WORKFLOW FAILED")
            print("=" * 50)
            print(f"Error: {result['error']}")
            print("=" * 50)
            print("\nPlease check the following:")
            print("1. Your .env file is properly configured")
            print("2. Your API keys are valid")
            print("3. Your GitHub repository exists and is accessible")
            print("4. Your prompt file exists and contains content")
            
            sys.exit(1)
            
    except Exception as e:
        logger.error(f"Fatal error: {str(e)}")
        print(f"\n❌ Fatal error: {str(e)}")
        print("\nPlease check your configuration and try again.")
        sys.exit(1)


if __name__ == "__main__":
    main()
