package com.yourproject.resource.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Implementation of {@link SampleCustomRepository}.
 */
public class SampleCustomRepositoryImpl implements SampleCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> findByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date endDate, String currencyCode) {
        List<Sample> samples = this.mongoTemplate.find(query(where("username").is(username).andOperator(where("date").gte(startDate).andOperator(where("date").lte(endDate)))), Sample.class);

        return samples
                .stream()
                .filter(expense -> expense.getCurrency().getCode().equals(currencyCode))
                .collect(Collectors.toList());
    }
}
