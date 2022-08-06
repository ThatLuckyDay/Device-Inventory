package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dto.request.DeviceRequest;
import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.User;
import net.deviceinventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
@Validated
public class UserController {
    UserService userService;

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public void signOut(HttpServletRequest request) {
        userService.signOut(request);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signIn(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.signIn(oAuth2User);
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse viewAccount(@AuthenticationPrincipal OAuth2User oAuth2User) {
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

    @PostMapping(value = "/owners", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse takeDevice(@AuthenticationPrincipal OAuth2User oAuth2User,
                                   @Valid @RequestBody DeviceRequest device) {
        return userService.takeDevice(device, oAuth2User);
    }

    @PostMapping(value = "/admins", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signInLikeAdmin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.addAdminAuthority(oAuth2User);
    }

}
