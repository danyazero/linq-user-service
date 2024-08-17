package org.zero.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.userservice.entity.ContactPerson;
import org.zero.userservice.entity.Counterparty;

import java.util.List;
import java.util.Optional;

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Integer> {
  Optional<ContactPerson> findFirstByIssuerUser_IdAndPhone(Integer userId, String phone);

  @Query("select CP from ContactPerson CP where CP.id = ?1")
  Optional<ContactPerson> getUserByIdAndIssuerIdfExists(Integer contactPersonId);

  @Query("select CP from ContactPerson CP where CP.counterparty.phone = ?1 and CP.issuerUser.id = ?2")
  List<ContactPerson> getUserByPhoneAndIssuerId(String phone, Integer issuerId);

  Optional<ContactPerson> findFirstByIssuerUser_IdAndAndId(Integer issuerUserId, Integer contactPersonId);
  Optional<ContactPerson> findFirstByIssuerUser_IdAndHash(Integer userId, String hash);
}