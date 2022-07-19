package net.deviceinventory.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dao.DeviceDao;
import net.deviceinventory.dao.UserDao;
import net.deviceinventory.dto.mappers.UserDtoMapper;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class AdminService {
    UserDtoMapper userDtoMapper;
    UserDao userDao;
    DeviceDao deviceDao;


    public User appointAdmin(User user) {
        User newAdmin = userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(0, RoleType.ROLE_ADMIN));
        user.setRole(roles);
        newAdmin.setRole(roles);
        userDao.save(newAdmin);
        return newAdmin;
    }

    public Device addDevice(Device device) {
        if (deviceDao.existsByQRCode(device.getQRCode())) throw new RuntimeException("QR Code already exists");
        return deviceDao.save(device);
    }

    public Device editDevice(Device device) {
        Optional<Device> deviceByQR = deviceDao.findByQRCode(device.getQRCode());
        if (deviceByQR.isPresent())
            if (deviceByQR.get().getId() != device.getId())
                throw new RuntimeException(String.format("QR Code already exists by Device with Id = %d",
                        deviceByQR.get().getId()));
        return deviceDao.save(device);
    }

    public Device deleteDevice(Long id) {
        Device device = deviceDao.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
        deviceDao.deleteById(id);
        return device;
    }
}
