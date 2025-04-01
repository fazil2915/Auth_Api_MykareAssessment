package Service;

import Config.JwtUtil;
import Dto.LoginRequest;
import Dto.LoginResponse;
import Dto.RegisterRequest;
import Entity.User;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.InvalidCredentialsException;
import Repository.UserRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service

public class AuthService {
    private final UserRepo userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepo userRepository, RestTemplate restTemplate,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        String ip=fetchUserIP();
        String country=fetchUserCountry(ip);
        User newUser = User.builder()

                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .gender(request.getGender())
                .ipAddress(ip)
                .country(country)
                .build();

        return userRepository.save(newUser);
    }

    private String fetchUserIP() {
        return restTemplate.getForObject("https://api64.ipify.org?format=text", String.class);
    }

    private String fetchUserCountry(String ip) {
        Map<String, String> response = restTemplate.getForObject("http://ip-api.com/json/" + ip, Map.class);
        return response != null ? response.get("country") : "Unknown";
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, "Login successful");
    }

    public List<User> getAllUsers(String token)  {
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied: Only admins can view users");
        }
        return userRepository.findAll();
    }

    public boolean deleteUserByEmail(String token, String email) {
        String role = jwtUtil.extractRole(token);
        String requestingEmail = jwtUtil.extractEmail(token);

        Optional<User> requestingUser = userRepository.findByEmail(requestingEmail);

        if (requestingUser.isPresent() && "ADMIN".equals(role)) {
            Optional<User> userToDelete = userRepository.findByEmail(email);

            if (userToDelete.isPresent()) {
                userRepository.delete(userToDelete.get());
                return true;
            } else {
                System.out.println(" User to delete not found: " + email);
            }
        } else {
            System.out.println(" Requesting User is not an ADMIN");
        }
        return false;
    }

}
