package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.config.TestSecurityConfig;
import com.todo.dto.TodoRequest;
import com.todo.dto.TodoResponse;
import com.todo.exception.TodoNotFoundException;
import com.todo.security.JwtAuthenticationFilter;
import com.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}))
@Import({TodoExceptionHandler.class, TestSecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoRequest todoRequest;
    private TodoResponse todoResponse;
    private static final String TEST_USER = "testuser";

    @BeforeEach
    void setUp() {
        todoRequest = TodoRequest.builder()
                .title("Test Todo")
                .description("Test Description")
                .build();

        todoResponse = TodoResponse.builder()
                .id("1")
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void getAllTodos_ShouldReturnTodoList() throws Exception {
        List<TodoResponse> todos = Arrays.asList(todoResponse);
        when(todoService.getAllTodosByUserId(TEST_USER)).thenReturn(todos);

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(todoResponse.getId()))
                .andExpect(jsonPath("$[0].title").value(todoResponse.getTitle()))
                .andExpect(jsonPath("$[0].description").value(todoResponse.getDescription()))
                .andExpect(jsonPath("$[0].completed").value(todoResponse.isCompleted()));

        verify(todoService).getAllTodosByUserId(TEST_USER);
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void createTodo_WithValidRequest_ShouldReturnCreatedTodo() throws Exception {
        when(todoService.createTodo(any(TodoRequest.class), eq(TEST_USER))).thenReturn(todoResponse);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(todoResponse.getDescription()))
                .andExpect(jsonPath("$.completed").value(todoResponse.isCompleted()));

        verify(todoService).createTodo(any(TodoRequest.class), eq(TEST_USER));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void getTodoById_WithExistingId_ShouldReturnTodo() throws Exception {
        when(todoService.getTodoById("1")).thenReturn(todoResponse);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(todoResponse.getDescription()))
                .andExpect(jsonPath("$.completed").value(todoResponse.isCompleted()));

        verify(todoService).getTodoById("1");
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void updateTodo_WithValidRequest_ShouldReturnUpdatedTodo() throws Exception {
        when(todoService.updateTodo(eq("1"), any(TodoRequest.class))).thenReturn(todoResponse);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(todoResponse.getDescription()))
                .andExpect(jsonPath("$.completed").value(todoResponse.isCompleted()));

        verify(todoService).updateTodo(eq("1"), any(TodoRequest.class));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void toggleTodoStatus_WithExistingId_ShouldReturnToggledTodo() throws Exception {
        TodoResponse toggledResponse = TodoResponse.builder()
                .id("1")
                .title("Test Todo")
                .description("Test Description")
                .completed(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.toggleTodoStatus("1")).thenReturn(toggledResponse);

        mockMvc.perform(put("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toggledResponse.getId()))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).toggleTodoStatus("1");
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void deleteTodo_WithExistingId_ShouldReturnOk() throws Exception {
        doNothing().when(todoService).deleteTodo("1");

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).deleteTodo("1");
    }
}
