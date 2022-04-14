package org.titanic.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */

@Slf4j
@EnableJpaRepositories("org.titanic.db.repository")
@EntityScan("org.titanic.db.entity")
@SpringBootApplication(scanBasePackages = "org.titanic")
public class TitanicApplication {
  public static void main(String[] args) {
      SpringApplication.run(TitanicApplication.class, args);
  }
}
