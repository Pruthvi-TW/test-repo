"""
Utility functions for reading and processing prompts.
"""
import os
import re
from typing import List, Dict, Any, Tuple, Optional
from config import Config

class PromptReader:
    """Handles reading and processing of prompt files."""
    
    def __init__(self):
        self.prompts_dir = Config.PROMPTS_DIR
        self.prompt_files = Config.PROMPT_FILES
    
    def read_prompt(self, prompt_file: str) -> str:
        """Read a single prompt file and return its content."""
        prompt_path = Config.get_prompt_path(prompt_file)
        
        if not os.path.exists(prompt_path):
            raise FileNotFoundError(f"Prompt file not found: {prompt_path}")
        
        with open(prompt_path, 'r', encoding='utf-8') as file:
            content = file.read().strip()
        
        return content
    
    def read_all_prompts(self) -> Dict[str, str]:
        """Read all prompt files and return as a dictionary."""
        prompts = {}
        
        for prompt_file in self.prompt_files:
            try:
                content = self.read_prompt(prompt_file)
                # Extract the key from filename (e.g., "P1-PreTech.txt" -> "P1")
                key = prompt_file.split('-')[0]
                prompts[key] = content
            except FileNotFoundError as e:
                print(f"Warning: {e}")
                continue
        
        return prompts
    
    def get_ordered_prompts(self) -> List[Dict[str, Any]]:
        """Get prompts in the specified order (P1, P2, P3, P4)."""
        prompts = self.read_all_prompts()
        ordered_prompts = []
        
        for i, prompt_file in enumerate(self.prompt_files, 1):
            key = prompt_file.split('-')[0]
            if key in prompts:
                ordered_prompts.append({
                    'order': i,
                    'key': key,
                    'filename': prompt_file,
                    'content': prompts[key],
                    'type': self._get_prompt_type(key)
                })
        
        return ordered_prompts
    
    def _get_prompt_type(self, key: str) -> str:
        """Determine the type of prompt based on the key."""
        prompt_types = {
            'P1': 'technical_guidelines',
            'P2': 'business_requirements',
            'P3': 'post_technical_requirements',
            'P4': 'additional_specifications'
        }
        return prompt_types.get(key, 'unknown')
    
    def combine_prompts(self, prompts: List[Dict[str, Any]]) -> str:
        """Combine multiple prompts into a single context string."""
        combined = []
        
        for prompt in prompts:
            combined.append(f"=== {prompt['key']}: {prompt['type'].replace('_', ' ').title()} ===")
            combined.append(prompt['content'])
            combined.append("")  # Empty line for separation
        
        return "\n".join(combined)
    
    def detect_technology_stack(self, prompts: List[Dict[str, Any]]) -> Dict[str, Any]:
        """Detect programming language and framework from prompts with priority on explicit specifications."""

        # First, check for explicit technology specifications in P1 (technical guidelines)
        p1_content = ""
        for prompt in prompts:
            if prompt.get('key') == 'P1' or prompt.get('type') == 'technical_guidelines':
                p1_content = prompt.get('content', '').lower()
                break

        # Check for explicit technology specifications in P1
        explicit_tech = self._detect_explicit_technology(p1_content)
        if explicit_tech['language'] != 'unknown':
            return explicit_tech

        # Fallback to pattern-based detection if no explicit specification
        combined_content = self.combine_prompts(prompts).lower()

        # Language detection patterns
        language_patterns = {
            'java': [r'\bjava\b', r'spring\s*boot', r'maven', r'gradle'],
            'python': [r'\bpython\b', r'django', r'flask', r'fastapi', r'pip', r'requirements\.txt'],
            'golang': [r'\bgo\b', r'\bgolang\b', r'gin', r'fiber', r'go\.mod'],
            'javascript': [r'\bjavascript\b', r'\bjs\b', r'node\.?js', r'express', r'npm'],
            'typescript': [r'\btypescript\b', r'\bts\b', r'nestjs'],
            'csharp': [r'\bc#\b', r'\bcsharp\b', r'\.net', r'asp\.net'],
            'rust': [r'\brust\b', r'cargo', r'actix'],
            'kotlin': [r'\bkotlin\b', r'ktor'],
            'scala': [r'\bscala\b', r'akka', r'play'],
            'php': [r'\bphp\b', r'laravel', r'symfony', r'composer']
        }

        # Framework detection patterns
        framework_patterns = {
            'spring-boot': [r'spring\s*boot', r'@springbootapplication'],
            'django': [r'\bdjango\b'],
            'flask': [r'\bflask\b'],
            'fastapi': [r'\bfastapi\b'],
            'gin': [r'\bgin\b', r'gin-gonic'],
            'fiber': [r'\bfiber\b'],
            'express': [r'\bexpress\b.*js'],
            'nestjs': [r'\bnestjs\b', r'nest\.js'],
            'asp.net': [r'asp\.net', r'\.net\s*core'],
            'laravel': [r'\blaravel\b'],
            'rails': [r'ruby\s*on\s*rails', r'\brails\b']
        }

        detected_language = self._detect_from_patterns(combined_content, language_patterns)
        detected_framework = self._detect_from_patterns(combined_content, framework_patterns)

        # Fix cross-language framework detection
        detected_framework = self._fix_framework_language_mismatch(detected_language, detected_framework)

        # Additional context extraction
        database = self._detect_database(combined_content)
        build_tool = self._detect_build_tool(combined_content, detected_language)

        return {
            'language': detected_language or 'unknown',
            'framework': detected_framework or 'unknown',
            'database': database,
            'build_tool': build_tool,
            'confidence': self._calculate_confidence(combined_content, detected_language, detected_framework)
        }

    def _detect_from_patterns(self, content: str, patterns: Dict[str, List[str]]) -> Optional[str]:
        """Detect technology from regex patterns."""
        scores = {}

        for tech, pattern_list in patterns.items():
            score = 0
            for pattern in pattern_list:
                matches = len(re.findall(pattern, content, re.IGNORECASE))
                score += matches
            scores[tech] = score

        # Return the technology with highest score
        if scores:
            best_match = max(scores, key=scores.get)
            return best_match if scores[best_match] > 0 else None

        return None

    def _detect_database(self, content: str) -> str:
        """Detect database technology from content."""
        db_patterns = {
            'postgresql': [r'postgresql', r'postgres', r'psql'],
            'mysql': [r'\bmysql\b'],
            'mongodb': [r'\bmongodb\b', r'\bmongo\b'],
            'sqlite': [r'\bsqlite\b'],
            'redis': [r'\bredis\b'],
            'oracle': [r'\boracle\b'],
            'sqlserver': [r'sql\s*server', r'mssql']
        }

        return self._detect_from_patterns(content, db_patterns) or 'unknown'

    def _detect_build_tool(self, content: str, language: str) -> str:
        """Detect build tool based on content and language."""
        build_patterns = {
            'maven': [r'\bmaven\b', r'pom\.xml'],
            'gradle': [r'\bgradle\b', r'build\.gradle'],
            'npm': [r'\bnpm\b', r'package\.json'],
            'yarn': [r'\byarn\b'],
            'pip': [r'\bpip\b', r'requirements\.txt'],
            'poetry': [r'\bpoetry\b', r'pyproject\.toml'],
            'cargo': [r'\bcargo\b', r'cargo\.toml'],
            'go-mod': [r'go\.mod', r'go\s*mod'],
            'composer': [r'\bcomposer\b', r'composer\.json']
        }

        detected = self._detect_from_patterns(content, build_patterns)

        # Default build tools based on language
        if not detected:
            defaults = {
                'java': 'maven',
                'python': 'pip',
                'golang': 'go-mod',
                'javascript': 'npm',
                'typescript': 'npm',
                'rust': 'cargo',
                'php': 'composer'
            }
            detected = defaults.get(language, 'unknown')

        return detected

    def _calculate_confidence(self, content: str, language: str, framework: str) -> float:
        """Calculate confidence score for detection."""
        if not language or language == 'unknown':
            return 0.0

        # Count total technology mentions
        total_mentions = len(re.findall(r'\b(?:java|python|golang|javascript|typescript|csharp|rust|kotlin|scala|php)\b', content, re.IGNORECASE))

        if total_mentions == 0:
            return 0.0

        # Count mentions of detected language
        language_mentions = len(re.findall(rf'\b{language}\b', content, re.IGNORECASE))

        confidence = language_mentions / total_mentions

        # Boost confidence if framework is also detected
        if framework and framework != 'unknown':
            confidence = min(1.0, confidence * 1.2)

        return round(confidence, 2)

    def _fix_framework_language_mismatch(self, language: str, framework: str) -> str:
        """Fix framework-language mismatches."""
        if not language or not framework:
            return framework

        # Language-specific framework mappings
        language_frameworks = {
            'java': ['spring-boot'],
            'python': ['django', 'flask', 'fastapi'],
            'golang': ['gin', 'fiber'],
            'javascript': ['express', 'nestjs'],
            'typescript': ['nestjs', 'express'],
            'csharp': ['asp.net'],
            'php': ['laravel'],
            'ruby': ['rails']
        }

        valid_frameworks = language_frameworks.get(language.lower(), [])

        # If detected framework is not valid for the language, pick a default
        if framework not in valid_frameworks:
            if language.lower() == 'golang':
                return 'gin'  # Default Go framework
            elif language.lower() == 'java':
                return 'spring-boot'  # Default Java framework
            elif language.lower() == 'python':
                return 'fastapi'  # Default Python framework
            elif language.lower() in ['javascript', 'typescript']:
                return 'express'  # Default Node.js framework
            elif language.lower() == 'csharp':
                return 'asp.net'  # Default .NET framework

        return framework

    def _detect_explicit_technology(self, p1_content: str) -> Dict[str, Any]:
        """Detect explicitly specified technology from P1 technical guidelines."""

        # Initialize with unknown values
        result = {
            'language': 'unknown',
            'framework': 'unknown',
            'database': 'unknown',
            'build_tool': 'unknown',
            'confidence': 1.0  # High confidence for explicit specifications
        }

        # Explicit language detection patterns
        if 'use java' in p1_content or 'java (' in p1_content:
            result['language'] = 'java'

            # Check for Spring Boot
            if 'spring boot' in p1_content or 'spring-boot' in p1_content:
                result['framework'] = 'spring-boot'

            # Check for build tool
            if 'maven' in p1_content:
                result['build_tool'] = 'maven'
            elif 'gradle' in p1_content:
                result['build_tool'] = 'gradle'

        elif 'use python' in p1_content or 'python (' in p1_content:
            result['language'] = 'python'

            if 'django' in p1_content:
                result['framework'] = 'django'
            elif 'flask' in p1_content:
                result['framework'] = 'flask'
            elif 'fastapi' in p1_content:
                result['framework'] = 'fastapi'

        elif 'use go' in p1_content or 'use golang' in p1_content or 'golang (' in p1_content:
            result['language'] = 'golang'

            if 'gin' in p1_content:
                result['framework'] = 'gin'
            elif 'fiber' in p1_content:
                result['framework'] = 'fiber'

        # Database detection
        if 'postgresql' in p1_content or 'postgres' in p1_content:
            result['database'] = 'postgresql'
        elif 'mysql' in p1_content:
            result['database'] = 'mysql'
        elif 'mongodb' in p1_content or 'mongo' in p1_content:
            result['database'] = 'mongodb'

        # Set default build tools and frameworks based on language
        if result['language'] == 'java':
            if result['build_tool'] == 'unknown':
                result['build_tool'] = 'maven'
            if result['framework'] == 'unknown':
                result['framework'] = 'spring-boot'
        elif result['language'] == 'python':
            if result['build_tool'] == 'unknown':
                result['build_tool'] = 'pip'
            if result['framework'] == 'unknown':
                result['framework'] = 'fastapi'
        elif result['language'] == 'golang':
            if result['build_tool'] == 'unknown':
                result['build_tool'] = 'go-mod'
            if result['framework'] == 'unknown':
                result['framework'] = 'gin'

        return result
