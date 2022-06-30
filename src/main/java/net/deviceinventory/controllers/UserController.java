package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.model.User;
import net.deviceinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
public class UserController {
    UserService userService;


    @GetMapping("/user")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return null;
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/error")
    @ResponseBody
    public String error(HttpServletRequest request) {
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return message;
    }

    @PostMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signIn(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.signIn(oAuth2User);
    }

    @DeleteMapping(value = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signOut(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.signOut(oAuth2User);
    }

    @DeleteMapping(value = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public User leave(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.leave(oAuth2User);
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
