package net.deviceinventory.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dto.request.LoginRequest;
import net.deviceinventory.dto.request.RegisterRequest;
import net.deviceinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {
    UserService userService;


    @PostMapping("/signing")
    public String authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }


}