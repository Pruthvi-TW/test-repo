```python
from flask import Flask, request, jsonify
from datetime import datetime
from typing import Dict, List, Optional
import uuid

app = Flask(__name__)

# In-memory storage for todos
# In production, this should be replaced with a proper database
todos: Dict[str, dict] = {}

class ValidationError(Exception):
    """Custom exception for validation errors"""
    pass

def validate_todo(data: dict) -> None:
    """
    Validate todo data
    
    Args:
        data (dict): Todo data to validate
        
    Raises:
        ValidationError: If validation fails
    """
    if not isinstance(data.get('title'), str) or not data.get('title').strip():
        raise ValidationError("Title is required and must be a non-empty string")
    
    if 'completed' in data and not isinstance(data['completed'], bool):
        raise ValidationError("Completed must be a boolean value")

@app.route('/api/todos', methods=['POST'])
def create_todo():
    """Create a new todo item"""
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No data provided"}), 400

        validate_todo(data)
        
        todo_id = str(uuid.uuid4())
        todo = {
            'id': todo_id,
            'title': data['title'],
            'completed': data.get('completed', False),
            'created_at': datetime.utcnow().isoformat(),
            'updated_at': datetime.utcnow().isoformat()
        }
        
        todos[todo_id] = todo
        return jsonify(todo), 201

    except ValidationError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error"}), 500

@app.route('/api/todos', methods=['GET'])
def get_todos():
    """Get all todo items"""
    try:
        return jsonify(list(todos.values())), 200
    except Exception as e:
        return jsonify({"error": "Internal server error"}), 500

@app.route('/api/todos/<todo_id>', methods=['GET'])
def get_todo(todo_id: str):
    """Get a specific todo item by ID"""
    try:
        todo = todos.get(todo_id)
        if not todo:
            return jsonify({"error": "Todo not found"}), 404
        return jsonify(todo), 200
    except Exception as e:
        return jsonify({"error": "Internal server error"}), 500

@app.route('/api/todos/<todo_id>', methods=['PUT'])
def update_todo(todo_id: str):
    """Update a specific todo item"""
    try:
        if todo_id not in todos:
            return jsonify({"error": "Todo not found"}), 404

        data = request.get_json()
        if not data:
            return jsonify({"error": "No data provided"}), 400

        validate_todo(data)
        
        todo = todos[todo_id]
        todo['title'] = data['title']
        todo['completed'] = data.get('completed', todo['completed'])
        todo['updated_at'] = datetime.utcnow().isoformat()
        
        return jsonify(todo), 200

    except ValidationError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error"}), 500

@app.route('/api/todos/<todo_id>', methods=['DELETE'])
def delete_todo(todo_id: str):
    """Delete a specific todo item"""
    try:
        if todo_id not in todos:
            return jsonify({"error": "Todo not found"}), 404
            
        del todos[todo_id]
        return jsonify({"message": "Todo deleted successfully"}), 200
    except Exception as e:
        return jsonify({"error": "Internal server error"}), 500

if __name__ == '__main__':
    app.run(debug=True)
```