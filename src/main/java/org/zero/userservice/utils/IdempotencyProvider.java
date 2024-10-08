package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zero.userservice.repository.IdempotencyRepository;

@Component
@RequiredArgsConstructor
public class IdempotencyProvider {
    private final IdempotencyRepository idempotencyRepository;
    @Value("${spring.application.name}")
    private String serviceName;

    public IdempotencyExist<Object> check(String idempotencyKey, Object value) {
        var founded = idempotencyRepository.findById(getFormatedIdempotencyKey(idempotencyKey, serviceName));

        Idempotency checkResult = founded
                .map(idempotency -> new Idempotency(true, idempotency.getIdempotencyHash().equals(value.hashCode())))
                .orElseGet(() -> new Idempotency(false, false));

        return new IdempotencyExist<Object>(idempotencyKey, this, checkResult);
    }

    public void delete(String idempotencyKey) {
        idempotencyRepository.deleteById(getFormatedIdempotencyKey(idempotencyKey, serviceName));
    }

    public void save(String idempotencyKey, Object value) {
        saveWithHash(idempotencyKey, value.hashCode());
    }

    private void saveWithHash(String idempotencyKey, Integer createdUserHash) {
        var idempotency = new org.zero.userservice.entity.Idempotency();
        idempotency.setIdempotencyHash(createdUserHash);
        idempotency.setIdempotencyKey(getFormatedIdempotencyKey(idempotencyKey, serviceName));
        idempotencyRepository.save(idempotency);
    }

    private static String getFormatedIdempotencyKey(String idempotencyKey, String serviceName) {
        return idempotencyKey + "-" + serviceName;
    }

    public record Idempotency(boolean exist, boolean equals) {
    }
}
