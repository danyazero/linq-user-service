package org.zero.userservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash(value = "Idempotency", timeToLive = 900)
public class Idempotency implements Serializable {
    @Id
    private String idempotencyKey;
    private String idempotencyHash;
}
