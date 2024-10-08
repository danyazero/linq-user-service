package org.zero.userservice.model;

import java.util.Objects;

public record UserData(
        String firstName,
        String lastName,
        String middleName,
        String phone,
        Integer userId
) {
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, middleName, phone, userId);
    }
}
