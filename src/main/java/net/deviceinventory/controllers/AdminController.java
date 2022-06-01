package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
public class AdminController {
    AdminService adminService;

    @PostMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addDevice() {
        return null;
    }

    @DeleteMapping(value = "/devices/{devicesNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteDevice(@PathVariable("devicesNumber") String devicesNumber) {
        return null;
    }

}
