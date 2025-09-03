package com.example.todoapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Document(collection = "todos")
public class Todo {
    @Id
    private String id;
    private String title;
    private String description;
    private List<String> tags;
    private boolean done;

    // Constructor های مختلف
    public Todo() {
        this.tags = new ArrayList<>();
        this.done = false;
    }

    public Todo(String title) {
        this();
        this.title = title;
    }

    public Todo(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public Todo(String title, String description, List<String> tags, boolean done) {
        this.title = title;
        this.description = description;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.done = done;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    // برای wrapper Boolean هم
    public void setDone(Boolean done) {
        this.done = done != null ? done : false;
    }
}