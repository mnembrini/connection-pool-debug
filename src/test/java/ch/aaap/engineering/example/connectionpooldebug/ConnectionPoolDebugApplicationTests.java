package ch.aaap.engineering.example.connectionpooldebug;

import ch.aaap.engineering.example.connectionpooldebug.domain.User;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerIntegrationTest.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setName("root");
        userRepository.save(user);
    }

    @Test
    void singleRequest() throws Exception {
        callUserAndAssert();
    }

    @Test
    void multipleRequests() throws Exception {
        int requests = 10;
        int testTimeoutSeconds = 20;
        // enough threads to run in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(requests);

        List<Callable<Void>> tasks = IntStream.range(0, requests)
                .mapToObj(this::createTask)
                .collect(Collectors.toList());
        List<Future<Void>> futures = executorService.invokeAll(tasks);

        executorService.shutdown();
        boolean termination = executorService.awaitTermination(testTimeoutSeconds, TimeUnit.SECONDS);
        Assertions.assertTrue(termination, "Executor did not terminate properly");

        for (Future<Void> future : futures) {
            Assertions.assertDoesNotThrow(() -> future.get());
        }
    }

    private void callUserAndAssert() throws Exception {
        mockMvc.perform(get("/users/root/document"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("root"));
    }

    Callable<Void> createTask(int i) {
        return () -> {
            log.info("Running task {}", i);
            callUserAndAssert();
            return null;
        };
    }
}