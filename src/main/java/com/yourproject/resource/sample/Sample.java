package com.yourproject.resource.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yourproject.resource.currency.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * Sample MongoDb document.
 */
public class Sample {
    @Id
    @JsonProperty
    private String id;

    @JsonProperty
    @Indexed(unique = true)
    private String title;

    @DBRef
    @JsonProperty
    private Currency currency;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date date;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date expiredDate;

    @Indexed
    @JsonProperty
    private String username;

    private Sample() {}

    public Sample(String title, Currency currency, Date date, Date expiredDate, String username) {
        this.title = title;
        this.currency = currency;
        this.date = date;
        this.expiredDate = expiredDate;
        this.username = username;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Date getDate() {
        return this.date;
    }

    public Date getExpiredDate() {
        return this.expiredDate;
    }

    public String getUsername() {
        return this.username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("Sample[id=%s, title='%s', currency='%s', date='%s', expiredDate='%s', username='%s']",
                             this.id,
                             this.title,
                             this.currency.toString(),
                             this.date.toString(),
                             this.expiredDate.toString(),
                             this.username);
    }
}
