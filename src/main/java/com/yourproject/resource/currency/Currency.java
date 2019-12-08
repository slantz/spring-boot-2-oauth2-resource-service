package com.yourproject.resource.currency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Currency MongoDb document.
 */
@Document
public class Currency {

    @Id
    @JsonIgnore
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

    @Override
    public String toString() {
        return String.format("Currency[id=%s, code='%s', symbol='%s']",
                             this.id,
                             this.code,
                             this.symbol);
    }
}
