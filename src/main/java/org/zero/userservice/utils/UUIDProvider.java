package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UUIDProvider {
    private final SyncEncryption syncEncryption;

    @SneakyThrows
    public String generate(Integer userId) {
        return Base64Encoder.apply(syncEncryption.init().encrypt(userId.toString()));
    }

    @SneakyThrows
    public Integer get(String uuid) {
        var userId = syncEncryption.init().decrypt(Base64Decoder.apply(uuid));
        return Integer.parseInt(userId);
    }
}
