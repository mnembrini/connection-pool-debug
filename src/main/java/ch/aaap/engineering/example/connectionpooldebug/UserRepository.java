package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByName(String name);
}
