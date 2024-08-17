package org.zero.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.userservice.entity.Counterparty;

import java.util.Optional;

public interface CounterpartyRepository extends JpaRepository<Counterparty, Integer> {
  Optional<Counterparty> getFirstByPhone(String phone);
  @Query("select C from Counterparty C, User U where C.phone = ?1 and U.counterparty.id = C.id")
  Optional<Counterparty> getUserByPhoneIfExists(String phone);
}