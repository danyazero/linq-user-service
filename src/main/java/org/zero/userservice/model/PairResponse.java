package org.zero.userservice.model;

public record PairResponse(
        String userId,
        String warehouseRef,
        String createdUserId
) {
}
