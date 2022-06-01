package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
public class UserController {
    UserService userService;

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public String registerUser() {
        return null;
    }

    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login() {
        return null;
    }

    @DeleteMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public String logout() {
        return null;
    }

    @DeleteMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String leave() {
        return null;
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String viewAccount() {
        return null;
    }

    @PutMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String editAccount() {
        return null;
    }

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public String listDevices() {
        return null;
    }

    @GetMapping(value = "/devices/{devicesNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findDevice() {
        return null;
    }

    @PostMapping(value = "/devices/{devicesNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String takeDevice() {
        return null;
    }

}
