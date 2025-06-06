package project.api.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.api.rest.dto.AuthResponse;
import project.api.rest.dto.UserDTO;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.TokenService;
import project.api.rest.service.UserService;


@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(TokenService tokenService, UserService userService, UserMapper userMapper) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and returns an authentication token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody UserDTO userDTO) {

        UserDTO createdUserDTO = userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO)));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDTO.getEmail(),
                userDTO.getPassword(),
                userService.loadUserByUsername(createdUserDTO.getEmail()).getAuthorities());

        String token = tokenService.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponse(createdUserDTO, token), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(Authentication authentication) {
        String token = tokenService.generateToken(authentication);

        UserDTO authUserDTO = userMapper.toDTO(userService.findByEmail(authentication.getName()));

        return new ResponseEntity<>(new AuthResponse(authUserDTO, token), HttpStatus.OK);
    }
}

