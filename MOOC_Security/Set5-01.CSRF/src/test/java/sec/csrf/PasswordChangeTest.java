package sec.csrf;

import fi.helsinki.cs.tmc.edutestutils.Points;
import javax.servlet.Filter;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Points("S5.01")
public class PasswordChangeTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private MockHttpSession mockSession;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void cannotChangePasswordWithoutCrsfToken() throws Throwable {
        // login
        mockMvc.perform(post("/login").session(mockSession).param("username", "ted").param("password", "ted").with(
                SecurityMockMvcRequestPostProcessors.csrf())).andReturn();
        // change password (no token), should not change password
        mockMvc.perform(post("/password").session(mockSession).param("password", "newted")).andReturn();

        // logout
        mockMvc.perform(post("/logout").session(mockSession).with(SecurityMockMvcRequestPostProcessors.csrf())).andReturn();
        mockSession.clearAttributes();

        // login should fail due to wrong password
        MvcResult res = mockMvc.perform(post("/login").session(mockSession).param("username", "ted").param("password", "newted").with(
                SecurityMockMvcRequestPostProcessors.csrf())).andReturn();
        assertTrue(res.getResponse().getRedirectedUrl().contains("login"));

        // login should succeed with old password
        mockMvc.perform(post("/login").session(mockSession).param("username", "ted").param("password", "ted").with(
                SecurityMockMvcRequestPostProcessors.csrf())).andReturn();
    }

    @Test
    public void loginWithCsrfTokenAllowed() throws Exception {
        MvcResult res = mockMvc.perform(post("/login").session(mockSession).param("username", "ted").param("password", "ted").with(
                SecurityMockMvcRequestPostProcessors.csrf())).andReturn();
    }

    @Test
    public void loginWithoutCsrfTokenNotAllowed() throws Throwable {
        mockMvc.perform(post("/login").session(mockSession).param("username", "ted").param("password", "ted")).andExpect(status().isForbidden());
    }
}
