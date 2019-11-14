package com.yourproject.resource.model.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Currency {

    @Id
//    @JsonIgnore
    @JsonProperty
    private String id;

    @Indexed(unique = true)
    @JsonProperty
    private String title;

    @Indexed(unique = true)
    @JsonProperty
    private String symbol;

    private Currency() {}

    public Currency(String title, String symbol) {
        this.title = title;
        this.symbol = symbol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getTitle() {
        return this.title;
    }
}
