package net.deviceinventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.exceptions.ErrorCode;
import net.deviceinventory.exceptions.ServerException;
import net.deviceinventory.exceptions.ServerExceptionResponse;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import net.deviceinventory.service.DebugService;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
class UserControllerTest {

    @Autowired
    private DebugService debugService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private Device deviceFromDB;


    @BeforeEach
    void initData() throws Exception {
        debugService.clearDatabase();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        MvcResult result = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "admin");
                                    a.put("family_name", "admin");
                                    a.put("email", "admin@admin.admin");
                                })))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        HttpSession mockHttpSession = Objects.requireNonNull(result.getRequest().getSession());

        Cookie token = result.getResponse().getCookie("XSRF-TOKEN");

        Device device = new Device(
                0,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult deviceResult = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        deviceFromDB = mapper.readValue(deviceResult.getResponse().getContentAsString(), Device.class);
    }

    @Test
    void signInTest() throws Exception {
        MvcResult result = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        User user = mapper.readValue(result.getResponse().getContentAsString(), User.class);

        assertNotNull(user);

        Role[] userRole = user.getRole().toArray(Role[]::new);

        assertAll(
                () -> assertEquals("test@test.test", user.getEmail()),
                () -> assertEquals("test", user.getFirstName()),
                () -> assertEquals("test", user.getLastName()),
                () -> assertEquals(userRole.length, 1),
                () -> assertEquals(userRole[0].getName(), RoleType.ROLE_USER)
        );
    }

    @Test
    void signInFailEmptyUserTest() throws Exception {
        mvc
                .perform(get("/api/users")
                        .with(oauth2Login()))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void signInFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signOutTest() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");
        mvc
                .perform(post("/api/users")
                        .with(r -> {
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();
    }

    @Test
    void signOutFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(post("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewAccountTest() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");
        MvcResult result = mvc
                .perform(get("/api/accounts")
                        .with(r -> {
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        UserResponse user = mapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertNotNull(user);

        Role[] userRole = user.getRoles().toArray(Role[]::new);

        assertAll(
                () -> assertEquals("test@test.test", user.getEmail()),
                () -> assertEquals("test", user.getFirstName()),
                () -> assertEquals("test", user.getLastName()),
                () -> assertEquals(userRole.length, 1),
                () -> assertEquals(userRole[0].getName(), RoleType.ROLE_USER)
        );
    }

    @Test
    void viewAccountFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(get("/api/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listDevices() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");
        MvcResult result = mvc
                .perform(get("/api/devices")
                        .with(r -> {
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        Device[] devices = mapper.readValue(result.getResponse().getContentAsString(), Device[].class);

        assertNotNull(devices);

        assertAll(
                () -> assertEquals(devices.length, 1),
                () -> assertEquals(deviceFromDB.getQRCode(), devices[0].getQRCode()),
                () -> assertEquals(deviceFromDB.getName(), devices[0].getName()),
                () -> assertNull(devices[0].getUser())
        );
    }

    @Test
    void listDevicesFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(get("/api/devices"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findDevice() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");
        MvcResult result = mvc
                .perform(get("/api/devices/" + deviceFromDB.getId())
                        .with(r -> {
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        Device device = mapper.readValue(result.getResponse().getContentAsString(), Device.class);

        assertNotNull(device);

        assertAll(
                () -> assertEquals(deviceFromDB.getQRCode(), device.getQRCode()),
                () -> assertEquals(deviceFromDB.getName(), device.getName()),
                () -> assertNull(device.getUser())
        );
    }

    @Test
    void findDeviceFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(get("/api/devices/" + deviceFromDB.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findDeviceFailNoSuchDevice() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");
        MvcResult result = mvc
                .perform(get("/api/devices/" + deviceFromDB.getId() + 10)
                        .with(r -> {
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse exceptionResponse = mapper.readValue(
                result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.DEVICE_NOT_FOUND, exceptionResponse.getErrors().get(0));
    }

    @Test
    void takeDevice() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");

        byte[] requestBody = mapper.writeValueAsBytes(deviceFromDB);

        MvcResult result = mvc
                .perform(post("/api/owners")
                        .with(r -> {
                            oauth2Login();
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        UserResponse userResponse = mapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertNotNull(userResponse);

        Role[] userRole = userResponse.getRoles().toArray(Role[]::new);

        assertAll(
                () -> assertEquals(deviceFromDB.getQRCode(), userResponse.getDevices().get(0).getQRCode()),
                () -> assertEquals(deviceFromDB.getName(), userResponse.getDevices().get(0).getName()),
                () -> assertEquals("test@test.test", userResponse.getEmail()),
                () -> assertEquals("test", userResponse.getFirstName()),
                () -> assertEquals("test", userResponse.getLastName()),
                () -> assertEquals(userRole.length, 1),
                () -> assertEquals(userRole[0].getName(), RoleType.ROLE_USER),
                () -> assertEquals(userResponse.getId(), userResponse.getDevices().get(0).getUser().getId())
        );
    }

    @Test
    void takeDeviceFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(post("/api/owners"))
                .andExpect(status().isForbidden());
    }

    @Test
    void takeDeviceFailNoSuchDevice() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");

        Device unknownDevice = new Device(
                0,
                "testDevice2",
                "testQR2",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(unknownDevice);

        MvcResult result = mvc
                .perform(post("/api/owners")
                        .with(r -> {
                            oauth2Login();
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse exceptionResponse = mapper.readValue(
                result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.QR_CODE_NOT_EXIST, exceptionResponse.getErrors().get(0));
    }

    @Test
    void takeDeviceFailReservedDevice() throws Exception {
        MvcResult mvcResult = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andReturn();
        Cookie token = mvcResult.getResponse().getCookie("XSRF-TOKEN");

        byte[] requestBody = mapper.writeValueAsBytes(deviceFromDB);

        mvc
                .perform(post("/api/owners")
                        .with(r -> {
                            oauth2Login();
                            r.setSession(Objects.requireNonNull(mvcResult.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk());

        MvcResult mvcResultSecond = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test2");
                                    a.put("family_name", "test2");
                                    a.put("email", "test@test.test2");
                                })))
                .andReturn();

        Cookie tokenSecond = mvcResultSecond.getResponse().getCookie("XSRF-TOKEN");

        MvcResult result = mvc
                .perform(post("/api/owners")
                        .with(r -> {
                            oauth2Login();
                            r.setSession(Objects.requireNonNull(mvcResultSecond.getRequest().getSession()));
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(tokenSecond).getValue());
                            r.setCookies(tokenSecond);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse exceptionResponse = mapper.readValue(
                result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.DEVICE_RESERVED, exceptionResponse.getErrors().get(0));
    }


}