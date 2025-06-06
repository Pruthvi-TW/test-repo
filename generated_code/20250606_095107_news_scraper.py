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
    def __init__(self, url: str):
        """
        Initialize the NewsScraper with target URL.

        Args:
            url (str): The URL of the news website to scrape
        """
        self.url = url
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }

    def fetch_page(self) -> str:
        """
        Fetch the webpage content.

        Returns:
            str: HTML content of the page

        Raises:
            NewsScraperError: If there's an error fetching the page
        """
        try:
            response = requests.get(self.url, headers=self.headers, timeout=10)
            response.raise_for_status()
            return response.text
        except requests.RequestException as e:
            raise NewsScraperError(f"Error fetching page: {str(e)}")

    def parse_articles(self, html_content: str) -> List[Dict[str, str]]:
        """
        Parse article titles and URLs from HTML content.

        Args:
            html_content (str): HTML content to parse

        Returns:
            List[Dict[str, str]]: List of dictionaries containing article info
        """
        articles = []
        soup = BeautifulSoup(html_content, 'html.parser')
        
        # Note: These selectors should be adjusted based on the target website's structure
        article_elements = soup.find_all('article') or soup.find_all('div', class_='article')
        
        for article in article_elements:
            title_element = article.find('h2') or article.find('h3')
            link_element = article.find('a')
            
            if title_element and link_element:
                articles.append({
                    'title': title_element.text.strip(),
                    'url': link_element.get('href', ''),
                    'timestamp': datetime.now().isoformat()
                })
        
        return articles

    def save_to_json(self, articles: List[Dict[str, str]], filename: str) -> None:
        """
        Save scraped articles to a JSON file.

        Args:
            articles (List[Dict[str, str]]): List of article dictionaries
            filename (str): Output filename

        Raises:
            NewsScraperError: If there's an error saving the file
        """
        try:
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(articles, f, indent=4, ensure_ascii=False)
            logger.info(f"Successfully saved {len(articles)} articles to {filename}")
        except IOError as e:
            raise NewsScraperError(f"Error saving to JSON file: {str(e)}")

    def run(self, output_file: str) -> None:
        """
        Run the scraper and save results.

        Args:
            output_file (str): Path to save the JSON output
        """
        try:
            logger.info(f"Starting scrape of {self.url}")
            html_content = self.fetch_page()
            articles = self.parse_articles(html_content)
            
            if not articles:
                logger.warning("No articles found!")
                return
            
            self.save_to_json(articles, output_file)
            logger.info("Scraping completed successfully")
            
        except NewsScraperError as e:
            logger.error(f"Scraping failed: {str(e)}")
            sys.exit(1)

def main():
    """Example usage of the NewsScraper class"""
    # Example news website (replace with actual target website)
    news_url = "https://example-news-site.com"
    output_file = f"news_articles_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    
    scraper = NewsScraper(news_url)
    scraper.run(output_file)

if __name__ == "__main__":
    main()
```