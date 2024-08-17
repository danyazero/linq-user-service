package org.zero.userservice.model;

public record UserData(
        String firstName,
        String lastName,
        String middleName,
        String phone,
        Integer userId
) {
}
