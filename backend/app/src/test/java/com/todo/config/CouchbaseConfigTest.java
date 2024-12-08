package com.todo.config;

import com.couchbase.client.core.env.SecurityConfig;
import com.couchbase.client.java.env.ClusterEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class CouchbaseConfigTest {

    private CouchbaseConfig couchbaseConfig;

    @BeforeEach
    void setUp() {
        couchbaseConfig = new CouchbaseConfig();
        ReflectionTestUtils.setField(couchbaseConfig, "connectionString", "couchbase://localhost");
        ReflectionTestUtils.setField(couchbaseConfig, "bucketName", "todo-bucket");
        ReflectionTestUtils.setField(couchbaseConfig, "username", "admin");
        ReflectionTestUtils.setField(couchbaseConfig, "password", "password");
        ReflectionTestUtils.setField(couchbaseConfig, "enableTls", true);
        ReflectionTestUtils.setField(couchbaseConfig, "enablePlainSasl", true);
    }

    @Test
    void getConnectionString_ShouldReturnConfiguredValue() {
        assertEquals("couchbase://localhost", couchbaseConfig.getConnectionString());
    }

    @Test
    void getUserName_ShouldReturnConfiguredValue() {
        assertEquals("admin", couchbaseConfig.getUserName());
    }

    @Test
    void getPassword_ShouldReturnConfiguredValue() {
        assertEquals("password", couchbaseConfig.getPassword());
    }

    @Test
    void getBucketName_ShouldReturnConfiguredValue() {
        assertEquals("todo-bucket", couchbaseConfig.getBucketName());
    }

    @Test
    void configureEnvironment_ShouldConfigureSecurityWithTls() {
        // Given
        ClusterEnvironment.Builder builder = ClusterEnvironment.builder();
        
        // When
        couchbaseConfig.configureEnvironment(builder);
        ClusterEnvironment environment = builder.build();
        
        // Then
        assertTrue(environment.securityConfig().tlsEnabled());
        
        // Cleanup
        environment.shutdown();
    }
}
