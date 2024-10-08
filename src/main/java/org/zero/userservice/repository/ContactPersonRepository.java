package org.zero.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.userservice.entity.ContactPerson;

import java.util.List;
import java.util.Optional;

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Integer> {
  Optional<ContactPerson> findFirstByIssuerUser_IdAndPhone(Integer userId, String phone);

  @Query("select CP from ContactPerson CP where CP.id = ?1")
  Optional<ContactPerson> getUserByIdAndIssuerIdfExists(Integer contactPersonId);

  @Query("select CP from ContactPerson CP where CP.counterparty.phone = ?1 and CP.issuerUser.id = ?2")
  List<ContactPerson> getUserByPhoneAndIssuerId(String phone, Integer issuerId);

  @Query("select CP from ContactPerson CP where CP.hash = ?3 and CP.issuerUser.id = ?2 and CP.counterparty.phone = ?1")
  Optional<ContactPerson> getUserByPhoneAndIssuerIdAndHash(String phone, Integer issuerId, Integer hash);

  Optional<ContactPerson> findFirstByIssuerUser_IdAndAndId(Integer issuerUserId, Integer contactPersonId);
}