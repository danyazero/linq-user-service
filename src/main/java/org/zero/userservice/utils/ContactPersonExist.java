package org.zero.userservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.mappers.UserMapper;
import org.zero.userservice.model.UserDataRequest;
import org.zero.userservice.model.UserDataResponse;
import org.zero.userservice.repository.ContactPersonRepository;

import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactPersonExist implements Function<UserDataRequest, UserDataResponse> {
    private final UUIDProvider uuid;
    private final ContactPersonRepository contactPersonRepository;

    @Override
    public UserDataResponse apply(UserDataRequest userDataRequest) {

        log.info("Searching for contact person in database");
        ContactPerson existedContactPerson = contactPersonRepository
                .getUserByPhoneAndIssuerId(
                        userDataRequest.phone(),
                        uuid.get(userDataRequest.userId())
                ).getFirst();
        log.info("Found contact person -> {}", existedContactPerson.toString());

        var userId = uuid.generate(existedContactPerson.getId());
        log.info("For found contact person data in database generated id -> {}", userId);
        return UserMapper.map(existedContactPerson, userId);
    }
}
