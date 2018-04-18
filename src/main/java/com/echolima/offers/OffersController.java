package com.echolima.offers;

import com.google.common.base.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/offers")
public class OffersController {
    private final OffersRepository offersRepository;

    public OffersController(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
    }

    @PostMapping
    public ResponseEntity<?> createOffer(@RequestBody CreateOfferRequest createOfferRequest) {
        if (createOfferRequest == null || isInvalid(createOfferRequest)) {
            return ResponseEntity.badRequest().build();
        }
        String id = UUID.randomUUID().toString();
        offersRepository.createOffer(Offer.builder()
                .withId(id)
                .withPriceInPence(createOfferRequest.getOfferPriceInPence())
                .withExpiryDate(createOfferRequest.getExpiryDate())
                .withCurrency(Currency.getInstance(createOfferRequest.getIso4217CurrencyCode()))
                .withDescription(createOfferRequest.getDescription())
                .build());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public List<Offer> getOffers() {
        return offersRepository.getOffers();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOffer(@PathVariable("id") String id) {
        Optional<Offer> offerOptional = offersRepository.getOffer(id);

        return offerOptional.isPresent() ? ResponseEntity.ok(offerOptional.get()) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> cancelOffer(@PathVariable("id") String id) {
        if (offersRepository.getOffer(id).isPresent()) {
            offersRepository.cancel(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private static boolean isInvalid(CreateOfferRequest request) {
        return Strings.isNullOrEmpty(request.getDescription())
                || Strings.isNullOrEmpty(request.getIso4217CurrencyCode())
                || isInvalidCurrencyCode(request.getIso4217CurrencyCode())
                || request.getExpiryDate() == null
                || request.getOfferPriceInPence() == null;
    }

    private static boolean isInvalidCurrencyCode(String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }
}
