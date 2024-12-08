import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { TodoService } from '../services/todoService';
import { useNavigate } from 'react-router-dom';
import '../styles/Todo.css';

interface Todo {
  id: string;
  title: string;
  description: string;
  completed: boolean;
}

const TodoList = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [newTitle, setNewTitle] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const { token, logout } = useAuth();
  const navigate = useNavigate();
  const todoService = new TodoService();

  useEffect(() => {
    loadTodos();
  }, []);

  const loadTodos = async () => {
    try {
      const response = await todoService.getTodos();
      setTodos(response.data);
    } catch (error) {
      console.error('Error loading todos:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    try {
      const response = await todoService.createTodo(newTitle, newDescription);
      setTodos([...todos, response.data]);
      setNewTitle('');
      setNewDescription('');
    } catch (error) {
      console.error('Error creating todo:', error);
    }
  };

  const toggleTodo = async (id: string) => {
    try {
      await todoService.toggleTodo(id);
      setTodos(todos.map(todo =>
        todo.id === id ? { ...todo, completed: !todo.completed } : todo
      ));
    } catch (error) {
      console.error('Error toggling todo:', error);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      <div className="logout-container">
        <button className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </div>
      <div className="todo-container">
        <div className="todo-header">
          <h2>My Todo List</h2>
        </div>
        <form onSubmit={handleSubmit} className="todo-form">
          <div className="todo-inputs">
            <input
              type="text"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              placeholder="Title"
              className="todo-input"
              required
            />
            <input
              type="text"
              value={newDescription}
              onChange={(e) => setNewDescription(e.target.value)}
              placeholder="Description"
              className="todo-input"
            />
          </div>
          <button type="submit" className="todo-button">Add</button>
        </form>
        <ul className="todo-list">
          {todos.map((todo) => (
            <li key={todo.id} className={`todo-item ${todo.completed ? 'completed' : ''}`}>
              <div className="todo-content">
                <h3 className="todo-title" title={todo.title}>
                  {todo.title}
                </h3>
                <p className="todo-description" title={todo.description}>
                  {todo.description}
                </p>
              </div>
              <button
                onClick={() => toggleTodo(todo.id)}
                className="toggle-button"
              >
                {todo.completed ? '✓' : '○'}
              </button>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
};

export default TodoList;
