package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.auth.LoginRequest;
import rw.gov.erp.dto.auth.LoginResponse;
import rw.gov.erp.dto.RegisterRequest;
import rw.gov.erp.entity.User;
import rw.gov.erp.model.Employee;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.UserRepository;
import rw.gov.erp.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new LoginResponse(token, user.getEmail(), user.getRole());
    }

    @Transactional
    public void register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // Save user
        userRepository.save(user);

        // Send welcome email
        emailService.sendEmail(
            request.getEmail(),
            "Welcome to ERP System",
            "Dear " + request.getUsername() + ",\n\n" +
            "Welcome to our ERP System! Your account has been successfully created.\n" +
            "You can now login with your username and password.\n\n" +
            "Best regards,\n" +
            "ERP System Team"
        );
    }
}
