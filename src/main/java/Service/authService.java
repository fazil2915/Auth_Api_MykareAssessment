package Service;

import Dto.authResponse;
import Entity.User;
import Exceptions.EmailAlreadyExistsException;
import Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class authService {
    private final UserRepo userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public authService(UserRepo userRepository, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(authResponse request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String ip = fetchUserIP();
        String country = fetchUserCountry(ip);

        User newUser = User.builder()
                .id(UUID.randomUUID())  // Generate UUID
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .ipAddress(ip)
                .country(country)
                .role(request.getRole())
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

    public User login()
}
