package com.thomaskasene.springdatajpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.PostgreSQLContainer.IMAGE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostgresqlParameterBindingTest {

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse(IMAGE).withTag("12.11"));
        POSTGRESQL_CONTAINER.start();

        System.setProperty("spring.datasource.url", String.format("jdbc:postgresql://%s:%d/%s",
                POSTGRESQL_CONTAINER.getHost(),
                POSTGRESQL_CONTAINER.getFirstMappedPort(),
                POSTGRESQL_CONTAINER.getDatabaseName()));
        System.setProperty("spring.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRESQL_CONTAINER.getPassword());
    }

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpTest() {
        petRepository.deleteAll();
    }

    // Succeeds.
    @Test
    void jdbcTemplate() {
        Pet.Tags petTagsPojo = new Pet.Tags()
                .setTags(List.of("1111", "2222", "3333"));

        petRepository.save(new Pet().setTags(petTagsPojo));

        assertThat(jdbcTemplate.queryForList("select * from PET where TAGS_JSON -> 'tags' ?? '2222'"))
                .singleElement()
                .satisfies(pet -> assertThat(pet.get("TAGS_JSON").toString())
                        .isEqualTo("{\"tags\": [\"1111\", \"2222\", \"3333\"]}"));
    }

    // Fails with: IllegalStateException: Required name for ParameterBinding [name: null, position: 1, expression: null] not available!
    @Test
    void jpaNativeQuery() {
        Pet.Tags petTagsPojo = new Pet.Tags()
                .setTags(List.of("1111", "2222", "3333"));

        petRepository.save(new Pet().setTags(petTagsPojo));

        assertThat(petRepository.findByTag("2222"))
                .isNotNull()
                .satisfies(pet -> assertThat(pet.getTags()).isEqualTo(petTagsPojo));
    }

    // Fails with: IllegalArgumentException: At least 1 parameter(s) provided but only 0 parameter(s) present in query.
    @Test
    void jpaNativeQueryHardCoded() {
        Pet.Tags petTagsPojo = new Pet.Tags()
                .setTags(List.of("1111", "2222", "3333"));

        petRepository.save(new Pet().setTags(petTagsPojo));

        assertThat(petRepository.findByTagEquals2222())
                .isNotNull()
                .satisfies(pet -> assertThat(pet.getTags()).isEqualTo(petTagsPojo));
    }
}
