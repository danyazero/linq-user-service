package org.zero.userservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDataRequest(
        @JsonProperty("_IK")
        String idempotencyKey,
        String firstName,
        String lastName,
        String middleName,
        String phone
//        String userId
) { }
