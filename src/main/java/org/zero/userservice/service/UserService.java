package org.zero.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.entity.Counterparty;
import org.zero.userservice.entity.User;
import org.zero.userservice.exception.AuthException;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.mappers.UserMapper;
import org.zero.userservice.mappers.RegisterMapper;
import org.zero.userservice.model.*;
import org.zero.userservice.repository.ContactPersonRepository;
import org.zero.userservice.repository.CounterpartyRepository;
import org.zero.userservice.repository.UserRepository;
import org.zero.userservice.utils.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CounterpartyRepository counterpartyRepository;
    private final ContactPersonRepository contactPersonRepository;
    private final IdempotencyProvider idempotencyProvider;
    private final UserRepository userRepository;
    private final JWTModule jwtModule;
    private final UUIDProvider uuid;

    public Tokens loginUser(
            AuthRequest authRequest
    ) {
        var passwordHash = SHAEncoder.apply(authRequest.password());
        var user = userRepository.getUserByPhoneAndPasswordIfExists(authRequest.phone(), passwordHash);
        if (user.isEmpty()) throw new AuthException("Incorrect credentials");

        var userId = uuid.generate(user.get().getId());
        var sessionToken = jwtModule.issueSession(userId, false, user.get().getRoles());
        var refreshToken = jwtModule.issueRefresh(userId);

        return new Tokens(sessionToken, refreshToken);
    }

    @Transactional
    public void register(
            RegisterRequest registerRequest
    ) {
        if (!registerRequest.password().equals(registerRequest.passwordRepeat()))
            throw new AuthException("Паролі не співпадають");

        var user = counterpartyRepository.getUserByPhoneIfExists(registerRequest.phone());
        if (user.isPresent()) throw new AuthException("User already registered");

        var counterparty = getCounterparty(registerRequest.phone());

        var createdUser = saveNewUser(registerRequest, counterparty);
        var userData = RegisterMapper.map(registerRequest, createdUser.getId());

        createContactPerson(userData, counterparty);
    }


    public UserDataResponse createContactPerson(
            UserData userData,
            String idempotencyKey
    ) {

        var idempotencyValue = getUserDataIdempotencyValue(userData);
        var contactPerson = idempotencyCheck(userData, idempotencyKey, idempotencyValue);
        if (contactPerson.isPresent()) return contactPerson.get();

        var generatedDataHash = SHAEncoder.apply(idempotencyValue, SHAEncoder.Encryption.SHA1);
        var isAlreadyExist = isContactPersonAlreadyExist(userData, idempotencyKey, generatedDataHash);
        if (isAlreadyExist.isPresent()) return isAlreadyExist.get();

        var counterparty = getCounterparty(userData.phone());
        var createdContactPerson = createContactPerson(userData, counterparty, generatedDataHash);
        idempotencyProvider.save(idempotencyKey, idempotencyValue);

        var userId = uuid.generate(createdContactPerson.getId());
        return UserMapper.map(createdContactPerson, userId);
    }

    public String deleteContactPerson(
            String userId,
            String issuerId
    ) {
        var parsedIssuerId = uuid.get(issuerId);
        var parsedUserId = uuid.get(userId);

        var foundedContactPerson = contactPersonRepository.findFirstByIssuerUser_IdAndAndId(parsedIssuerId, parsedUserId);
        if (foundedContactPerson.isPresent()) {
            contactPersonRepository.delete(foundedContactPerson.get());
            return userId;
        }
        throw new RequestException("Щось пішло не так.");
    }


    private ContactPerson createContactPerson(
            UserData userData,
            Counterparty counterparty
    ) {
        var generatedDataHash = SHAEncoder.apply(getUserDataIdempotencyValue(userData), SHAEncoder.Encryption.SHA1);
        return createContactPerson(userData, counterparty, generatedDataHash);
    }

    private Optional<UserDataResponse> isContactPersonAlreadyExist(
            UserData userData,
            String idempotencyKey,
            String generatedDataHash
    ) {
        var isAlreadyExist = contactPersonRepository.findFirstByIssuerUser_IdAndHash(userData.userId(), generatedDataHash);
        if (isAlreadyExist.isPresent()) {
            idempotencyProvider.saveWithHash(idempotencyKey, generatedDataHash);
            var userId = uuid.generate(isAlreadyExist.get().getId());
            return Optional.of(UserMapper.map(isAlreadyExist.get(), userId));
        }
        return Optional.empty();
    }

    private ContactPerson createContactPerson(
            UserData userData,
            Counterparty counterparty,
            String userDataHash
    ) {
        var issuer = userRepository.getReferenceById(userData.userId());
        var newContactPerson = UserMapper.map(userData, counterparty, issuer, userDataHash);

        return contactPersonRepository.save(newContactPerson);
    }

    private Counterparty getCounterparty(
            String phone
    ) {
        var counterparty = counterpartyRepository.getFirstByPhone(phone);
        return counterparty.orElseGet(() -> createCounterparty(phone));
    }

    private Counterparty createCounterparty(String phone) {
        var newCounterparty = new Counterparty();
        newCounterparty.setPhone(phone);
        return counterpartyRepository.save(newCounterparty);
    }

    private static String getUserDataIdempotencyValue(
            UserData userData
    ) {
        return IdempotencyValueProvider.generate(userData.firstName(), userData.lastName(), userData.middleName(), userData.phone());
    }

    private Optional<UserDataResponse> idempotencyCheck(
            UserData userData,
            String idempotencyKey,
            String idempotencyValue
    ) {
        var idempotency = idempotencyProvider.check(idempotencyKey, idempotencyValue);

        if (idempotency.exist()) {
            if (idempotency.equals()) {
                ContactPerson existedContactPerson = tryGetContactPerson(userData, idempotencyKey);

                var userId = uuid.generate(existedContactPerson.getId());
                return Optional.of(UserMapper.map(existedContactPerson, userId));
            } else throw new RequestException("Особу було створено із іншими даними.");
        }
        return Optional.empty();
    }

    private ContactPerson tryGetContactPerson(
            UserData userData,
            String idempotencyKey
    ) {
        try {
            return contactPersonRepository.getUserByPhoneAndIssuerId(userData.phone(), userData.userId()).getFirst();
        } catch (Exception e) {
            idempotencyProvider.delete(idempotencyKey);
            throw new RequestException("Особу не знайдено. Спробуйте пізніше.");
        }
    }

    private User saveNewUser(RegisterRequest registerRequest, Counterparty counterparty) {
        User newUser = new User();
        newUser.setCounterparty(counterparty);
        newUser.setPassword(SHAEncoder.apply(registerRequest.password()));
        return userRepository.save(newUser);
    }
}
