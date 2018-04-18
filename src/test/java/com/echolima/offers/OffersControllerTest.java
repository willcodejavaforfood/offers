package com.echolima.offers;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OffersApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OffersControllerTest {
    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private TimeService timeService;

    @Autowired
    private OffersRepository offersRepository;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/offers");
        offersRepository.deleteAll();
    }

    @Test
    public void shouldSaveOffer() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                createOfferRequest(timeService.now().plusMonths(1)),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldBadRequestSaveOfferWhenMissingExpiryDate() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                new CreateOfferRequest(
                    "EUR",
                    BigDecimal.valueOf(1234l),
                    null,
                    "IT Services"),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldBadRequestSaveOfferWhenMissingDescription() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                new CreateOfferRequest(
                        "EUR",
                        BigDecimal.valueOf(1234l),
                        timeService.now().plusDays(1),
                        null),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldBadRequestSaveOfferWhenMissingCurrencyCode() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                new CreateOfferRequest(
                        null,
                        BigDecimal.valueOf(1234l),
                        timeService.now().plusDays(1),
                        "Hot Dogs"),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldBadRequestSaveOfferWhenInvalidCurrencyCode() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                new CreateOfferRequest(
                        "NotACurrency",
                        BigDecimal.valueOf(1234l),
                        timeService.now().plusDays(1),
                        "Hot Dogs"),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldBadRequestSaveOfferWhenMissingPrice() {
        ResponseEntity<Void> createOfferResponse = template.postForEntity(
                base.toString(),
                new CreateOfferRequest(
                        "USD",
                        null,
                        timeService.now().plusDays(1),
                        "Hot Dogs"),
                Void.class);

        assertThat(createOfferResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldReadOffer() {
        LocalDate expiryDate = timeService.now().plusMonths(1);
        ResponseEntity<Void> createOfferResponse =
                template.postForEntity(base.toString(), createOfferRequest(expiryDate), Void.class);

        ResponseEntity<Offer> offerResponseEntity =
                template.getForEntity(createOfferResponse.getHeaders().getLocation(), Offer.class);

        assertThat(offerResponseEntity.getStatusCode(), is(HttpStatus.OK));

        Offer offer = offerResponseEntity.getBody();
        assertThat(offer.getId(), notNullValue());
        assertThat(offer, is(Offer.builder()
                .withCurrency(Currency.getInstance("EUR"))
                .withDescription("IT Services")
                .withExpiryDate(expiryDate)
                .withPriceInPence(BigDecimal.valueOf(1234))
                .withId(offer.getId())
                .build()));
    }

    @Test
    public void shouldReturnNotFound() {
        ResponseEntity<Offer> offerResponseEntity = template.getForEntity(
                UriComponentsBuilder.fromHttpUrl(
                        base.toString())
                        .pathSegment("/areallylongid")
                        .build().toUri(),
                Offer.class);

        assertThat(offerResponseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldGetOffers() throws URISyntaxException {
        IntStream.range(0, 3).forEach(i ->
                template.postForEntity(base.toString(), createOfferRequest(timeService.now().plusMonths(1)), Void.class));

        ResponseEntity<List<Offer>> responseEntity = template.exchange(
                base.toURI(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Offer>>() {});

        assertThat(responseEntity.getBody().size(), is(3));
    }

    @Test
    public void shouldCancelOffer() {
        URI location = template.postForEntity(
                        base.toString(),
                        createOfferRequest(timeService.now().plusMonths(1)),
                        Void.class)
                .getHeaders().getLocation();

        template.delete(location);
        HttpStatus responseStatusCode =
                template.exchange(location, HttpMethod.DELETE, null, Void.class).getStatusCode();
        assertThat(responseStatusCode, is(HttpStatus.NO_CONTENT));

        Offer cancelledOffer = template.getForEntity(location, Offer.class).getBody();
        assertThat(cancelledOffer.isCancelled(), is(true));
    }

    @Test
    public void shouldNotFoundWhenCancellingOfferWhichDoesntExist() {
        template.delete(UriComponentsBuilder.fromHttpUrl(base.toString()).pathSegment("fakeId").build().toUri());

        HttpStatus responseStatusCode = template.exchange(
                UriComponentsBuilder.fromHttpUrl(base.toString()).pathSegment("fakeId").build().toUri(),
                HttpMethod.DELETE,
                null,
                Void.class).getStatusCode();

        assertThat(responseStatusCode, is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldExpireOfferWhenGettingOneAtATime() {
        template.postForEntity(base.toString(), createOfferRequest(timeService.now().plusDays(5)), Void.class);
        URI locationOfExpiredOffer = template.postForEntity(
                base.toString(),
                createOfferRequest(timeService.now().minusDays(5)),
                Void.class).getHeaders().getLocation();
        template.postForEntity(base.toString(), createOfferRequest(timeService.now().plusDays(3)), Void.class);

        assertThat(template.getForEntity(locationOfExpiredOffer, Offer.class).getBody().isExpired(), is(true));
    }

    @Test
    public void shouldExpireOfferWhenGettingAll() throws URISyntaxException {
        template.postForEntity(base.toString(), createOfferRequest(timeService.now().plusDays(5)), Void.class);
        template.postForEntity(
                base.toString(),
                createOfferRequest(timeService.now().minusDays(5)),
                Void.class);
        template.postForEntity(base.toString(), createOfferRequest(timeService.now().plusDays(3)), Void.class);

        ResponseEntity<List<Offer>> responseEntity = template.exchange(
                base.toURI(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Offer>>() {});

        assertThat(responseEntity.getBody().size(), is(3));
        assertThat(responseEntity.getBody().stream().filter(Offer::isExpired).count(), is(1l));
    }

    @Test
    public void shouldNotCancelExpiredOffer() {
        URI locationOfExpiredOffer = template.postForEntity(
                base.toString(),
                createOfferRequest(timeService.now().minusDays(5)),
                Void.class).getHeaders().getLocation();

        template.delete(locationOfExpiredOffer);

        Offer offer = template.getForEntity(locationOfExpiredOffer, Offer.class).getBody();

        assertThat(offer.isCancelled(), is(false));
        assertThat(offer.isExpired(), is(true));
    }

    private CreateOfferRequest createOfferRequest(LocalDate expiryDate) {
        return new CreateOfferRequest(
                "EUR",
                BigDecimal.valueOf(1234l),
                expiryDate,
                "IT Services");
    }

}