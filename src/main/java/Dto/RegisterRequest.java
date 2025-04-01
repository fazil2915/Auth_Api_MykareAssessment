package Dto;


import Entity.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private User.Gender gender;
    private User.Role role;
    private String token;
}
