package n26.statistics.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import n26.statistics.N26StatisticsApplication;
import n26.statistics.dto.TransactionRequest;
import n26.statistics.service.StatisticService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = N26StatisticsApplication.class)
@WebAppConfiguration
public class StatisticControllerTest {

    private static final String GET_PATH = "/statistics";
    private static final String POST_PATH = "/transactions";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private StatisticService statisticService;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get(GET_PATH))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostCreated() throws Exception {
        String json = mockServiceAndGetRequest(true);

        mockMvc.perform(
                post(POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostOldTransaction() throws Exception {
        String json = mockServiceAndGetRequest(false);

        mockMvc.perform(
                post(POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    private String mockServiceAndGetRequest(boolean isResultSuccessful) throws JsonProcessingException {
        final TransactionRequest request = new TransactionRequest(0, 0);
        when(statisticService.saveTransaction(any(TransactionRequest.class))).thenReturn(isResultSuccessful);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(request);
    }

}
