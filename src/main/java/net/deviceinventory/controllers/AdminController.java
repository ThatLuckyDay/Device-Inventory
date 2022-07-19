package net.deviceinventory.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.model.Device;
import net.deviceinventory.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
public class AdminController {
    AdminService adminService;

    @PostMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device addDevice(@RequestBody Device device) {
        return adminService.addDevice(device);
    }

    @PutMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device editDevice(@RequestBody Device device) {
        return adminService.editDevice(device);
    }

    @DeleteMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device deleteDevice(@PathVariable("id") Long id) {
        return adminService.deleteDevice(id);
    }

}
