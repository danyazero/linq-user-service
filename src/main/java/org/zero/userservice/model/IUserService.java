package org.zero.userservice.model;

public interface IUserService extends Chain {

    Tokens loginUser(AuthRequest authRequest);

    void registerUser(RegisterRequest registerRequest);

    UserDataResponse createContactPerson(UserDataRequest userData);

    String deleteContactPerson(String userId, String issuerId);
}
