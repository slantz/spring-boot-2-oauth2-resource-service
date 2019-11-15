package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class SampleCustomRepositoryImpl implements SampleCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Sample> findByUsernameAndDateAndCurrencyTitle(String username, Date startDate, Date endDate, String currencyCode) {
        List<Sample> expensesByDates = this.mongoTemplate.find(query(where("username").is(username).andOperator(where("date").gte(startDate).andOperator(where("date").lte(endDate)))), Sample.class);

        return expensesByDates
                .stream()
                .filter(expense -> expense.getCurrency().getCode().equals(currencyCode))
                .collect(Collectors.toList());
    }
}
