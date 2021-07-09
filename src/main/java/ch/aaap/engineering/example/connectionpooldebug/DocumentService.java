package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);


    public String compute(User user) throws InterruptedException {
        // slow render
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        return "Some document";
    }
}
