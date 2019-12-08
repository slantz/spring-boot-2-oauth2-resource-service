package com.yourproject.resource.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.yourproject.resource.DELETEME__dummyObjectsToDb;
import com.yourproject.resource.currency.CurrencyServiceImpl;
import com.yourproject.resource.currency.Currency;
import org.bson.BsonDocument;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.yourproject.resource.util.TestUtil.getMatcherForHasItems;
import static com.yourproject.resource.util.TestUtil.getMatcherForHasSize;
import static com.yourproject.resource.util.TestUtil.getMatcherForOneItem;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auth for endpoints is disabled.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SampleController.class, secure = false)
public class SampleControllerTest {

    private static final Currency USD = new Currency("USD", "$");
    private static final Currency EUR = new Currency("EUR", "€");

    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SampleServiceImpl sampleService;

    // TODO: remove this bean along with DELETEME__dummyObjectsToDb
    @MockBean
    private DELETEME__dummyObjectsToDb deleteme__dummyObjectsToDb;

    @MockBean
    private CurrencyServiceImpl currencyService;

    @Mock
    private MongoBulkWriteException mongoBulkWriteException;

    @BeforeClass
    public static void setBeforeAll() {
        DATE_FORMAT.setTimeZone(TIMEZONE_UTC);
    }

    @Before
    public void setUp() {
        when(this.mongoBulkWriteException.getWriteErrors()).thenReturn(Collections.singletonList(new BulkWriteError(11000, "E11000 duplicate key error collection: test.samples index: title dup key: { : \"SOME TITLE\" }", new BsonDocument(), 0)));
    }

    @Test
    public void getSamples() throws Exception {
        when(this.sampleService.getSamplesByUsername("admin")).thenReturn(createdSamplesWithIds());

        this.mockMvc
                .perform(get("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasItems("$[*].id", "id-super", "id-title", "id-that", "id-describes", "id-samples"));
    }

    @Test
    public void getSamplesByTitle() throws Exception {
        when(this.sampleService.getSamplesByTitle("admin", "that")).thenReturn(filterCreatedSamplesByTitle("that"));

        this.mockMvc
                .perform(get("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .param("title", "that")
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasSize("$.*", 1))
                .andExpect(getMatcherForHasItems("$[*].id", "id-that"))
                .andExpect(getMatcherForHasItems("$[*].title", "that"));
    }

    @Test
    public void getSamplesByUsernameAndPreciseDate() throws Exception {
        Date preciseDate = formatDate("2019-01-01");

        when(this.sampleService.getSamplesByUsernameAndPreciseDate("admin", preciseDate)).thenReturn(filterCreatedSamplesByDate(preciseDate));

        this.mockMvc
                .perform(get("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .param("date", "2019-01-01T00:00:00.000Z")
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasSize("$.*", 2))
                .andExpect(getMatcherForHasItems("$[*].date", "2019-01-01T00:00:00.000Z"))
                .andExpect(getMatcherForHasItems("$[*].title", "super", "title"));
    }

    @Test
    public void getSamplesByUsernameAndDateAndCurrencyCode() throws Exception {
        Date startDate = formatDate("2019-03-01");
        Date endDate = formatDate("2019-04-01");
        String currencyCode = "EUR";

        when(this.sampleService.getSamplesByUsernameAndDateAndCurrencyCode("admin", startDate, endDate, currencyCode)).thenReturn(filterCreatedSamplesByDatesAndCurrency(startDate, endDate, currencyCode));

        this.mockMvc
                .perform(get("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .param("startDate", "2019-03-01T00:00:00.000Z")
                                 .param("endDate", "2019-04-01T00:00:00.000Z")
                                 .param("currencyCode", "EUR")
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasSize("$.*", 1))
                .andExpect(getMatcherForHasItems("$[*].date", "2019-03-01T00:00:00.000Z"))
                .andExpect(getMatcherForHasItems("$[*].expiredDate", "2019-04-01T00:00:00.000Z"))
                .andExpect(getMatcherForHasItems("$[*].title", "samples"));
    }

    @Test
    public void getSamplesByOverlappingDateRangeAndUsername() throws Exception {
        Date startDate = formatDate("2019-01-01");
        Date endDate = formatDate("2019-03-01");

        when(this.sampleService.getSamplesByOverlappingDateRangeAndUsername("admin", startDate, endDate)).thenReturn(filterCreatedSamplesByDates(startDate, endDate));

        this.mockMvc
                .perform(get("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .param("startDate", "2019-01-01T00:00:00.000Z")
                                 .param("endDate", "2019-03-01T00:00:00.000Z")
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasSize("$.*", 3))
                .andExpect(getMatcherForHasItems("$[*].date", "2019-01-01T00:00:00.000Z", "2019-02-01T00:00:00.000Z"))
                .andExpect(getMatcherForHasItems("$[*].expiredDate", "2019-02-01T00:00:00.000Z", "2019-03-01T00:00:00.000Z"))
                .andExpect(getMatcherForHasItems("$[*].title", "super", "title", "that"));


    }

    @Test
    public void createSamples() throws Exception {
        when(this.sampleService.create(anyList(), anyString())).thenReturn(createdSamplesWithIds());

        this.mockMvc
                .perform(post("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(OBJECT_MAPPER.writeValueAsString(createdSamples()))
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(getMatcherForHasItems("$[*].id", "id-super", "id-title", "id-that", "id-describes", "id-samples"));
    }

    @Test
    public void createSamplesDuplicatedKey() throws Exception {
        when(this.sampleService.create(anyList(), anyString())).thenThrow(this.mongoBulkWriteException);

        this.mockMvc
                .perform(post("/super-endpoint/samples")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(OBJECT_MAPPER.writeValueAsString(createdSamplesWithDuplicatedTitles()))
                                 .principal(() -> "admin"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
                .andExpect(getMatcherForOneItem("$.message", "[E11000 duplicate key error collection: test.samples index: title dup key: { : \"SOME TITLE\" }]"))
                .andExpect(getMatcherForOneItem("$.contextPath", "/super-endpoint/samples"));
    }

    @Test
    public void createSamplesFailed() throws Exception {
        this.mockMvc
                .perform(post("/super-endpoint/samples").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
                .andExpect(getMatcherForOneItem("$.message", "Cannot deserialize instance of `java.util.ArrayList` out of START_OBJECT token\n at [Source: (PushbackInputStream); line: 1, column: 1]"))
                .andExpect(getMatcherForOneItem("$.contextPath", "/super-endpoint/samples"));
    }

    private static List<Sample> filterCreatedSamplesByDates(Date startDate, Date endDate) {
        return createdSamplesWithIds().stream().filter(sample -> sample.getDate().compareTo(startDate) >= 0 && sample.getExpiredDate().compareTo(endDate) <= 0).collect(Collectors.toList());
    }

    private static List<Sample> filterCreatedSamplesByDatesAndCurrency(Date startDate, Date endDate, String currencyCode) {
        return createdSamplesWithIds().stream().filter(sample -> sample.getDate().compareTo(startDate) == 0 && sample.getExpiredDate().compareTo(endDate) == 0 && sample.getCurrency().getCode().equals(currencyCode)).collect(Collectors.toList());
    }

    private static List<Sample> filterCreatedSamplesByDate(Date... dates) {
        return createdSamplesWithIds().stream().filter(sample -> Arrays.asList(dates).contains(sample.getDate())).collect(Collectors.toList());
    }

    private static List<Sample> filterCreatedSamplesByTitle(String... titles) {
        return createdSamplesWithIds().stream().filter(sample -> Arrays.asList(titles).contains(sample.getTitle())).collect(Collectors.toList());
    }

    private static List<Sample> createdSamplesWithDuplicatedTitles() {
        return createdSamples().stream().peek(sample -> sample.setTitle("duplicated-title")).collect(Collectors.toList());
    }

    private static List<Sample> createdSamplesWithIds() {
        return createdSamples().stream().peek(sample -> sample.setId(String.format("id-%s", sample.getTitle()))).collect(Collectors.toList());
    }

    private static List<Sample> createdSamples() {
        return List.of(
            new Sample("super", USD, formatDate("2019-01-01"), formatDate("2019-02-01"), "admin"),
            new Sample("title", EUR, formatDate("2019-01-01"), formatDate("2019-02-01"), "admin"),
            new Sample("that", USD, formatDate("2019-02-01"), formatDate("2019-03-01"), "admin"),
            new Sample("describes", USD, formatDate("2019-03-01"), formatDate("2019-04-01"), "admin"),
            new Sample("samples", EUR, formatDate("2019-03-01"), formatDate("2019-04-01"), "admin")
        );
    }

    private static Date formatDate(String date) {
        try {
            return DATE_FORMAT.parse(date + "T00:00:00.000Z");
        }
        catch (ParseException e) {
            return new Date();
        }
    }
}