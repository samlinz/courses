package sec.millionaire;

import fi.helsinki.cs.tmc.edutestutils.Points;
import java.util.UUID;
import javax.servlet.Filter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sec.millionaire.domain.UserDetails;
import sec.millionaire.repository.UserDetailsRepository;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@Points("S5.02")
public class MillionaireTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockHttpSession mockSession;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void postingData() throws Throwable {
        // login
        String username = UUID.randomUUID().toString().substring(0, 6);
        String email = username + "@email.net";

        // should not be able to add a user without passing the game
        mockMvc.perform(post("/details").session(mockSession).param("name", username).param("email", email)).andReturn();

        UserDetails details = userDetailsRepository.findByName(username);
        assertNull("One should not be able to enter user user details without actually finishing the game..", details);

    }
}
