package com.todo.repository;

import com.todo.model.Todo;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends CouchbaseRepository<Todo, String> {
    List<Todo> findByUserId(String userId);
    List<Todo> findByUserIdOrderByUpdatedAtDesc(String userId);
}
