package org.zero.userservice.utils;

import lombok.SneakyThrows;

public class RequestBodyDecoder {

    @SneakyThrows
    public static String compile(Object object, String secretToken) {
        var encodedData = Base64Encoder.apply(String.valueOf(object));
        var rawToken = secretToken + encodedData + secretToken;
        return SHAEncoder.apply(rawToken);
    }
}
