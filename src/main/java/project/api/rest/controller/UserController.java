package project.api.rest.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.api.rest.entity.User;
import project.api.rest.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) { // @RequestBody en lugar de @PathVariable

        try {
            userService.createUser(user); // Invoca el servicio para crear el usuario
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) { // Maneja la excepción específica
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // Maneja cualquier otra excepción
            return new ResponseEntity<>("An error occurred while creating the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}
