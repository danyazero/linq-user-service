package org.zero.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.entity.ContactWarehousePair;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.mappers.PairMapper;
import org.zero.userservice.model.FullPairRequest;
import org.zero.userservice.model.PairRequest;
import org.zero.userservice.model.PairResponse;
import org.zero.userservice.model.IUserService;
import org.zero.userservice.repository.ContactPersonRepository;
import org.zero.userservice.repository.ContactWarehousePairRepository;
import org.zero.userservice.utils.IdempotencyProvider;
import org.zero.userservice.utils.UUIDProvider;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PairService {
    @Qualifier("idempotencyCheckProxy")
    private final IUserService userService;
    private final UUIDProvider uuidProvider;
    private final IdempotencyProvider idempotencyProvider;
    private final ContactWarehousePairRepository repository;
    private final ContactPersonRepository contactPersonRepository;

    public String createPair(PairRequest pair, String idempotencyKey, String issuerId) {
        var contactPersonId = uuidProvider.get(pair.userId());

/*
    if (idempotencyCheckResult.exist()) {
      if (idempotencyCheckResult.equals()) {
        var foundedPair =
            repository.findFirstByContactPerson_IdAndWarehouseRef(
                contactPersonId, pair.warehouseRef());
        if (foundedPair.isPresent()) return uuidProvider.generate(foundedPair.get().getId());

        idempotencyProvider.delete(idempotencyKey);
        throw new RequestException("Конфігурацію не знайдено. Спробуйте пізніше.");
      } else throw new RequestException("Конфігурацію вже було створено із іншими даними.");
    }
    checkIssuerPermissionToCreatePair(issuerId, contactPersonId);

    var pairHash = SHAEncoder.apply(idempotencyCheckValue, SHAEncoder.Encryption.SHA1);
*/
        var foundedPair =
                repository.findFirstByContactPerson_IdAndWarehouseRef(contactPersonId, pair.warehouseRef());

/*
    if (isExistAndHashEquals(foundedPair, pairHash)) {
      idempotencyProvider.save(idempotencyKey, idempotencyCheckValue);
      return uuidProvider.generate(foundedPair.get().getId());
    }
*/

        var newPair = getContactWarehousePair(pair, contactPersonId);
        return savePair(idempotencyKey, newPair);
    }

    public PairResponse createContactPersonAndGetPair(
            FullPairRequest pair, String idempotencyKey, String issuerId) {
        var foundedPair = getPairOrThrow(pair);

        var parsedIssuerId = uuidProvider.get(issuerId);
        if (isContactPersonIssuerAndIssuerEquals(foundedPair, parsedIssuerId))
            throw new RequestException("Не має доступу до конфігурації.");

        var contactPersonId = uuidProvider.generate(foundedPair.getContactPersonId());
        var warehouseRef = foundedPair.getWarehouseRef();

        var userId = uuidProvider.get(PairMapper.getUserId(pair));
        var contactPerson = PairMapper.map(pair, userId);
//        var createdContactPerson = userService.createContactPerson(contactPerson);
//        var createdContactPerson = null;
        //TODO add create contact person method and it id return

        return new PairResponse(contactPersonId, warehouseRef, "");
    }

    private String savePair(
            String idempotencyKey, ContactWarehousePair newPair) {
        var savedPair = repository.save(newPair);
//    idempotencyProvider.save(idempotencyKey);
        return uuidProvider.generate(savedPair.getId());
    }

    private ContactWarehousePair getContactWarehousePair(
            PairRequest pair, Integer contactPersonId) {
        var newPair = new ContactWarehousePair();
        newPair.setContactPerson(contactPersonRepository.getReferenceById(contactPersonId));
        newPair.setWarehouseRef(pair.warehouseRef());
        return newPair;
    }

    private void checkIssuerPermissionToCreatePair(String issuerId, Integer contactPersonId) {
        var parsedIssuerId = uuidProvider.get(issuerId);
        var contactPerson = contactPersonRepository.findById(contactPersonId);
        if (contactPerson.isPresent()) {
            if (isContactPersonIssuerAndIssuerEquals(contactPerson, parsedIssuerId))
                throw new RequestException("У вас немає дозволу на це.");
        } else throw new RequestException("Такої контактної особи не існує.");
    }

    private ContactWarehousePair getPairOrThrow(FullPairRequest pair) {
        var pairId = uuidProvider.get(pair.pairId());
        var foundedPair = repository.findById(pairId);
        if (foundedPair.isEmpty()) throw new RequestException("Такої конфігурації не існує.");
        return foundedPair.get();
    }

    private static boolean isContactPersonIssuerAndIssuerEquals(
            Optional<ContactPerson> contactPerson, Integer parsedIssuerId) {
        return !Objects.equals(contactPerson.get().getIssuerUser().getId(), parsedIssuerId);
    }

    private static boolean isContactPersonIssuerAndIssuerEquals(
            ContactWarehousePair foundedPair, Integer parsedIssuerId) {
        return !Objects.equals(foundedPair.getIssuerId(), parsedIssuerId);
    }

    private static boolean isExistAndHashEquals(
            Optional<ContactWarehousePair> foundedPair, String pairHash) {
        return foundedPair.isPresent() && foundedPair.get().getHash().equals(pairHash);
    }
}
