package org.zero.userservice.utils;

import org.springframework.stereotype.Component;

@Component
public class RequestMetadataStore {
    private final ThreadLocal<String> requestId = new ThreadLocal<>();

    public String getRequestId() {
        return requestId.get();
    }
    public void setRequestId(String requestId) {
        this.requestId.set(requestId);
    }
}
