package com.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Todo {
    
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;
    
    @Field
    private String title;
    
    @Field
    private String description;
    
    @Field
    private boolean completed;
    
    @Field
    private LocalDateTime createdAt;
    
    @Field
    private LocalDateTime updatedAt;
    
    @Field
    private String userId;
}
