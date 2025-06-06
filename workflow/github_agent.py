"""GitHub agent for repository operations."""

import logging
import os
from datetime import datetime
from typing import Dict, Any, Optional
from github import Github, GithubException
from config import WorkflowConfig

logger = logging.getLogger(__name__)


class GitHubAgent:
    """Agent for interacting with GitHub repositories."""
    
    def __init__(self, config: WorkflowConfig):
        """Initialize the GitHub agent."""
        self.config = config
        self.github = Github(config.github_token)
        self.repo = None
        
    def connect_to_repo(self) -> bool:
        """
        Connect to the specified GitHub repository.
        
        Returns:
            True if connection successful, False otherwise
        """
        try:
            repo_name = f"{self.config.github_repo_owner}/{self.config.github_repo_name}"
            self.repo = self.github.get_repo(repo_name)
            logger.info(f"Successfully connected to repository: {repo_name}")
            return True
        except GithubException as e:
            logger.error(f"Failed to connect to repository: {str(e)}")
            return False
    
    def create_file(self, file_path: str, content: str, commit_message: str) -> Dict[str, Any]:
        """
        Create a new file in the repository.
        
        Args:
            file_path: Path where the file should be created
            content: Content of the file
            commit_message: Commit message
            
        Returns:
            Dictionary with operation result
        """
        try:
            if not self.repo:
                if not self.connect_to_repo():
                    raise Exception("Failed to connect to repository")
            
            # Check if file already exists
            try:
                self.repo.get_contents(file_path)
                # File exists, update it instead
                return self.update_file(file_path, content, commit_message)
            except GithubException:
                # File doesn't exist, create it
                pass
            
            result = self.repo.create_file(
                path=file_path,
                message=commit_message,
                content=content
            )
            
            logger.info(f"Successfully created file: {file_path}")
            return {
                "success": True,
                "file_path": file_path,
                "commit_sha": result["commit"].sha,
                "commit_url": result["commit"].html_url,
                "file_url": result["content"].html_url
            }
            
        except GithubException as e:
            logger.error(f"Failed to create file {file_path}: {str(e)}")
            return {
                "success": False,
                "error": str(e),
                "file_path": file_path
            }
    
    def update_file(self, file_path: str, content: str, commit_message: str) -> Dict[str, Any]:
        """
        Update an existing file in the repository.
        
        Args:
            file_path: Path of the file to update
            content: New content of the file
            commit_message: Commit message
            
        Returns:
            Dictionary with operation result
        """
        try:
            if not self.repo:
                if not self.connect_to_repo():
                    raise Exception("Failed to connect to repository")
            
            # Get the current file to get its SHA
            file_contents = self.repo.get_contents(file_path)
            
            result = self.repo.update_file(
                path=file_path,
                message=commit_message,
                content=content,
                sha=file_contents.sha
            )
            
            logger.info(f"Successfully updated file: {file_path}")
            return {
                "success": True,
                "file_path": file_path,
                "commit_sha": result["commit"].sha,
                "commit_url": result["commit"].html_url,
                "file_url": result["content"].html_url
            }
            
        except GithubException as e:
            logger.error(f"Failed to update file {file_path}: {str(e)}")
            return {
                "success": False,
                "error": str(e),
                "file_path": file_path
            }
    
    def push_generated_code(self, code_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Push generated code to the repository.
        
        Args:
            code_data: Dictionary containing code information
            
        Returns:
            Dictionary with operation result
        """
        try:
            # Prepare file path
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = code_data.get("filename", "generated_code.py")
            file_path = f"{self.config.output_directory}/{timestamp}_{filename}"
            
            # Prepare commit message
            description = code_data.get("description", "Generated code")
            commit_message = f"{self.config.commit_message_prefix} {description}"
            
            # Create the file
            result = self.create_file(file_path, code_data["code"], commit_message)
            
            if result["success"]:
                logger.info(f"Successfully pushed generated code to {file_path}")
            
            return result
            
        except Exception as e:
            logger.error(f"Failed to push generated code: {str(e)}")
            return {
                "success": False,
                "error": str(e)
            }
    
    def get_repo_info(self) -> Optional[Dict[str, Any]]:
        """
        Get information about the connected repository.
        
        Returns:
            Dictionary with repository information or None if not connected
        """
        try:
            if not self.repo:
                if not self.connect_to_repo():
                    return None
            
            return {
                "name": self.repo.name,
                "full_name": self.repo.full_name,
                "description": self.repo.description,
                "url": self.repo.html_url,
                "default_branch": self.repo.default_branch,
                "private": self.repo.private
            }
            
        except Exception as e:
            logger.error(f"Failed to get repository info: {str(e)}")
            return None
