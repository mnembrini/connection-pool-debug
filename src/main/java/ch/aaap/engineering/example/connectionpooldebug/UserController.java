package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.Session;
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
    private final EntityManager entityManager;

    public UserController(UserService userService, DocumentService documentService, EntityManager entityManager) {
        this.userService = userService;
        this.documentService = documentService;
        this.entityManager = entityManager;
    }

    @GetMapping("/{name}/document")
    public ResponseEntity<?> findOne(@PathVariable String name) throws InterruptedException {
        log.debug("Looking for user [{}]", name);
        Optional<User> user = userService.findOne(name);
        if (user.isEmpty()) {
            log.info("User not found");
            return ResponseEntity.notFound().build();
        }
        log.debug("Found user {}", user.get());

        ((Session) entityManager.getDelegate()).close();


        String document = documentService.renderDocument(user.get());
        log.debug("Rendered document [{}]", document);

        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}