package net.deviceinventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.deviceinventory.model.Device;
import net.deviceinventory.service.DebugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
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
class AdminControllerTest {

    @Autowired
    private DebugService debugService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private HttpSession mockHttpSession;

    private Cookie token;


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
                                    a.put("given_name", "test");
                                    a.put("family_name", "test");
                                    a.put("email", "test@test.test");
                                })))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
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
                .andExpectAll(
                        status().isOk(),
                        cookie().doesNotExist("XSRF-TOKEN")
                )
                .andReturn();

        Device responseDevice = mapper.readValue(result.getResponse().getContentAsString(), Device.class);

        assertAll(
                () -> assertEquals(device.getName(), responseDevice.getName()),
                () -> assertEquals(device.getQRCode(), responseDevice.getQRCode()),
                () -> assertNull(device.getUser())
        );
    }


}