package com.todo.service;

import com.todo.dto.TodoRequest;
import com.todo.dto.TodoResponse;
import com.todo.exception.TodoNotFoundException;
import com.todo.model.Todo;
import com.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Captor
    private ArgumentCaptor<Todo> todoCaptor;

    private TodoRequest todoRequest;
    private Todo todo;
    private static final String USER_ID = "user123";
    private static final String TODO_ID = "todo123";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        todoRequest = TodoRequest.builder()
                .title("Test Todo")
                .description("Test Description")
                .build();

        todo = Todo.builder()
                .id(TODO_ID)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .userId(USER_ID)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createTodo_Success() {
        // Arrange
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        TodoResponse response = todoService.createTodo(todoRequest, USER_ID);

        // Assert
        verify(todoRepository).save(todoCaptor.capture());
        Todo capturedTodo = todoCaptor.getValue();
        
        assertNotNull(response);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todoRequest.getTitle(), capturedTodo.getTitle());
        assertEquals(todoRequest.getDescription(), capturedTodo.getDescription());
        assertEquals(USER_ID, capturedTodo.getUserId());
        assertFalse(capturedTodo.isCompleted());
        assertNotNull(capturedTodo.getCreatedAt());
        assertNotNull(capturedTodo.getUpdatedAt());
    }

    @Test
    void getAllTodosByUserId_Success() {
        // Arrange
        Todo todo2 = Todo.builder()
                .id("todo456")
                .title("Test Todo 2")
                .description("Test Description 2")
                .completed(true)
                .userId(USER_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.findByUserIdOrderByUpdatedAtDesc(USER_ID))
                .thenReturn(Arrays.asList(todo, todo2));

        // Act
        List<TodoResponse> responses = todoService.getAllTodosByUserId(USER_ID);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(todo.getId(), responses.get(0).getId());
        assertEquals(todo2.getId(), responses.get(1).getId());
    }

    @Test
    void getTodoById_Success() {
        // Arrange
        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.of(todo));

        // Act
        TodoResponse response = todoService.getTodoById(TODO_ID);

        // Assert
        assertNotNull(response);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertEquals(todo.getDescription(), response.getDescription());
        assertEquals(todo.isCompleted(), response.isCompleted());
    }

    @Test
    void getTodoById_NotFound() {
        // Arrange
        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class,
                () -> todoService.getTodoById(TODO_ID));
    }

    @Test
    void updateTodo_Success() {
        // Arrange
        TodoRequest updateRequest = TodoRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();

        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        TodoResponse response = todoService.updateTodo(TODO_ID, updateRequest);

        // Assert
        verify(todoRepository).save(todoCaptor.capture());
        Todo capturedTodo = todoCaptor.getValue();

        assertNotNull(response);
        assertEquals(updateRequest.getTitle(), capturedTodo.getTitle());
        assertEquals(updateRequest.getDescription(), capturedTodo.getDescription());
        assertNotNull(capturedTodo.getUpdatedAt());
    }

    @Test
    void updateTodo_NotFound() {
        // Arrange
        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class,
                () -> todoService.updateTodo(TODO_ID, todoRequest));
    }

    @Test
    void toggleTodoStatus_Success() {
        // Arrange
        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        TodoResponse response = todoService.toggleTodoStatus(TODO_ID);

        // Assert
        verify(todoRepository).save(todoCaptor.capture());
        Todo capturedTodo = todoCaptor.getValue();

        assertNotNull(response);
        assertTrue(capturedTodo.isCompleted()); // Should be toggled from false to true
        assertNotNull(capturedTodo.getUpdatedAt());
    }

    @Test
    void toggleTodoStatus_NotFound() {
        // Arrange
        when(todoRepository.findById(TODO_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class,
                () -> todoService.toggleTodoStatus(TODO_ID));
    }

    @Test
    void deleteTodo_Success() {
        // Arrange
        when(todoRepository.existsById(TODO_ID)).thenReturn(true);

        // Act
        todoService.deleteTodo(TODO_ID);

        // Assert
        verify(todoRepository).deleteById(TODO_ID);
    }

    @Test
    void deleteTodo_NotFound() {
        // Arrange
        when(todoRepository.existsById(TODO_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(TodoNotFoundException.class,
                () -> todoService.deleteTodo(TODO_ID));
        verify(todoRepository, never()).deleteById(any());
    }
}
