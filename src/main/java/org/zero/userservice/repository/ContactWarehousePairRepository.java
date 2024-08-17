package org.zero.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zero.userservice.entity.ContactWarehousePair;

import java.util.Optional;

public interface ContactWarehousePairRepository extends JpaRepository<ContactWarehousePair, Integer> {
    Optional<ContactWarehousePair> findFirstByContactPerson_IdAndWarehouseRef(Integer contactPerson, String warehouseRef);
}