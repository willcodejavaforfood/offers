package com.echolima.offers;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateOfferRequest {
    private String iso4217CurrencyCode;
    private BigDecimal offerPriceInPence;
    private LocalDate expiryDate;
    private String description;

    public CreateOfferRequest() {
    }

    public CreateOfferRequest(
            String iso4217CurrencyCode, BigDecimal offerPriceInPence, LocalDate expiryDate, String description) {
        this.iso4217CurrencyCode = iso4217CurrencyCode;
        this.offerPriceInPence = offerPriceInPence;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public String getIso4217CurrencyCode() {
        return iso4217CurrencyCode;
    }

    public void setIso4217CurrencyCode(String iso4217CurrencyCode) {
        this.iso4217CurrencyCode = iso4217CurrencyCode;
    }

    public BigDecimal getOfferPriceInPence() {
        return offerPriceInPence;
    }

    public void setOfferPriceInPence(BigDecimal offerPriceInPence) {
        this.offerPriceInPence = offerPriceInPence;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
