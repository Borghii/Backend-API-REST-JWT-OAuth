package project.api.rest.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.api.rest.dto.UserDTO;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Operations related to users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all registered users in the system")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> userDTOList = userService.findAllUsers().stream()
                .map(userMapper::toDTO)
                .toList();

        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user based on their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(userMapper.toDTO(userService.findById(id)), HttpStatus.OK);
    }


    @Operation(summary = "Create a new user", description = "Registers a new user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "User already exist")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUserDTO = userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO)));
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a registered user from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User with id : " + id + " deleted successfully", HttpStatus.OK);
    }

    @Operation(summary = "Update user by ID", description = "Updates user information based on their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "User with email already exist"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserById(
            @PathVariable Integer userId,
            @Valid @RequestBody UserDTO userDTO) {

        UserDTO updatedUserDTO = userMapper.toDTO(userService.updateUser(userId, userMapper.toEntity(userDTO)));

        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }


}
