package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(com.example.todoapp.controller.TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllTodos() throws Exception {
        // ایجاد todo با constructor درست
        Todo todo = new Todo("Test Title", "Test Description", List.of("tag1"), false);
        when(repository.findAll()).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].done").value(false));
    }

    @Test
    public void testCreateTodo() throws Exception {
        Todo newTodo = new Todo("New Task", "Task description", List.of("work"), false);
        Todo savedTodo = new Todo("New Task", "Task description", List.of("work"), false);
        savedTodo.setId("123");

        when(repository.save(any(Todo.class))).thenReturn(savedTodo);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    public void testCreateTodoWithEmptyTitle() throws Exception {
        Todo invalidTodo = new Todo("", "Description", List.of(), false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateTodo() throws Exception {
        String todoId = "123";
        Todo existingTodo = new Todo("Old Title", "Old Description", List.of("old"), false);
        existingTodo.setId(todoId);

        Todo updatedTodo = new Todo("Updated Title", "Updated Description", List.of("new"), true);
        Todo savedTodo = new Todo("Updated Title", "Updated Description", List.of("new"), true);
        savedTodo.setId(todoId);

        when(repository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(repository.save(any(Todo.class))).thenReturn(savedTodo);

        mockMvc.perform(put("/api/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    public void testUpdateNonExistentTodo() throws Exception {
        String todoId = "nonexistent";
        Todo updatedTodo = new Todo("Title", "Description", List.of(), false);

        when(repository.findById(todoId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTodo() throws Exception {
        String todoId = "123";

        when(repository.existsById(todoId)).thenReturn(true);
        doNothing().when(repository).deleteById(todoId);

        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(todoId);
    }

    @Test
    public void testDeleteNonExistentTodo() throws Exception {
        String todoId = "nonexistent";

        when(repository.existsById(todoId)).thenReturn(false);

        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isNotFound());

        verify(repository, never()).deleteById(todoId);
    }
}