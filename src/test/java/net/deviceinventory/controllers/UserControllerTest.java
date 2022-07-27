package net.deviceinventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.deviceinventory.dto.response.UserResponse;
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


    @BeforeEach
    void initData() {
        debugService.clearDatabase();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
    void signInEmptyUserTest() throws Exception {
        mvc
                .perform(get("/api/users")
                        .with(oauth2Login()))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void signInPermissionDeniedTest() throws Exception {
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
    void signOutPermissionDeniedTest() throws Exception {
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


}