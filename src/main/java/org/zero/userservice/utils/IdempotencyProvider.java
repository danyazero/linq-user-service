package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zero.userservice.entity.Idempotency;
import org.zero.userservice.repository.IdempotencyRepository;

@Component
@RequiredArgsConstructor
public class IdempotencyProvider {
    @Value("${spring.application.name}")
    private String serviceName;
    private final IdempotencyRepository idempotencyRepository;

    public Idempotency check(String idempotencyKey, String idempotencyValue) {
        var dataHash = SHAEncoder.apply(idempotencyValue, SHAEncoder.Encryption.SHA1);
        var founded = idempotencyRepository.findById(getFormatedIdempotencyKey(idempotencyKey, serviceName));

        return founded.map(idempotency -> new Idempotency(true, idempotency.getIdempotencyHash().equals(dataHash))).orElseGet(() -> new Idempotency(false, false));

    }

    public void delete(String idempotencyKey) {
        idempotencyRepository.deleteById(getFormatedIdempotencyKey(idempotencyKey, serviceName));
    }

    public void save(String idempotencyKey, String idempotencyValue) {
        var createdUserHash = SHAEncoder.apply(idempotencyValue, SHAEncoder.Encryption.SHA1);
        saveWithHash(idempotencyKey, createdUserHash);
    }

    public void saveWithHash(String idempotencyKey, String createdUserHash) {
        var idempotency = new org.zero.userservice.entity.Idempotency();
        idempotency.setIdempotencyHash(createdUserHash);
        idempotency.setIdempotencyKey(getFormatedIdempotencyKey(idempotencyKey, serviceName));
        idempotencyRepository.save(idempotency);
    }


    private static String getFormatedIdempotencyKey(String idempotencyKey, String serviceName) {
        return idempotencyKey + "-" + serviceName;
    }

    public record Idempotency(boolean exist, boolean equals) {}
}
