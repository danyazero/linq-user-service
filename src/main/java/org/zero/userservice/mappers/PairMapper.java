package org.zero.userservice.mappers;

import org.zero.userservice.model.FullPairRequest;
import org.zero.userservice.model.UserData;

public class PairMapper {
    public static UserData map(FullPairRequest pair, Integer userId) {
        return new UserData(pair.contactPerson().firstName(), pair.contactPerson().lastName(), pair.contactPerson().middleName(), pair.contactPerson().phone(), userId);
    }

    public static String getUserId(FullPairRequest pair) {
        return pair.contactPerson().userId();
    }
}
