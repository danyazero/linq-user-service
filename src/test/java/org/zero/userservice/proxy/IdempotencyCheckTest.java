package org.zero.userservice.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.userservice.entity.Idempotency;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.model.UserDataRequest;
import org.zero.userservice.model.UserDataResponse;
import org.zero.userservice.model.IUserService;
import org.zero.userservice.service.UserService;
import org.zero.userservice.utils.ContactPersonExist;
import org.zero.userservice.utils.IdempotencyExist;
import org.zero.userservice.utils.IdempotencyProvider;

@ExtendWith(MockitoExtension.class)
class IdempotencyCheckTest {

    @Mock private ContactPersonExist contactPersonExist;
    @Mock private UserService userService;
    @Mock private IdempotencyProvider idempotencyProvider;

    @InjectMocks
    private IdempotencyCheckProxy idempotencyCheck;

    private final String idempotencyKey = "idempotencyKey";
    private UserDataRequest userData;
    private UserDataResponse userDataResponse;
    Idempotency idempotency;

    @BeforeEach
    void setUp() {
        userData = new UserDataRequest(idempotencyKey, "Daniil", "Sushko", "Alekseevich", "380960348921", "dksllfkdllskfjglkdsjldfkj");
        userDataResponse = new UserDataResponse("Daniil", "Sushko", "Alekseevich", "380960348921", "dksllfkdllskfjglkdsjldfkj");

        idempotencyCheck.next(userService);
        idempotency = new Idempotency();
        idempotency.setIdempotencyKey(idempotencyKey);
        idempotency.setIdempotencyHash(userData.hashCode());
    }

    @Test
    void createContactPerson_AlreadyDidWithThisKey() {
        Mockito.when(idempotencyProvider.check(idempotencyKey, userData))
                .thenReturn(
                        new IdempotencyExist<>(
                                idempotencyKey,
                                idempotencyProvider,
                                new IdempotencyProvider.Idempotency(true, true))
                );
        UserDataResponse contactPerson = idempotencyCheck.createContactPerson(userData);

        Mockito.verify(idempotencyProvider, Mockito.times(1)).check(idempotencyKey, userData);
        Mockito.verify(userService, Mockito.times(0)).createContactPerson(userData);
        Mockito.verify(idempotencyProvider, Mockito.times(0)).delete(idempotencyKey);

        Assertions.assertNotNull(contactPerson);
        Assertions.assertEquals(contactPerson, userDataResponse);
    }

    @Test
    void createContactPerson_firstTimeWithThisKey() {
        Mockito.when(idempotencyProvider.check(idempotencyKey, userData))
                .thenReturn(
                        new IdempotencyExist<>(
                                idempotencyKey,
                                idempotencyProvider,
                                new IdempotencyProvider.Idempotency(false, false))
                );
        Mockito.when(userService.createContactPerson(userData)).thenReturn(userDataResponse);
        UserDataResponse contactPerson = idempotencyCheck.createContactPerson(userData);

        Mockito.verify(userService, Mockito.times(1)).createContactPerson(userData);
        Mockito.verify(idempotencyProvider, Mockito.times(1)).check(idempotencyKey, userData);
        Mockito.verify(idempotencyProvider, Mockito.times(0)).delete(idempotencyKey);

        Assertions.assertNotNull(contactPerson);
        Assertions.assertEquals(contactPerson, userDataResponse);
    }

    @Test
    void createContactPerson_AlreadyDidButCannotFind() {
        Mockito.when(idempotencyProvider.check(idempotencyKey, userData))
                .thenReturn(
                        new IdempotencyExist<>(
                                idempotencyKey,
                                idempotencyProvider,
                                new IdempotencyProvider.Idempotency(true, false))
                );
        Mockito.when(contactPersonExist.apply(userData)).thenThrow(RequestException.class);
        Assertions.assertThrows(RequestException.class, () -> idempotencyCheck.createContactPerson(userData));

        Mockito.verify(idempotencyProvider, Mockito.times(1)).check(idempotencyKey, userData);
        Mockito.verify(userService, Mockito.times(0)).createContactPerson(userData);
        Mockito.verify(idempotencyProvider, Mockito.times(1)).delete(idempotencyKey);

    }
}