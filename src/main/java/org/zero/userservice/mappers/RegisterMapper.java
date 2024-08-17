package org.zero.userservice.mappers;

import org.zero.userservice.model.RegisterRequest;
import org.zero.userservice.model.UserData;

public class RegisterMapper {

    public static UserData map(RegisterRequest registerRequest, Integer userId) {
        return new UserData(
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.middleName(),
                registerRequest.phone(),
                userId
        );
    }
}
