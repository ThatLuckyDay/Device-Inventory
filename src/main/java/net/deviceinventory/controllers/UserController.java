package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.User;
import net.deviceinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
public class UserController {
    UserService userService;


    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return message;
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public void signOut(HttpServletRequest request) {
        userService.signOut(request);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signIn(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.signIn(oAuth2User);
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public User viewAccount(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletRequest request) {
        return userService.viewAccount(oAuth2User);
    }

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Device> listDevices() {
        return userService.getDevices();
    }

    @GetMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device findDevice(@PathVariable Long id) {
        return userService.getDevice(id);
    }

    @PostMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String takeDevice() {
        return null;
    }

}
