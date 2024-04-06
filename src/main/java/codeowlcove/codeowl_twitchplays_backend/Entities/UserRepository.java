package codeowlcove.codeowl_twitchplays_backend.Entities;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserRepository extends CrudRepository<UserDBEntity, String> {
    UserDBEntity findByUsername(String username);

    Collection<UserDBEntity> findAll(Sort points);
}
