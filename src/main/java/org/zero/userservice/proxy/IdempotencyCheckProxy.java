package org.zero.userservice.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.zero.userservice.mappers.UserMapper;
import org.zero.userservice.model.*;
import org.zero.userservice.utils.ContactPersonExist;
import org.zero.userservice.utils.IdempotencyProvider;
import org.zero.userservice.utils.RequestMetadataStore;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class IdempotencyCheckProxy implements IUserService {
    private IUserService userService;
    private final ContactPersonExist contactPersonExist;
    private final IdempotencyProvider idempotencyProvider;
    private final RequestMetadataStore requestMetadataStore;

    @Override
    public Tokens loginUser(AuthRequest authRequest) {
        return userService.loginUser(authRequest);
    }

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
    }

    @Override
    public UserDataResponse createContactPerson(UserDataRequest userData) {
        System.out.println(Thread.currentThread().getName());
        return (UserDataResponse) idempotencyProvider
                .check(requestMetadataStore.getRequestId(), userData)
                .supplyIfExistOrThrow(
                        () -> UserMapper.map(userData),
                        () -> contactPersonExist.apply(userData)
                ).orElseGet(() -> {
                    log.info("It`s first request for idempotency key -> {}", requestMetadataStore.getRequestId());
                    UserDataResponse contactPerson = userService.createContactPerson(userData);
                    idempotencyProvider.save(requestMetadataStore.getRequestId(), userData);
                    return contactPerson;
                });
    }

    @Override
    public String deleteContactPerson(String userId, String issuerId) {
        return userService.deleteContactPerson(userId, issuerId);
    }

    @Override
    public Chain next(Chain chain) {
        log.info("IdempotencyCheckProxy.next -> {}", chain.getClass().getSimpleName());
        if (chain instanceof IUserService _userService) this.userService = _userService;
        return this;
    }
}
