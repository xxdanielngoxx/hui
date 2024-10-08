package com.github.xxdanielngoxx.hui.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({PostgresTestcontainerConfiguration.class})
@SpringBootTest
class ApiApplicationTests {

  @Test
  void contextLoads() {
    // Load the context to ensure the application can start without any errors
  }
}
