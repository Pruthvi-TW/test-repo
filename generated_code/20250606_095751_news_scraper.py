```python
#!/usr/bin/env python3
"""
News Website Scraper

This script scrapes article titles and URLs from a specified news website using
requests and BeautifulSoup4. Results are saved to a JSON file.

Required packages:
- requests
- beautifulsoup4
"""

import requests
from bs4 import BeautifulSoup
import json
from datetime import datetime
import logging
from typing import Dict, List
import sys

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class NewsScraperError(Exception):
    """Custom exception for NewsScraper errors"""
    pass

class NewsScraper:
    def __init__(self, url: str, output_file: str = "news_articles.json"):
        """
        Initialize the NewsScraper.

        Args:
            url (str): The URL of the news website to scrape
            output_file (str): Name of the JSON file to save results
        """
        self.url = url
        self.output_file = output_file
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }

    def fetch_webpage(self) -> str:
        """
        Fetch the webpage content.

        Returns:
            str: HTML content of the webpage

        Raises:
            NewsScraperError: If there's an error fetching the webpage
        """
        try:
            response = requests.get(self.url, headers=self.headers, timeout=10)
            response.raise_for_status()
            return response.text
        except requests.RequestException as e:
            raise NewsScraperError(f"Error fetching webpage: {str(e)}")

    def parse_articles(self, html_content: str) -> List[Dict[str, str]]:
        """
        Parse article titles and URLs from HTML content.

        Args:
            html_content (str): HTML content to parse

        Returns:
            List[Dict[str, str]]: List of dictionaries containing article information
        """
        articles = []
        soup = BeautifulSoup(html_content, 'html.parser')
        
        # Note: These selectors should be modified based on the target website's structure
        article_elements = soup.find_all('article') or soup.find_all('div', class_='article')
        
        for article in article_elements:
            title_element = article.find('h2') or article.find('h3')
            link_element = article.find('a')
            
            if title_element and link_element:
                title = title_element.text.strip()
                url = link_element.get('href', '')
                
                # Handle relative URLs
                if url.startswith('/'):
                    url = f"{self.url.rstrip('/')}{url}"
                
                articles.append({
                    'title': title,
                    'url': url,
                    'scraped_at': datetime.now().isoformat()
                })
        
        return articles

    def save_to_json(self, articles: List[Dict[str, str]]) -> None:
        """
        Save scraped articles to a JSON file.

        Args:
            articles (List[Dict[str, str]]): List of article dictionaries to save
        """
        try:
            with open(self.output_file, 'w', encoding='utf-8') as f:
                json.dump(articles, f, indent=4, ensure_ascii=False)
            logger.info(f"Successfully saved {len(articles)} articles to {self.output_file}")
        except IOError as e:
            raise NewsScraperError(f"Error saving to JSON file: {str(e)}")

    def scrape(self) -> List[Dict[str, str]]:
        """
        Execute the scraping process.

        Returns:
            List[Dict[str, str]]: List of scraped articles
        """
        logger.info(f"Starting scrape of {self.url}")
        html_content = self.fetch_webpage()
        articles = self.parse_articles(html_content)
        self.save_to_json(articles)
        return articles

def main():
    """Main function to demonstrate usage"""
    # Example usage with a news website
    try:
        scraper = NewsScraper("https://example-news-site.com")
        articles = scraper.scrape()
        logger.info(f"Successfully scraped {len(articles)} articles")
    except NewsScraperError as e:
        logger.error(f"Scraping failed: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()
```