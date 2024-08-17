package org.zero.userservice.model;

public record UserDataResponse(
        String firstName,
        String lastName,
        String middleName,
        String phone,
        String userId
) {}