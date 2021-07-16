package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findOne(String name) {
        return userRepository.findByName(name);
    }
}
