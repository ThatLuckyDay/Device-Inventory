package net.deviceinventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.deviceinventory.exceptions.ErrorCode;
import net.deviceinventory.exceptions.ServerExceptionResponse;
import net.deviceinventory.model.Device;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {DatabaseInitializer.Initializer.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class DeviceControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private HttpSession mockHttpSession;

    private Cookie token;


    @BeforeAll
    static void startContainer() {
        DatabaseInitializer.container.start();
    }

    @BeforeEach
    void initData() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        MvcResult result = mvc
                .perform(get("/api/users")
                        .with(oauth2Login()
                                .attributes(a -> {
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ))
                .andExpect(status().isOk())
                .andReturn();

        mockHttpSession = Objects.requireNonNull(result.getRequest().getSession());

        token = result.getResponse().getCookie("XSRF-TOKEN");
    }

    @Test
    void addDeviceTest() throws Exception {
        Device device = new Device(
                0,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult result = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk())
                .andReturn();

        Device responseDevice = mapper.readValue(result.getResponse().getContentAsString(), Device.class);

        assertAll(
                () -> assertEquals(device.getName(), responseDevice.getName()),
                () -> assertEquals(device.getQRCode(), responseDevice.getQRCode()),
                () -> assertNull(device.getUser())
        );
    }

    @Test
    void addDeviceFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(post("/api/devices"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addDeviceFailEmptyDeviceTest() throws Exception {
        MvcResult result = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.UNDEFINED_ERROR, responseDevice.getErrors().get(0));
    }

    @Test
    void addDeviceFailEmptyFieldTest() throws Exception {
        Device device = new Device(
                0,
                null,
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult result = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.EMPTY_FIELD, responseDevice.getErrors().get(0));
    }

    @Test
    void addDeviceFailQRExistTest() throws Exception {
        Device device = new Device(
                0,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk());

        MvcResult result = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.QR_CODE_EXIST, responseDevice.getErrors().get(0));
    }

    @Test
    void editDeviceTest() throws Exception {
        Device device = new Device(
                1,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult deviceSavedResponse = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk())
                .andReturn();

        Device deviceSaved = mapper.readValue(deviceSavedResponse.getResponse().getContentAsString(), Device.class);

        Device deviceEdited = new Device(
                deviceSaved.getId(),
                "testDevice2",
                "testQR2",
                null
        );

        byte[] requestBodyEdited = mapper.writeValueAsBytes(deviceEdited);

        MvcResult resultResponse = mvc
                .perform(put("/api/devices/" + deviceSaved.getId())
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBodyEdited);
                            return r;
                        }))
                .andExpectAll(status().isOk())
                .andReturn();

        Device deviceResult = mapper.readValue(resultResponse.getResponse().getContentAsString(), Device.class);

        assertAll(
                () -> assertEquals(deviceEdited.getName(), deviceResult.getName()),
                () -> assertEquals(deviceEdited.getQRCode(), deviceResult.getQRCode()),
                () -> assertNull(deviceEdited.getUser())
        );
    }

    @Test
    void editDeviceFailPermissionDeniedTest() throws Exception {
        mvc
                .perform(put("/api/devices"))
                .andExpect(status().isForbidden());
    }

    @Test
    void editDeviceFailEmptyDeviceTest() throws Exception {
        MvcResult result = mvc
                .perform(put("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.UNDEFINED_ERROR, responseDevice.getErrors().get(0));
    }

    @Test
    void editDeviceFailEmptyFieldTest() throws Exception {
        Device device = new Device(
                0,
                null,
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult result = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(result.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.EMPTY_FIELD, responseDevice.getErrors().get(0));
    }

    @Test
    void editDeviceFailQRAlreadyExistTest() throws Exception {
        Device device = new Device(
                1,
                "testDevice1",
                "testQR1",
                null
        );

        Device device2 = new Device(
                2,
                "testDevice2",
                "testQR2",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult deviceSavedResponse = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk())
                .andReturn();

        byte[] requestBody2 = mapper.writeValueAsBytes(device2);

        mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody2);
                            return r;
                        }))
                .andExpect(status().isOk());

        Device deviceSaved = mapper.readValue(deviceSavedResponse.getResponse().getContentAsString(), Device.class);

        Device deviceEdited = new Device(
                deviceSaved.getId(),
                "testDevice",
                "testQR2",
                null
        );

        byte[] requestBodyEdited = mapper.writeValueAsBytes(deviceEdited);

        MvcResult resultResponse = mvc
                .perform(put("/api/devices/" + deviceSaved.getId())
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBodyEdited);
                            return r;
                        }))
                .andExpectAll(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(resultResponse.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.QR_CODE_EXIST, responseDevice.getErrors().get(0));
    }

    @Test
    void deleteDeviceTest() throws Exception {
        Device device = new Device(
                1,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        MvcResult deviceSavedResponse = mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk())
                .andReturn();

        Device deviceSaved = mapper.readValue(deviceSavedResponse.getResponse().getContentAsString(), Device.class);

        MvcResult resultResponse = mvc
                .perform(delete("/api/devices/" + deviceSaved.getId())
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpect(status().isOk())
                .andReturn();

        Device deviceResult = mapper.readValue(resultResponse.getResponse().getContentAsString(), Device.class);

        assertAll(
                () -> assertEquals(deviceSaved.getName(), deviceResult.getName()),
                () -> assertEquals(deviceSaved.getQRCode(), deviceResult.getQRCode()),
                () -> assertNull(deviceSaved.getUser())
        );
    }

    @Test
    void deleteDeviceFailNotFoundTest() throws Exception {
        Device device = new Device(
                1,
                "testDevice1",
                "testQR1",
                null
        );

        byte[] requestBody = mapper.writeValueAsBytes(device);

        mvc
                .perform(post("/api/devices")
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            r.setContentType(MediaType.APPLICATION_JSON.toString());
                            r.setContent(requestBody);
                            return r;
                        }))
                .andExpect(status().isOk());

        MvcResult resultResponse = mvc
                .perform(delete("/api/devices/" + device.getId())
                        .with(r -> {
                            r.setSession(mockHttpSession);
                            r.addHeader("X-XSRF-TOKEN", Objects.requireNonNull(token).getValue());
                            r.setCookies(token);
                            return r;
                        }))
                .andExpectAll(status().isBadRequest())
                .andReturn();

        ServerExceptionResponse responseDevice = mapper.readValue(resultResponse.getResponse().getContentAsString(),
                ServerExceptionResponse.class);

        assertEquals(ErrorCode.DEVICE_NOT_FOUND, responseDevice.getErrors().get(0));
    }


}