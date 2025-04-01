package Controller;

import Dto.AuthResponse;
import Dto.LoginRequest;
import Dto.LoginResponse;
import Dto.RegisterRequest;
import Entity.User;
import Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
    @Autowired
    AuthService authService;

    @Operation(summary = "Register a new user", description = "Registers a user and returns the user details")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User registeredUser = authService.registerUser(request);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error in register");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @Operation(summary = "Login a user", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "login failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
