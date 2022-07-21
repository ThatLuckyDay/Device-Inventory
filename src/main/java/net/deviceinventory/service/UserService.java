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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class UserService {
    UserDtoMapper userDtoMapper;
    UserDao userDao;
    DeviceDao deviceDao;


    public User signIn(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) return userDao.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("500"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(0, RoleType.ROLE_USER));
        user.setRole(roles);
        user.setActive(true);
        userDao.save(user);
        return user;
    }

    public void signOut(HttpServletRequest request) {
        request.getSession(false).invalidate();
    }

    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<>();
        deviceDao.findAll().forEach(devices::add);
        return devices;
    }

    public User viewAccount(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        return userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
    }

    public Device getDevice(Long id) {
        return deviceDao.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
    }

    public User takeDevice(Device device, OAuth2User oAuth2User) {
        Optional<Device> deviceData = deviceDao.findByQRCode(device.getQRCode());
        if (deviceData.isEmpty()) throw  new RuntimeException("Device not found");
        if (deviceData.get().getUser() != null)
            throw  new RuntimeException(String.format("The device is being used by user %s ",
                    deviceData.get().getUser().getEmail()));
        User user = userDtoMapper.fromUserDto(oAuth2User);
        User userData = userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
        deviceData.get().setUser(userData);
        deviceDao.save(deviceData.get());
        user = userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
        return userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
    }
}
