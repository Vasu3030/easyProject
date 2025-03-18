package vasu.easyproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vasu.easyproject.model.User;
import vasu.easyproject.dto.UserResponseDTO;
import vasu.easyproject.service.UserService;
import vasu.easyproject.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.saveUser(user);  // L'ID est maintenant généré

        String token = jwtUtil.generateToken(savedUser.getUsername());

        UserResponseDTO userResponse = new UserResponseDTO(
            savedUser.getId(), 
            savedUser.getUsername(),
            savedUser.getEmail(),
            token
        );

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());

        if (existingUser == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Boolean validCredentials = userService.validateCredentials(existingUser.getPassword(), user.getPassword());

        if (validCredentials) {
            String token = jwtUtil.generateToken(existingUser.getUsername());
            UserResponseDTO userResponse = new UserResponseDTO(
            existingUser.getId(), 
            existingUser.getUsername(),
            existingUser.getEmail(),
            token
            );
            return ResponseEntity.ok(userResponse);
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
