package com.yourproject.resource.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import org.bson.BsonDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.yourproject.resource.util.TestUtil.getMatcherForHasItems;
import static com.yourproject.resource.util.TestUtil.getMatcherForOneItem;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auth for endpoints is disabled.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SampleController.class, secure=false)
public class SampleControllerTest {

    private static final String CURRENCY_ID = "5b0412f014d35a48c9148e40";
    private static final String CURRENCY_TITLE = "UAH";
    private static final String CURRENCY_SYMBOL = "₴";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SampleService sampleService;

    @Mock
    private MongoBulkWriteException mongoBulkWriteException;

    @Before
    public void setUp() {
        when(this.mongoBulkWriteException.getWriteErrors()).thenReturn(Collections.singletonList(new BulkWriteError(11000, "E11000 duplicate key error collection: test.samples index: title dup key: { : \"SOME TITLE\" }", new BsonDocument(), 0)));
    }

    @Test
    public void getSamples() {
    }

    @Test
    public void createSamples() throws Exception {
        when(this.sampleService.create(anyList(), anyString())).thenReturn(Collections.emptyList());


        this.mockMvc
                .perform(post("/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(OBJECT_MAPPER.writeValueAsString(Collections.emptyList())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasItems("$.[*].title", "UAH"))
                .andExpect(getMatcherForHasItems("$.[*].symbol", "₴"));
    }

    @Test
    public void createSamplesDuplicatedKey() throws Exception {
        when(this.sampleService.create(anyList(), anyString())).thenThrow(this.mongoBulkWriteException);

        this.mockMvc
                .perform(post("/samples").contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(Collections.emptyList())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
                .andExpect(getMatcherForOneItem("$.message", "[E11000 duplicate key error collection: test.samples index: title dup key: { : \"SOME TITLE\" }]"))
                .andExpect(getMatcherForOneItem("$.contextPath", "/samples"));
    }

    @Test
    public void createSamplesFailed() throws Exception {
        this.mockMvc
                .perform(post("/samples").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
                .andExpect(getMatcherForOneItem("$.message", "Cannot deserialize instance of `java.util.ArrayList` out of START_OBJECT token\n at [Source: (PushbackInputStream); line: 1, column: 1]"))
                .andExpect(getMatcherForOneItem("$.contextPath", "/samples"));
    }
}