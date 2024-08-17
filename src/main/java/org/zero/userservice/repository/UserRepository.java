package org.zero.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.userservice.entity.Counterparty;
import org.zero.userservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
  @Query("select U from Counterparty C, User U where C.phone = ?1 and U.counterparty.id = C.id and U.password = ?2")
  Optional<User> getUserByPhoneAndPasswordIfExists(String phone, String password);
}