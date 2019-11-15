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
    private String code;

    @Indexed(unique = true)
    @JsonProperty
    private String symbol;

    private Currency() {}

    public Currency(String code, String symbol) {
        this.code = code;
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

    public String getCode() {
        return this.code;
    }
}
