package Dto;

import Entity.User;
import lombok.Data;

@Data
public class authResponse {
     private String message;
     private String name;
     private String password;
     private String email;
     private User.Gender gender;
     private User.Role role;

}
