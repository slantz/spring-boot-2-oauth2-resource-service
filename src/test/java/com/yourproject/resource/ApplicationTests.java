package com.yourproject.resource;

import com.yourproject.resource.currency.CurrencyRepository;
import com.yourproject.resource.sample.SampleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ApplicationTests {

	@MockBean
	private MongoTemplate mongoTemplate;

	@MockBean
	private DELETEME__dummyObjectsToDb deleteme__dummyObjectsToDb;

	@MockBean
	private SampleRepository sampleRepository;

	@MockBean
	private CurrencyRepository currencyRepository;

	@Test
	public void contextLoads() {}
}