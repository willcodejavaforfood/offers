package com.echolima.offers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class OffersRepository {
    private final Map<String, Offer> idToOfferMap;
    private final TimeService timeService;

    @Autowired
    public OffersRepository(TimeService timeService, Map<String, Offer> idToOfferMap) {
        this.idToOfferMap = idToOfferMap;
        this.timeService = timeService;
    }

    public void createOffer(Offer offer) {
        checkArgument(offer != null, "Offer cannot be null");
        idToOfferMap.put(offer.getId(), offer);
    }

    public Optional<Offer> getOffer(String id) {
        checkArgument(!Strings.isNullOrEmpty(id), "id cannot be empty or null");
        return Optional.ofNullable(idToOfferMap.get(id)).map(this::expire);
    }

    public List<Offer> getOffers() {
        return idToOfferMap.values().stream().map(this::expire).collect(Collectors.toList());
    }

    @VisibleForTesting
    void deleteAll() {
        idToOfferMap.clear();
    }

    public void cancel(String id) {
        checkArgument(!Strings.isNullOrEmpty(id), "id cannot be null or empty");
        Optional<Offer> offerOptional = getOffer(id);
        if (offerOptional.isPresent() && !offerOptional.get().isExpired()) {
            idToOfferMap.put(id, Offer.builder(offerOptional.get())
                    .withCancelled(true)
                    .build());
        }
    }

    private Offer expire(Offer offer) {
        return timeService.now().isAfter(offer.getExpiryDate())
                ? Offer.builder(offer).withExpired(true).build()
                : offer;
    }
}
