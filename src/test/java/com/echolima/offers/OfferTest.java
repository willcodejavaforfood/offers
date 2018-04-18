package com.echolima.offers;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class OfferTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutDescription() {
        Offer.builder()
                .withCurrency(Currency.getInstance("EUR"))
                .withExpiryDate(LocalDate.now())
                .withPriceInPence(BigDecimal.TEN)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutCurrencyCode() {
        Offer.builder()
                .withExpiryDate(LocalDate.now())
                .withPriceInPence(BigDecimal.TEN)
                .withDescription("23456")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutPrice() {
        Offer.builder()
                .withDescription("234454")
                .withCurrency(Currency.getInstance("EUR"))
                .withExpiryDate(LocalDate.now())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutExpiryDate() {
        Offer.builder()
                .withDescription("234454")
                .withCurrency(Currency.getInstance("EUR"))
                .withPriceInPence(BigDecimal.TEN)
                .build();
    }

}