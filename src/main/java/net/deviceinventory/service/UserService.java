package net.deviceinventory.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dao.DeviceDao;
import net.deviceinventory.dao.UserDao;
import net.deviceinventory.dto.mappers.UserDtoMapper;
import net.deviceinventory.dto.request.DeviceRequest;
import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.exceptions.ErrorCode;
import net.deviceinventory.exceptions.ServerException;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import net.deviceinventory.security.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    CustomOAuth2UserService securityService;


    public User signIn(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) {
            return userDao.findByEmail(user.getEmail()).orElseThrow(() -> new ServerException(ErrorCode.SERVER_ERROR));
        }
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

    public UserResponse viewAccount(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        User userData = userDao
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new ServerException(ErrorCode.USER_NOT_FOUND, user.getEmail()));
        List<Device> devices = deviceDao.findByUser(userData);
        return userDtoMapper.toUserDto(userData, devices);
    }

    public UserResponse addAdminAuthority(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) {
            User presentUser = userDao.findByEmail(user.getEmail())
                    .orElseThrow(() -> new ServerException(ErrorCode.SERVER_ERROR));
            boolean isAdmin = presentUser
                    .getRole()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toList())
                    .contains(RoleType.ROLE_ADMIN);
            if (isAdmin) throw new ServerException(ErrorCode.ROLE_ALREADY_ADDED, user.getEmail());
            else presentUser.getRole().add(new Role(0, RoleType.ROLE_ADMIN));
            userDao.save(presentUser);
            List<Device> devices = deviceDao.findByUser(presentUser);

            Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            Authentication on = new OAuth2AuthenticationToken(
                    new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "sub"),
                    authorities,
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

            SecurityContextHolder.getContext().setAuthentication(on);

            return userDtoMapper.toUserDto(presentUser, devices);
        }
        throw new ServerException(ErrorCode.USER_NOT_FOUND, user.getEmail());
    }

    public UserResponse removeAdminAuthority(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) {
            User presentUser = userDao.findByEmail(user.getEmail())
                    .orElseThrow(() -> new ServerException(ErrorCode.SERVER_ERROR));
            presentUser
                    .getRole()
                    .removeIf(role -> role.getName() == RoleType.ROLE_ADMIN);
            userDao.save(presentUser);
            List<Device> devices = deviceDao.findByUser(presentUser);

            Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
            authorities.removeIf(auth -> auth.getAuthority().equals(RoleType.ROLE_ADMIN.name()));

            Authentication on = new OAuth2AuthenticationToken(
                    new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "sub"),
                    authorities,
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

            SecurityContextHolder.getContext().setAuthentication(on);

            return userDtoMapper.toUserDto(presentUser, devices);
        }
        throw new ServerException(ErrorCode.USER_NOT_FOUND, user.getEmail());
    }

    public UserResponse assignDevice(DeviceRequest deviceDto, OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        User userData = userDao
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new ServerException(ErrorCode.USER_NOT_FOUND, user.getEmail()));
        Device device = userDtoMapper.fromDeviceDto(deviceDto);
        Optional<Device> deviceData = deviceDao.findByQRCode(device.getQRCode());
        if (deviceData.isEmpty()) {
            throw new ServerException(ErrorCode.QR_CODE_NOT_EXIST, device.getQRCode());
        }
        if (deviceData.get().getUser() != null) {
            if (deviceData.get().getUser().equals(userData)) deviceData.get().setUser(null);
            else throw new ServerException(ErrorCode.DEVICE_RESERVED, deviceData.get().getUser().getEmail());
        }
        else deviceData.get().setUser(userData);
        deviceDao.save(deviceData.get());
        userData = userDao
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new ServerException(ErrorCode.USER_NOT_FOUND, user.getEmail()));
        return userDtoMapper.toUserDto(userData, deviceDao.findByUser(userData));
    }
}
