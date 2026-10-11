
package com.plagiarism.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plagiarism.dto.LoginRequest;
import com.plagiarism.dto.RegisterRequest;
import com.plagiarism.entity.User;
import com.plagiarism.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "http://localhost:*")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.getName() == null
                || request.getEmail() == null
                || request.getPassword() == null
                || request.getName().isBlank()
                || request.getEmail().isBlank()
                || request.getPassword().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "All fields are required."));
        }

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "Email is already registered."));
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful.");
        response.put("email", user.getEmail());
        response.put("name", user.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        if (request.getEmail() == null
                || request.getPassword() == null
                || request.getEmail().isBlank()
                || request.getPassword().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Email and password are required."));
        }

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid email or password."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful.");
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }
}