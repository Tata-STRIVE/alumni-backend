package com.striveconnect.controller;

import com.striveconnect.dto.AuthRequest;
import com.striveconnect.dto.AuthResponse;
import com.striveconnect.dto.OtpVerificationRequest;
import com.striveconnect.dto.RegistrationRequestDto;
import com.striveconnect.dto.UserDto;
import com.striveconnect.entity.User;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.service.JwtService;
import com.striveconnect.service.OtpService;
import com.striveconnect.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserService userService;

    public AuthController(UserRepository userRepository, JwtService jwtService, OtpService otpService, UserService userService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationRequestDto registrationRequest) {
        // ... (registration code is unchanged)
        logger.info("Received registration request for mobile: {}", registrationRequest.getMobileNumber());
        UserDto newUser = userService.registerUser(registrationRequest);
        logger.info("User registered successfully with ID: {}. Awaiting admin approval.", newUser.getUserId());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    
    @GetMapping("/test")
    public String testUser() {
        // ... (registration code is unchanged)
      
        return  "AuthController working";
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        String mobileNumber = authRequest.getMobileNumber().trim();
        String tenantId = authRequest.getTenantId().trim();

        logger.info("Received login request for mobile: {}", mobileNumber);

        // --- NEW: Robust check for duplicate accounts ---
        List<User> users = userRepository.findAllByMobileNumberAndTenantId(mobileNumber, tenantId);

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for this tenant");
        }
        
        if (users.size() > 1) {
            logger.error("CRITICAL: Multiple active accounts found for mobile number {} in tenant {}. Login denied.", mobileNumber, tenantId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple accounts are associated with this mobile number. Please contact support.");
        }
        // --- End of new check ---

        String otpKey = mobileNumber + "@" + tenantId;
        String otp = otpService.generateOtp(otpKey);

        logger.info("Generated OTP for {}: {}", otpKey, otp);

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndGenerateToken(@RequestBody OtpVerificationRequest verificationRequest) {
        // ... (verify-otp code is unchanged)
        String mobileNumber = verificationRequest.getMobileNumber().trim();
        String tenantId = verificationRequest.getTenantId().trim();
        String otp = verificationRequest.getOtp().trim();

        logger.info("Received OTP verification request for mobile: {}", mobileNumber);
        
        String otpKey = mobileNumber + "@" + tenantId;
        
        if (!otpService.validateOtp(otpKey, otp)) {
            logger.warn("Invalid OTP received for key: {}", otpKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP");
        }

        User user = userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() != User.Status.ACTIVE) {
             logger.warn("Login attempt for non-active user: {}", user.getMobileNumber());
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is not active.");
        }

        final String jwt = jwtService.generateToken(user);
        logger.info("OTP verification successful. JWT generated for user: {}", user.getUserId());
        logger.info("OTP verification successful. JWT is : {}", jwt);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

