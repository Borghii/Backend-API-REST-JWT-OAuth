package project.api.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.api.rest.dto.AuthResponse;
import project.api.rest.dto.UserDTO;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.TokenService;
import project.api.rest.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/auth")
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

//    @PostMapping("/sign-up")
//    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
//        UserDTO createdUserDTO = userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO)));
//        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
//    }

//    @PostMapping("/token")
//    public String token(Authentication authentication) {
//        return tokenService.generateToken(authentication);
//    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody UserDTO userDTO) {

        UserDTO createdUserDTO = userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO)));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDTO.getEmail(),
                userDTO.getPassword(),
                userService.loadUserByUsername(createdUserDTO.getEmail()).getAuthorities());

        String token = tokenService.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponse(createdUserDTO,token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(Authentication authentication) {
        String token = tokenService.generateToken(authentication);

        UserDTO authUserDTO = userMapper.toDTO(userService.findByEmail(authentication.getName()));

        return new ResponseEntity<>(new AuthResponse(authUserDTO,token),HttpStatus.OK);
    }

}
