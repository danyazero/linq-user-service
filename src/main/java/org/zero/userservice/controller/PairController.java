package org.zero.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.userservice.model.FullPairRequest;
import org.zero.userservice.model.PairRequest;
import org.zero.userservice.model.PairResponse;
import org.zero.userservice.service.PairService;

@RestController
@RequestMapping("/api/v1/pair")
@RequiredArgsConstructor
public class PairController {
    private final PairService pairService;

    @PutMapping
    public String createPair(
            @RequestBody PairRequest pair,
            @RequestHeader("X-Request-ID") String idempotencyKey,
            @RequestHeader("X-Issuer-ID") String issuerId
    ) {
        return pairService.createPair(pair, idempotencyKey, issuerId);
    }

    @PatchMapping
    public PairResponse createContactPersonAndGetPair(
            @RequestBody FullPairRequest pair,
            @RequestHeader("X-Request-ID") String idempotencyKey,
            @RequestHeader("X-Issuer-ID") String issuerId
    ) {
        return pairService.createContactPersonAndGetPair(pair, idempotencyKey, issuerId);
    }

}
