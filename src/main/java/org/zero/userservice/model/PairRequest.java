package org.zero.userservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PairRequest(
//        @JsonProperty("_IK")
//        String idempotencyKey,
        String userId,
        String warehouseRef
) {
}
