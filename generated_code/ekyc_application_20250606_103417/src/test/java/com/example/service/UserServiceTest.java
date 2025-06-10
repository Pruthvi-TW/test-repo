```java
package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }

    @Test
    void createUser_ValidData_Success() {
        User savedUser = userService.createUser(testUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        userService.createUser(testUser);
        
        User duplicateUser = new User();
        duplicateUser.setEmail(testUser.getEmail());
        duplicateUser.setFirstName("Another");
        duplicateUser.setLastName("User");

        assertThrows(IllegalStateException.class, () -> 
            userService.createUser(duplicateUser)
        );
    }
}
```