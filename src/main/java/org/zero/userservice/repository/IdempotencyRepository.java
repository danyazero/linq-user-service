package org.zero.userservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.zero.userservice.entity.Idempotency;

@Repository
public interface IdempotencyRepository extends CrudRepository<Idempotency, String> {

}
