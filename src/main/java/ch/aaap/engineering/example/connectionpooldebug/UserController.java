package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final DocumentService documentService;

    public UserController(UserService userService, DocumentService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @GetMapping("/{name}/document")
    public ResponseEntity<?> findOne(@PathVariable String name) throws InterruptedException {
        Instant start = Instant.now();
        Optional<User> user = userService.findOne(name);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Duration elapsed = Duration.between(start, Instant.now());
        log.info("Found user {} in {}", user.get(), elapsed);


        start = Instant.now();
        String document = documentService.compute(user.get());
        elapsed = Duration.between(start, Instant.now());
        log.info("Rendered document {} in {}", document, elapsed);

        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}