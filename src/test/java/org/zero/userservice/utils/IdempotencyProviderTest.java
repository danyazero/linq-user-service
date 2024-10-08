package org.zero.userservice.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.userservice.entity.Idempotency;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.model.UserDataRequest;
import org.zero.userservice.repository.IdempotencyRepository;

import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IdempotencyProviderTest {
    private final String idempotencyKey = "idempotencyKey";
    private UserDataRequest userData;
    @Mock
    private IdempotencyRepository idempotencyRepository;
    @InjectMocks
    private IdempotencyProvider idempotencyProvider;
    @Captor
    private ArgumentCaptor<Idempotency> captor;
    Idempotency idempotency;

    @BeforeEach
    void setUp() {
        userData = new UserDataRequest(idempotencyKey, "Daniil", "Sushko", "Alekseevich", "380960348921", "dksllfkdllskfjglkdsjldfkj");
        idempotency = new Idempotency();
        idempotency.setIdempotencyKey(idempotencyKey);
        idempotency.setIdempotencyHash(userData.hashCode());
    }

    @Test
    void saveWithValues() {
        idempotencyProvider.save(idempotencyKey, userData);
        verify(idempotencyRepository, Mockito.times(1)).save(Mockito.any(Idempotency.class));

        var idempotencyKey = "idempotencyKey-" + null;

        verify(idempotencyRepository).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getIdempotencyKey(), idempotencyKey);
        Assertions.assertEquals(captor.getValue().getIdempotencyHash(), userData.hashCode());
    }

    @Test
    void check_Successful() {
        Mockito.when(idempotencyRepository.findById(idempotencyKey + "-" + null))
                .thenReturn(Optional.of(idempotency));

        var check = idempotencyProvider.check(idempotencyKey, userData).supplyIfExistOrThrow(
                () -> 0,
                () -> {
                    Assertions.fail();
                    return 1;
                }
        );
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals(0, check.get());
    }

    @Test
    void check_NotYetExist() {
        Mockito.when(idempotencyRepository.findById(idempotencyKey + "-" + null))
                .thenReturn(Optional.empty());

        var check = idempotencyProvider.check(idempotencyKey, userData).supplyIfExistOrThrow(
                () -> {
                    Assertions.fail();
                    return 0;
                },
                () -> {
                    Assertions.fail();
                    return 1;
                }
        );
        Assertions.assertTrue(check.isEmpty());
    }

    @Test
    void check_AlreadyExistWithOtherData() {
        idempotency.setIdempotencyHash(userData.hashCode() + 1);
        Mockito.when(idempotencyRepository.findById(idempotencyKey + "-" + null))
                .thenReturn(Optional.of(idempotency));

        var check = idempotencyProvider.check(idempotencyKey, userData).supplyIfExistOrThrow(
                () -> {
                    Assertions.fail();
                    return 0;
                },
                () -> 1
        );
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals(1, check.get());
    }

    @Test
    void check_AlreadyExistButNotFounded() {
        idempotency.setIdempotencyHash(userData.hashCode() + 1);
        Mockito.when(idempotencyRepository.findById(idempotencyKey + "-" + null))
                .thenReturn(Optional.of(idempotency));

        Assertions.assertThrows(RequestException.class,
                () -> idempotencyProvider.check(idempotencyKey, userData).supplyIfExistOrThrow(
                        () -> {
                            Assertions.fail();
                            return 0;
                        },
                        () -> {
                            throw new RequestException("Exception");
                        }
                ));
    }

    @Test
    void delete() {
        Assertions.assertDoesNotThrow(() -> idempotencyProvider.delete(idempotencyKey));
        Mockito.verify(idempotencyRepository, Mockito.times(1)).deleteById(idempotencyKey + "-" + null);
    }

}