package com.aerobook.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic Spring Boot context loading test. Disables Eureka discovery client and
 * external config server during testing.
 */
@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.cloud.discovery.enabled=false"
})
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
