package org.zero.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.entity.Counterparty;
import org.zero.userservice.entity.User;
import org.zero.userservice.exception.AuthException;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.mappers.RegisterMapper;
import org.zero.userservice.mappers.UserMapper;
import org.zero.userservice.model.*;
import org.zero.userservice.repository.ContactPersonRepository;
import org.zero.userservice.repository.CounterpartyRepository;
import org.zero.userservice.repository.UserRepository;
import org.zero.userservice.utils.JWTModule;
import org.zero.userservice.utils.RequestMetadataStore;
import org.zero.userservice.utils.SHAEncoder;
import org.zero.userservice.utils.UUIDProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final RequestMetadataStore requestMetadataStore;
    private final ContactPersonRepository contactPersonRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final UserRepository userRepository;
    private final JWTModule jwtModule;
    private final UUIDProvider uuid;

    public Tokens loginUser(AuthRequest authRequest) {
        var passwordHash = SHAEncoder.apply(authRequest.password());
        var user = userRepository.getUserByPhoneAndPasswordIfExists(authRequest.phone(), passwordHash);
        if (user.isEmpty()) throw new AuthException("Incorrect credentials");

        var userId = uuid.generate(user.get().getId());
        var sessionToken = jwtModule.issueSession(userId, false, user.get().getRoles());
        var refreshToken = jwtModule.issueRefresh(userId);

        return new Tokens(sessionToken, refreshToken);
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.password().equals(registerRequest.passwordRepeat()))
            throw new AuthException("Паролі не співпадають");

        var user = counterpartyRepository.getUserByPhoneIfExists(registerRequest.phone());
        if (user.isPresent()) throw new AuthException("User already registered");

        var counterparty = getCounterparty(registerRequest.phone());

        var createdUser = saveNewUser(registerRequest, counterparty);
        var userData = RegisterMapper.map(registerRequest, createdUser.getId());

        createContactPerson(userData, counterparty);
    }

    public UserDataResponse createContactPerson(UserDataRequest userDataRequest) {
        System.out.println(Thread.currentThread().getName());

        var counterparty = getCounterparty(userDataRequest.phone());
        var userDataId = uuid.get(userDataRequest.userId());
        log.info("({}) Extracted issuer userId -> {}", requestMetadataStore.getRequestId(), userDataId);
        var userData = UserMapper.map(userDataRequest, userDataId);
        var createdContactPerson = createContactPerson(
                userData,
                counterparty,
                userDataRequest.hashCode()
        );
        var userId = uuid.generate(createdContactPerson.getId());

        return UserMapper.map(createdContactPerson, userId);
    }


    public String deleteContactPerson(String userId, String issuerId) {
        var parsedIssuerId = uuid.get(issuerId);
        var parsedUserId = uuid.get(userId);
        log.info("({}) Parsed userId -> {}, issuerId -> {}", requestMetadataStore.getRequestId(), parsedUserId, parsedIssuerId);

        var foundedContactPerson = contactPersonRepository.findFirstByIssuerUser_IdAndAndId(parsedIssuerId, parsedUserId);
        if (foundedContactPerson.isPresent()) {
            log.info("({}) Contact person founded, removing it", requestMetadataStore.getRequestId());
            contactPersonRepository.delete(foundedContactPerson.get());
            return userId;
        }
        log.info("({}) Contact person not found, couldn't removing it", requestMetadataStore.getRequestId());
        throw new RequestException("Щось пішло не так.");
    }

    private void createContactPerson(
            UserData userData,
            Counterparty counterparty
    ) {
        createContactPerson(userData, counterparty, userData.hashCode());
    }

    private ContactPerson createContactPerson(UserData userData, Counterparty counterparty, Integer userDataHash) {
        var createdContactPerson = contactPersonRepository.getUserByPhoneAndIssuerIdAndHash(
                userData.phone(),
                counterparty.getId(),
                userDataHash
        );
        if (createdContactPerson.isPresent()) {

            log.info("Contact person for this issuer with similar data already exists -> {}", createdContactPerson.get().getId());
            return createdContactPerson.get();
        }

        var issuer = userRepository.getReferenceById(userData.userId());
        var newContactPerson = UserMapper.map(
                userData,
                counterparty,
                issuer,
                userDataHash
        );
        log.info("Contact person for this issuer was created");

        return contactPersonRepository.save(newContactPerson);
    }

    private Counterparty getCounterparty(String phone) {
        var counterparty = counterpartyRepository.getFirstByPhone(phone);

        return counterparty.orElseGet(() -> createCounterparty(phone));
    }

    private Counterparty createCounterparty(String phone) {
        var newCounterparty = new Counterparty();
        newCounterparty.setPhone(phone);
        return counterpartyRepository.save(newCounterparty);
    }

    private User saveNewUser(RegisterRequest registerRequest, Counterparty counterparty) {
        User newUser = new User();
        newUser.setCounterparty(counterparty);
        newUser.setPassword(SHAEncoder.apply(registerRequest.password()));

        return userRepository.save(newUser);
    }

    private static void method(String message) {
        System.out.println(message);
    }

    @Override
    public Chain next(Chain chain) {
        return this;
    }
}
