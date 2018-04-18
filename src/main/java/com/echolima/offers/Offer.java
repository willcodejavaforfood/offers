package com.echolima.offers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

@JsonDeserialize(builder = Offer.OfferBuilder.class)
public final class Offer {
    public static OfferBuilder builder() {
        return new OfferBuilder();
    }

    public static OfferBuilder builder(Offer offer) {
        return new OfferBuilder(offer);
    }

    private String id;
    private Currency currency;
    private BigDecimal priceInPence;
    private LocalDate expiryDate;
    private String description;
    private boolean cancelled;
    private boolean expired;

    public Offer(
            String id,
            Currency currency,
            BigDecimal priceInPence,
            LocalDate expiryDate,
            String description,
            boolean cancelled,
            boolean expired) {
        checkArgument(!isNullOrEmpty(id), "id cannot be null or empty");
        checkArgument(currency != null, "currency cannot be null");
        checkArgument(priceInPence != null, "priceInPence cannot be null");
        checkArgument(expiryDate != null, "expiryDate cannot be null");
        checkArgument(!isNullOrEmpty(description), "description cannot be null or empty");
        this.id = id;
        this.currency = currency;
        this.priceInPence = priceInPence;
        this.expiryDate = expiryDate;
        this.description = description;
        this.cancelled = cancelled;
        this.expired = expired;
    }

    public String getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPriceInPence() {
        return priceInPence;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isExpired() {
        return expired;
    }

    @JsonPOJOBuilder
    public static class OfferBuilder {
        private String id;
        private Currency currency;
        private BigDecimal priceInPence;
        private LocalDate expiryDate;
        private String description;
        private boolean isCancelled;
        private boolean isExpired;

        public OfferBuilder() {}

        public OfferBuilder(Offer offer) {
            this.id = offer.getId();
            this.currency = offer.getCurrency();
            this.priceInPence = offer.getPriceInPence();
            this.expiryDate = offer.getExpiryDate();
            this.description = offer.getDescription();
            this.isCancelled = offer.cancelled;
            this.isExpired = offer.expired;
        }

        public OfferBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public OfferBuilder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public OfferBuilder withPriceInPence(BigDecimal priceInPence) {
            this.priceInPence = priceInPence;
            return this;
        }

        public OfferBuilder withExpiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public OfferBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public OfferBuilder withCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
            return this;
        }

        public OfferBuilder withExpired(boolean isExpired) {
            this.isExpired = isExpired;
            return this;
        }

        public Offer build() {
            return new Offer(id, currency, priceInPence, expiryDate, description, isCancelled, isExpired);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return Objects.equal(currency, offer.currency)
                && Objects.equal(priceInPence, offer.priceInPence)
                && Objects.equal(expiryDate, offer.expiryDate)
                && Objects.equal(id, offer.id)
                && Objects.equal(cancelled, offer.cancelled)
                && Objects.equal(expired, offer.expired)
                && Objects.equal(description, offer.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currency, priceInPence, expiryDate, description, cancelled, expired);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currency", currency)
                .add("priceInPence", priceInPence)
                .add("expiryDate", expiryDate)
                .add("description", description)
                .add("id", id)
                .add("cancelled", cancelled)
                .add("expired", expired)
                .toString();
    }
}
