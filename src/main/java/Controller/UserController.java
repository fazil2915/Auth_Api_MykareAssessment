package Controller;


import Config.JwtUtil;
import Entity.User;
import Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private AuthService authService;
    @Operation(summary = "Get all users", description = "Returns a list of all registered users. Admin role required.")
    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            List<User> users = authService.getAllUsers(token);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    @Operation(summary = "Delete a user", description = "Deletes a user by email. Only admins can perform this action.")
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String email) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isDeleted = authService.deleteUserByEmail(token, email);
            System.out.println("Deleted: " + isDeleted);

            if (isDeleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized");
                errorResponse.put("message", "Only admins can delete users");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deleting user");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}