package uk.co.mruoc.cws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.AttemptEntity;

import java.util.UUID;

@Repository
public interface PostgresJpaAttemptRepository extends CrudRepository<AttemptEntity, UUID> {}
