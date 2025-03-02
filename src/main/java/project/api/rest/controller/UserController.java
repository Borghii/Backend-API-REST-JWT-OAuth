package project.api.rest.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> userDTOList = userService.findAllUsers().stream()
                .map(userMapper::toDTO)
                .toList();

        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id){
        return new ResponseEntity<>(userMapper.toDTO(userService.findById(id)), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUserDTO = userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO)));
//        createdUserDTO.setCreatedAt(Instant.now());
//        createdUserDTO.setUpdatedAt(Instant.now());
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id){
        userService.deleteUser(id);
        return new ResponseEntity<>("User with id : "+ id+" deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserById(
            @PathVariable int userId,
            @Valid @RequestBody UserDTO userDTO) {

        UserDTO updatedUserDTO = userMapper.toDTO(userService.updateUser(userId,userMapper.toEntity(userDTO)));

        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }


}
