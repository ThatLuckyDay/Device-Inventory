package net.deviceinventory.service;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dto.mappers.AuthDtoMapper;
import net.deviceinventory.dto.request.LoginRequest;
import net.deviceinventory.dto.request.RegisterRequest;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class UserService {
    AuthDtoMapper mapper;
    Gson gson;
//    UserDao userDao;
//    RoleDao roleDao;

    public String login(LoginRequest loginRequest, HttpServletResponse response) {
        return "login";
    }

    public String register(RegisterRequest registerRequest, HttpServletResponse response) {
//        User user = mapper.fromUserDto(registerRequest);
//        if (userDao.existsByUsername(user.getUsername())) {
//            return String.format("Error: Login %s is already taken!", registerRequest.getUsername());
//        }
//        if (userDao.existsByEmail(user.getEmail())) {
//            return String.format("Error: Email %s is already in use!", registerRequest.getEmail());
//        }
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setUsername(user.getUsername());
//        loginRequest.setPassword(user.getPassword());
//        user.setPassword(encoder.encode(registerRequest.getPassword()));
//        user.setRole(parseRoles(registerRequest.getRole()));
//        user.setActive(true);
//        userDao.save(user);
//        login(loginRequest, response);
        return "User registered successfully!";
    }

    public Set<Role> parseRoles(Set<String> strRoles) {
        Set<Role> roleSet = new HashSet<>();
//
//        if (strRoles == null) {
//            Role userRole = roleDao.findByRoleName(RoleType.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roleSet.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                if ("admin".equals(role)) {
//                    Role adminRole = roleDao.findByRoleName(RoleType.ROLE_ADMIN)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    roleSet.add(adminRole);
//                } else {
//                    Role userRole = roleDao.findByRoleName(RoleType.ROLE_USER)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    roleSet.add(userRole);
//                }
//            });
//        }
        return roleSet;
    }


}
