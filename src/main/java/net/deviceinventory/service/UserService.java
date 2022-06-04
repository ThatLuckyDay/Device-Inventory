package net.deviceinventory.service;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dao.RoleDao;
import net.deviceinventory.dao.UserDao;
import net.deviceinventory.dto.mappers.AuthDtoMapper;
import net.deviceinventory.dto.request.LoginRequest;
import net.deviceinventory.dto.request.RegisterRequest;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import net.deviceinventory.security.JwtUtils;
import net.deviceinventory.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class UserService {
    AuthDtoMapper mapper;
    Gson gson;
    UserDao userDao;
    AuthenticationManager authenticationManager;
    RoleDao roleDao;
    PasswordEncoder encoder;
    JwtUtils jwtUtils;

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // To Header: Authorization
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return gson.toJson(mapper.toUserDto(userDetails));
    }

    public String register(RegisterRequest registerRequest) {
        if (userDao.existsByUsername(registerRequest.getUsername())) {
            return String.format("Error: Login %s is already taken!", registerRequest.getUsername());
        }
        String mail = registerRequest.getEmail();
        if (userDao.existsByEmail(mail)) {
            return String.format("Error: Email %s is already in use!", registerRequest.getEmail());
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleDao.findByRoleName(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleDao.findByRoleName(RoleType.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleDao.findByRoleName(RoleType.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        /*----------- Test data -------------*/
        /* Move to request */
        user.setRole(roles);
        user.setFirstName("user");
        user.setLastName("user");
        /* -----------------------------------*/
        user.setActive(true);
        userDao.save(user);
        return "User registered successfully!";
    }


}
