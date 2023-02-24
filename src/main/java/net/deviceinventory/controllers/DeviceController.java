package net.deviceinventory.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dto.request.NewDeviceRequest;
import net.deviceinventory.model.Device;
import net.deviceinventory.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api")
@Validated
@Api(value = "Device APIs")
public class DeviceController {
    DeviceService deviceService;

    @ApiOperation(value = "Add new device to database. Admins only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device addDevice(@Valid @RequestBody NewDeviceRequest device) {
        return deviceService.addDevice(device);
    }

    @ApiOperation(value = "Edit device - change QR Code and (or) name. Admins only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device editDevice(@Valid @RequestBody NewDeviceRequest device) {
        return deviceService.editDevice(device);
    }

    @ApiOperation(value = "Delete device from database. Admins only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device deleteDevice(@PathVariable Long id) {
        return deviceService.deleteDevice(id);
    }

    @ApiOperation(value = "Find device by QR Code")
    @GetMapping(value = "/devices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Device findDevice(@PathVariable Long id) {
        return deviceService.getDevice(id);
    }

}
