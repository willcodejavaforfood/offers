package com.echolima.offers;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OffersRepositoryTest {
    @InjectMocks
    private OffersRepository offersRepository;
    @Mock
    private Map<String, Offer> idToOffer;
    @Mock
    private TimeService timeService;

    @Before
    public void setup() {
        given(timeService.now()).willReturn(LocalDate.now());
    }

    @Test
    public void shouldSaveOffer() {
        Offer offerToSave = Offer.builder()
                .withDescription("An Offer")
                .withPriceInPence(BigDecimal.valueOf(123456))
                .withExpiryDate(LocalDate.now().plusWeeks(2))
                .withCurrency(Currency.getInstance("SEK"))
                .withId("id")
                .withCancelled(false)
                .withExpired(false)
                .build();



        offersRepository.createOffer(offerToSave);

        verify(idToOffer).put("id", offerToSave);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullOfferWhenCreating() {
        offersRepository.createOffer(null);
    }

    @Test
    public void shouldGetOffer() {
        Offer offerToSave = Offer.builder()
                .withDescription("An Offer")
                .withPriceInPence(BigDecimal.valueOf(123456))
                .withExpiryDate(LocalDate.now().plusWeeks(2))
                .withCurrency(Currency.getInstance("SEK"))
                .withId("id")
                .withCancelled(false)
                .withExpired(false)
                .build();

        given(idToOffer.get("id")).willReturn(offerToSave);

        assertThat(offersRepository.getOffer("id").get(), is(offerToSave));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullIdWhenGettingOffer() {
        offersRepository.getOffer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyIdWhenGettingOffer() {
        offersRepository.getOffer("");
    }

    @Test
    public void shouldHandleGetsForMissingOffer() {
        assertThat(offersRepository.getOffer("id").isPresent(), is(false));
    }

    @Test
    public void shouldExpireOffer() {
        given(idToOffer.get("id")).willReturn(offer("id", LocalDate.now().minusDays(2)));

        assertThat(offersRepository.getOffer("id").get().isExpired(), is(true));
    }

    @Test
    public void shouldExpireOffers() {
        given(idToOffer.values()).willReturn(Lists.newArrayList(
                offer("id1", LocalDate.now().plusWeeks(1)),
                offer("id2", LocalDate.now().minusDays(1)),
                offer("id3", LocalDate.now().plusWeeks(1)),
                offer("id4", LocalDate.now().plusWeeks(3)),
                offer("id5", LocalDate.now().plusWeeks(4)),
                offer("id6", LocalDate.now().minusMonths(4))));

        assertThat(offersRepository.getOffers().stream().filter(Offer::isExpired).count(), is(2L));
    }

    @Test
    public void shouldCancelOffer() {
        Offer cancelMe = offer("cancelMe", LocalDate.now().plusWeeks(4));
        given(idToOffer.get("cancelMe")).willReturn(cancelMe);

        offersRepository.cancel("cancelMe");
        verify(idToOffer).put("cancelMe", Offer.builder(cancelMe).withCancelled(true).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullIdWhenCancellingOffer() {
        offersRepository.cancel(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyIdWhenCancellingOffer() {
        offersRepository.cancel("");
    }

    @Test
    public void shouldNotCancelExpiredOffer() {
        Offer expiredOffer = offer("id", LocalDate.now().minusMonths(2));
        given(idToOffer.get("id")).willReturn(expiredOffer);

        offersRepository.cancel("id");

        verify(idToOffer, never()).put("id", Offer.builder(expiredOffer).withCancelled(true).build());
    }

    public static Offer offer(String id, LocalDate expiryDate) {
        return Offer.builder()
                .withDescription("An Offer")
                .withPriceInPence(BigDecimal.valueOf(123456))
                .withExpiryDate(expiryDate)
                .withCurrency(Currency.getInstance("SEK"))
                .withId(id)
                .withCancelled(false)
                .withExpired(false)
                .build();
    }
}