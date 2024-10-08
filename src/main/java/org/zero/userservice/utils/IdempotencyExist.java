package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zero.userservice.exception.RequestException;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class IdempotencyExist<T> {
    private final String idempotencyKey;
    private final IdempotencyProvider idempotencyProvider;
    private final IdempotencyProvider.Idempotency idempotency;

    public Optional<T> supplyIfExistOrThrow(Supplier<T> isEquals, Supplier<T> notEquals) {
        if (idempotency.exist()) {
            if (idempotency.equals()) {
                log.info("Re-query with idempotency key -> {}", idempotencyKey);
                return Optional.of(isEquals.get());
            }
            log.info("Re-query with other data and idempotency key -> {}", idempotencyKey);
            return Optional.of(trySupply(notEquals));
        }
        return Optional.empty();
    }

    private T trySupply(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            idempotencyProvider.delete(idempotencyKey);
            log.info("Couldn't found response object for idempotency key -> {}", idempotencyKey);
            throw new RequestException("Особу не знайдено. Спробуйте пізніше.");
        }
    }
}
