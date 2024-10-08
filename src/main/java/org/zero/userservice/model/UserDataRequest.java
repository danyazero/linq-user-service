package org.zero.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDataRequest(
/*
        @JsonProperty("_IK")
        String idempotencyKey,
*/
        String firstName,
        String lastName,
        String middleName,
        String phone,
        String userId
) {
        @Override
        public int hashCode() {
                return Objects.hash(firstName, lastName, middleName, phone, userId);
        }
}
