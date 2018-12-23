package sec.helloweb;

import fi.helsinki.cs.tmc.edutestutils.Points;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Points("S2.04")
public class HelloWebWithDatabaseTest {

    @Autowired
    private HelloMessageRepository messageRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void contentIsRetrievedFromDatabase() throws Exception {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            messages.add(UUID.randomUUID().toString().substring(0, 6));
        }

        for (String message : messages) {
            HelloMessage msg = new HelloMessage();
            msg.setContent(message);
            messageRepository.save(msg);
        }

        for (int i = 0; i < 25; i++) {
            String response = this.mockMvc.perform(get("/"))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            messages.remove(response);
        }

        assertTrue("When there are messages in the database and a user queries the site, \nthe text content of a random message (from the database) should be shown to the user. Right now, not all messages from the database are used.", messages.isEmpty());

    }
}
