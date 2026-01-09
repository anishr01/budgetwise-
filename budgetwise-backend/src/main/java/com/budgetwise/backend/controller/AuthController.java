package com.budgetwise.backend.controller;

import com.budgetwise.backend.dto.LoginRequest;
import com.budgetwise.backend.model.User;
import com.budgetwise.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("===== REGISTER API HIT =====");
            System.out.println("FullName = " + user.getFullName());
            System.out.println("Email = " + user.getEmail());
            System.out.println("Password = " + user.getPassword());

            if (userRepository.existsByEmail(user.getEmail())) {
                System.out.println("EMAIL EXISTS IN DB");
                return ResponseEntity.status(409).body("EMAIL_EXISTS");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            System.out.println("USER SAVED SUCCESSFULLY");

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            System.out.println("🔥 REGISTER FAILED 🔥");
            e.printStackTrace();  // THIS WILL EXPOSE THE REAL ISSUE
            return ResponseEntity.status(500).body("REGISTER_FAILED");
        }
    }


    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("USER_NOT_FOUND");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("INVALID_PASSWORD");
        }

        return ResponseEntity.ok(user);
    }
}

