package net.deviceinventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.deviceinventory.model.User;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        assertAll(
                () -> assertEquals("test@test.test", user.getEmail()),
                () -> assertEquals("test", user.getFirstName()),
                () -> assertEquals("test", user.getLastName())
        );
    }


}