//package com.yourproject.resource.controller;
//
//import com.mongodb.MongoBulkWriteException;
//import com.mongodb.bulk.BulkWriteError;
//import com.yourproject.resource.error.MissingIdException;
//import org.bson.BsonDocument;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.net.URLEncoder;
//import java.util.AbstractMap;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//
//import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE;
//import static com.yourproject.resource.util.TestUtil.getMatcherForHasItems;
//import static com.yourproject.resource.util.TestUtil.getMatcherForOneItem;
//import static org.hamcrest.Matchers.emptyIterableOf;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * Tests just web layer, doesn't init the whole Spring Boot server, just necessary controller.
// *
// * Auth for endpoints is disabled.
// */
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = CurrencyController.class, secure=false)
//public class CurrencyControllerTest {
//
//    private static final String CURRENCY_ID = "5b0412f014d35a48c9148e40";
//    private static final String CURRENCY_TITLE = "UAH";
//    private static final String CURRENCY_SYMBOL = "₴";
//
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CurrencyService currencyService;
//
//    @Mock
//    private MongoBulkWriteException mongoBulkWriteException;
//
//    @Before
//    public void setUp() {
//        when(this.mongoBulkWriteException.getWriteErrors()).thenReturn(Collections.singletonList(new BulkWriteError(11000, "E11000 duplicate key error collection: test.currency index: title dup key: { : \"EUR\" }", new BsonDocument(), 0)));
//    }
//
//    @Test
//    public void getCurrencies() throws Exception {
//        HashMap<String, String> titleSymbols = new HashMap<>();
//
//        titleSymbols.put("UAH", "₴");
//        titleSymbols.put("USD", "$");
//        titleSymbols.put("EUR", "€");
//        titleSymbols.put("JPY", "¥");
//        titleSymbols.put("BYN", "Br");
//
//        when(this.currencyService.findAll()).thenReturn(createAllResponseOfCurrencyService(titleSymbols));
//
//        this.mockMvc
//                .perform(get("/currencies"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                // order doesn't matter
//                .andExpect(getMatcherForHasItems("$.[*].title", "UAH", "USD", "EUR", "JPY", "BYN"))
//                .andExpect(getMatcherForHasItems("$.[*].symbol", "Br", "₴", "€", "$", "¥"));
//    }
//
//    @Test
//    public void getCurrenciesNoDataInDb() throws Exception {
//        when(this.currencyService.findAll()).thenReturn(createAllResponseOfCurrencyService());
//
//        this.mockMvc
//                .perform(get("/currencies"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$", emptyIterableOf(Currency.class)));
//    }
//
//    @Test
//    public void getCurrency() throws Exception {
//        Currency currency = new Currency("UAH", "₴");
//        currency.setId(CURRENCY_ID);
//
//        when(this.currencyService.findById(CURRENCY_ID)).thenReturn(currency);
//
//        this.mockMvc
//                .perform(get("/currencies/{currencyId}", CURRENCY_ID))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$.title", "UAH"))
//                .andExpect(getMatcherForOneItem("$.symbol", "₴"));
//    }
//
//    @Test
//    public void getCurrencyWithWrongOrOutdatedId() throws Exception {
//        when(this.currencyService.findById(CURRENCY_ID)).thenThrow(new NoSuchElementException(NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
//
//        this.mockMvc
//                .perform(get("/currencies/{currencyId}", CURRENCY_ID))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andExpect(getMatcherForOneItem("$.status", "NOT_FOUND"))
//                .andExpect(getMatcherForOneItem("$.message", "suka"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies/5b0412f014d35a48c9148e40"));
//    }
//
//    @Test
//    public void getCurrencyByTitle() throws Exception {
//        Currency currency = new Currency("UAH", "₴");
//
//        when(this.currencyService.findByTitle(CURRENCY_TITLE)).thenReturn(currency);
//
//        this.mockMvc
//                .perform(get("/currencies/title/{currencyTitle}", CURRENCY_TITLE))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$.title", "UAH"))
//                .andExpect(getMatcherForOneItem("$.symbol", "₴"));
//    }
//
//    @Test
//    public void getCurrencyByTitleNotEncoded() throws Exception {
//        this.mockMvc
//                .perform(get("/currencies/title/{currencyTitle}", "Some not encoded title"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(""));
//    }
//
//    @Test
//    public void getCurrencyByTitleWrongOrAlreadyDeleted() throws Exception {
//        when(this.currencyService.findByTitle(CURRENCY_TITLE)).thenThrow(new NoSuchElementException(NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
//
//        this.mockMvc
//                .perform(get("/currencies/title/{currencyTitle}", CURRENCY_TITLE))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andExpect(getMatcherForOneItem("$.status", "NOT_FOUND"))
//                .andExpect(getMatcherForOneItem("$.message", "suka"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies/title/UAH"));
//    }
//
//    @Test
//    public void getCurrencyBySymbol() throws Exception {
//        Currency currency = new Currency("UAH", "₴");
//        String encodeDCurrencySymbol = URLEncoder.encode(CURRENCY_SYMBOL, "UTF-8");
//
//        when(this.currencyService.findBySymbol(encodeDCurrencySymbol)).thenReturn(currency);
//
//        this.mockMvc
//                .perform(get("/currencies/symbol/{currencySymbol}", encodeDCurrencySymbol))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$.title", "UAH"))
//                .andExpect(getMatcherForOneItem("$.symbol", "₴"));
//    }
//
//    @Test
//    public void getCurrencyBySymbolWrongOrAlreadyDeleted() throws Exception {
//        String encodeDCurrencySymbol = URLEncoder.encode(CURRENCY_SYMBOL, "UTF-8");
//
//        when(this.currencyService.findBySymbol(encodeDCurrencySymbol)).thenThrow(new NoSuchElementException(NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
//
//        this.mockMvc
//                .perform(get("/currencies/symbol/{currencySymbol}", encodeDCurrencySymbol))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andExpect(getMatcherForOneItem("$.status", "NOT_FOUND"))
//                .andExpect(getMatcherForOneItem("$.message", "suka"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies/symbol/%E2%82%B4"));
//    }
//
//    @Test
//    public void getCurrencyBySymbolNotEncoded() throws Exception {
//        this.mockMvc
//                .perform(get("/currencies/symbol/{currencySymbol}", CURRENCY_SYMBOL))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(""));
//    }
//
//    @Test
//    public void postCurrencies() throws Exception {
//        HashMap<String, String> titleSymbols = new HashMap<>();
//
//        titleSymbols.put("UAH", "₴");
//
//        List<Currency> allResponseOfCurrencyService = createAllResponseOfCurrencyService(titleSymbols);
//
//        when(this.currencyService.createCurrencies(anyList())).thenReturn(allResponseOfCurrencyService);
//
//
//        this.mockMvc
//                .perform(post("/currencies")
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(OBJECT_MAPPER.writeValueAsString(allResponseOfCurrencyService)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForHasItems("$.[*].title", "UAH"))
//                .andExpect(getMatcherForHasItems("$.[*].symbol", "₴"));
//    }
//
//    @Test
//    public void postCurrenciesDuplicatedKey() throws Exception {
//        HashMap<String, String> titleSymbols = new HashMap<>();
//        titleSymbols.put("EUR", "€");
//        List<Currency> allResponseOfCurrencyService = createAllResponseOfCurrencyService(titleSymbols);
//
//        when(this.currencyService.createCurrencies(anyList())).thenThrow(this.mongoBulkWriteException);
//
//        this.mockMvc
//                .perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(allResponseOfCurrencyService)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
//                .andExpect(getMatcherForOneItem("$.message", "[E11000 duplicate key error collection: test.currency index: title dup key: { : \"EUR\" }]"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies"));
//    }
//
//    @Test
//    public void postCurrenciesFailed() throws Exception {
//        this.mockMvc
//                .perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content("{}"))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
//                .andExpect(getMatcherForOneItem("$.message", "Cannot deserialize instance of `java.util.ArrayList` out of START_OBJECT token\n at [Source: (PushbackInputStream); line: 1, column: 1]"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies"));
//    }
//
//    @Test
//    public void putCurrencies() throws Exception {
//        HashMap<String, String> titleSymbols = new HashMap<>();
//        titleSymbols.put("USD", "$");
//        List<Currency> allResponseOfCurrencyService = createAllResponseOfCurrencyService(titleSymbols);
//
//        when(this.currencyService.updateCurrencies(anyList())).thenReturn(allResponseOfCurrencyService);
//
//
//        this.mockMvc
//                .perform(put("/currencies")
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(OBJECT_MAPPER.writeValueAsString(allResponseOfCurrencyService)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForHasItems("$.[*].title", "USD"))
//                .andExpect(getMatcherForHasItems("$.[*].symbol", "$"));
//    }
//
//    /**
//     * This scenario can occur only for already existing ids that are being updated.
//     */
//    @Test
//    public void putCurrenciesDuplicatedKeyUpdated() throws Exception {
//        HashMap<String, AbstractMap.SimpleEntry<String, String>> titleSymbols = new HashMap<>();
//        titleSymbols.put("111", new AbstractMap.SimpleEntry<>("EUR", "€"));
//        titleSymbols.put("222", new AbstractMap.SimpleEntry<>("EUR", "$"));
//
//        List<Currency> allResponseOfCurrencyService = createAllResponseOfCurrencyServiceWithId(titleSymbols);
//
//        when(this.currencyService.createCurrencies(anyList())).thenThrow(this.mongoBulkWriteException);
//
//        this.mockMvc
//                .perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(allResponseOfCurrencyService)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
//                .andExpect(getMatcherForOneItem("$.message", "[E11000 duplicate key error collection: test.currency index: title dup key: { : \"EUR\" }]"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies"));
//    }
//
//    @Test
//    public void putCurrenciesSomeIdsAreMissing() throws Exception {
//        HashMap<String, String> titleSymbols = new HashMap<>();
//        titleSymbols.put("EUR", "€");
//        List<Currency> allResponseOfCurrencyService = createAllResponseOfCurrencyService(titleSymbols);
//
//        allResponseOfCurrencyService.add(createCurrency("111", "USD", "$"));
//
//        when(this.currencyService.createCurrencies(anyList())).thenThrow(new MissingIdException("Some ids are missing for bulk currency update."));
//
//        this.mockMvc
//                .perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(allResponseOfCurrencyService)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
//                .andExpect(getMatcherForOneItem("$.message", "DB object is missing ['id'] ['Some ids are missing for bulk currency update.']"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies"));
//    }
//
//    @Test
//    public void putCurrenciesFailed() throws Exception {
//        this.mockMvc
//                .perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content("[{\"title\":\"cxczxc, \"symbol\": \"AA\"}]"))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(getMatcherForOneItem("$.status", "BAD_REQUEST"))
//                .andExpect(getMatcherForOneItem("$.message", "Unexpected character ('s' (code 115)): was expecting comma to " +
//                        "separate Object entries\n at [Source: (PushbackInputStream); line: 1, column: 22]\n at [Source: " +
//                        "(PushbackInputStream); line: 1, column: 11] (through reference chain: java.util.ArrayList[0])"))
//                .andExpect(getMatcherForOneItem("$.contextPath", "/currencies"));
//    }
//
//    @Test
//    public void deleteCurrency() throws Exception {
//        when(this.currencyService.deleteCurrency(CURRENCY_ID)).thenReturn(true);
//
//        this.mockMvc
//                .perform(delete("/currencies/{currencyId}", CURRENCY_ID))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$", true));
//    }
//
//    @Test
//    public void deleteCurrencyFailed() throws Exception {
//        this.mockMvc
//                .perform(delete("/currencies/{currencyId}", CURRENCY_SYMBOL))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$", false));
//    }
//
//    @Test
//    public void deleteCurrencyAlreadyDeleted() throws Exception {
//        when(this.currencyService.deleteCurrency(CURRENCY_ID)).thenReturn(true);
//
//        this.mockMvc
//                .perform(delete("/currencies/{currencyId}", CURRENCY_ID))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(getMatcherForOneItem("$", true));
//    }
//
//    private static List<Currency> createAllResponseOfCurrencyService(Map<String, String> titleAndSymbols) {
//        List<Currency> currencies = new ArrayList<>();
//
//        titleAndSymbols.forEach((key, value) -> currencies.add(new Currency(key, value)));
//
//        return currencies;
//    }
//
//    private static List<Currency> createAllResponseOfCurrencyServiceWithId(Map<String, AbstractMap.SimpleEntry<String, String>> titleAndSymbols) {
//        List<Currency> currencies = new ArrayList<>();
//
//        titleAndSymbols.forEach((key, value) -> {
//            currencies.add(createCurrency(key, value.getKey(), value.getValue()));
//        });
//
//        return currencies;
//    }
//
//    private static List<Currency> createAllResponseOfCurrencyService() {
//        return createAllResponseOfCurrencyService(Collections.emptyMap());
//    }
//
//    private static Currency createCurrency(String id, String title, String symbol) {
//        Currency currency = new Currency(title, symbol);
//        currency.setId(id);
//        return currency;
//    }
//}