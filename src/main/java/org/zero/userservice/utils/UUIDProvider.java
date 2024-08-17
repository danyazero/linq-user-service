package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
@RequiredArgsConstructor
public class UUIDProvider {
    private final SyncEncryption syncEncryption;

    @SneakyThrows
    public String generate(Integer userId) {
//        var time = new GregorianCalendar();
//        var salt = time.get(Calendar.DAY_OF_MONTH) + "" + time.get(Calendar.MONTH) + time.get(Calendar.HOUR) + time.get(Calendar.MINUTE);
        return Base64Encoder.apply(syncEncryption.init().encrypt(userId.toString()));
    }

    @SneakyThrows
    public Integer get(String uuid) {
        var userId = syncEncryption.init().decrypt(Base64Decoder.apply(uuid));
        System.out.println(userId);
        return Integer.parseInt(userId);
    }
}
