package org.zero.userservice.mappers;

import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.entity.Counterparty;
import org.zero.userservice.entity.User;
import org.zero.userservice.model.UserData;
import org.zero.userservice.model.UserDataResponse;

public class UserMapper {
    public static UserDataResponse map(ContactPerson contactPerson, String userId) {
        return new UserDataResponse(
                contactPerson.getFirstName(),
                contactPerson.getLastName(),
                contactPerson.getMiddleName(),
                contactPerson.getPhone(),
                userId
        );
    }

    public static ContactPerson map(UserData userData, Counterparty counterparty, User issuer, String hash) {
        var newContactPerson = new ContactPerson();
        newContactPerson.setPhone(userData.phone());
        newContactPerson.setFirstName(userData.firstName());
        newContactPerson.setLastName(userData.lastName());
        newContactPerson.setMiddleName(userData.middleName());
        newContactPerson.setCounterparty(counterparty);
        newContactPerson.setHash(hash);
        newContactPerson.setIssuerUser(issuer);
        return newContactPerson;
    }
}
