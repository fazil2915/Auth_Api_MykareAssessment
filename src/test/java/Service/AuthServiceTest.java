package Service;

import Config.JwtUtil;
import Dto.LoginRequest;
import Dto.LoginResponse;
import Dto.RegisterRequest;
import Entity.User;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.InvalidCredentialsException;
import Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito
class AuthServiceTest {

    @Mock // Mock dependencies
    private UserRepo userRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("hashedpassword")
                .role(User.Role.valueOf("ADMIN"))
                .gender(User.Gender.valueOf("MALE"))
                .ipAddress("127.0.0.1")
                .country("India")
                .build();
    }

    // ✅ 1. Test User Registration
    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("securePassword");
        request.setRole(User.Role.USER);
        request.setGender(User.Gender.MALE);
        request.setToken("mockToken");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = authService.registerUser(request);

        assertNotNull(registeredUser);
        assertEquals("john@example.com", registeredUser.getEmail());
        assertEquals(User.Role.USER, registeredUser.getRole());  // Compare enum to enum
        verify(userRepository).save(any(User.class));
    }



    //  2. Test Duplicate Email Registration
    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("securePassword");
        request.setRole(User.Role.USER);
        request.setGender(User.Gender.MALE);
        request.setToken("mockToken");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Expect exception
        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerUser(request));

        verify(userRepository, never()).save(any(User.class)); // Ensure save is not called
    }

    //  3. Test Successful Login
    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getEmail(), String.valueOf(testUser.getRole()))).thenReturn("mockToken");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    //  4. Test Login Failure (Invalid Password)
    @Test
    void shouldThrowExceptionForInvalidPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    //  5. Test Admin Access to Get All Users
    @Test
    void shouldAllowAdminToGetAllUsers() {
        when(jwtUtil.extractRole("mockToken")).thenReturn("ADMIN");

        authService.getAllUsers("mockToken");

        verify(userRepository, times(1)).findAll();
    }

    //  6. Test Delete User (Admin)
    @Test
    void shouldDeleteUserIfAdmin() {
        when(jwtUtil.extractRole("mockToken")).thenReturn("ADMIN");
        when(jwtUtil.extractEmail("mockToken")).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));

        boolean result = authService.deleteUserByEmail("mockToken", "user@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).delete(any(User.class));
    }

    //  7. Test Delete User (Non-Admin)
    @Test
    void shouldNotDeleteUserIfNotAdmin() {
        when(jwtUtil.extractRole("mockToken")).thenReturn("USER");
        when(jwtUtil.extractEmail("mockToken")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        boolean result = authService.deleteUserByEmail("mockToken", "another@example.com");

        assertFalse(result);
        verify(userRepository, never()).delete(any(User.class));
    }
}
