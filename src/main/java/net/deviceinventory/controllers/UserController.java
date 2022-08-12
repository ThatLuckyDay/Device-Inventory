package net.deviceinventory.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
@Validated
@Api(value = "APIs common to any users")
public class UserController {
    UserService userService;

    @ApiOperation(value = "End user session")
    @PostMapping(value = "/users")
    public void signOut(HttpServletRequest request) {
        userService.signOut(request);
    }

    @ApiOperation(value = "Start user session")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User signIn(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.signIn(oAuth2User);
    }

    @ApiOperation(value = "View user account")
    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse viewAccount(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return userService.viewAccount(oAuth2User);
    }

    @ApiOperation(value = "Show all devices")
    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Device> listDevices() {
        return userService.getDevices();
    }

    @ApiOperation(value = "Get administrator rights")
    @PutMapping(value = "/admins/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse takeRoleAdmin(@AuthenticationPrincipal OAuth2User oAuth2User,
                                      @PathVariable(required = false) long id) {
        return userService.addAdminAuthority(oAuth2User);
    }

    @ApiOperation(value = "Delete administrator rights")
    @DeleteMapping(value = "/admins/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse delRoleAdmin(@AuthenticationPrincipal OAuth2User oAuth2User,
                             @PathVariable(required = false) long id) {
        return userService.removeAdminAuthority(oAuth2User);
    }

    @ApiOperation(value = "Write device to user")
    @PostMapping(value = "/owners", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse takeDevice(@AuthenticationPrincipal OAuth2User oAuth2User,
                                   @Valid @RequestBody DeviceRequest device) {
        return userService.takeDevice(device, oAuth2User);
    }

}
