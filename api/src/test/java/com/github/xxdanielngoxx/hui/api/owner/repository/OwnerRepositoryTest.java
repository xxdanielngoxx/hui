package com.github.xxdanielngoxx.hui.api.owner.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.xxdanielngoxx.hui.api.owner.model.OwnerEntity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
/*
 * TODO: Revert when setup CD is done
 * @Import({PostgresTestcontainerConfiguration.class})
 */
@Profile("test") // TODO: Remove when setup CD is done
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OwnerRepositoryTest {

  @Autowired private OwnerRepository ownerRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setup() {
    cleanupDatabase();
  }

  void cleanupDatabase() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "owner");
  }

  @Nested
  class SaveTest {
    @Test
    void given_owner_entity_with_full_fields_when_saving_then_successful() {
      final OwnerEntity owner =
          OwnerEntity.builder()
              .fullName("Ngô Đình Lộc")
              .phoneNumber("0393238017")
              .password("super_secret?#")
              .email("danielngo1998@gmail.com")
              .build();

      final UUID ownerId = ownerRepository.save(owner).getId();

      final OwnerEntity savedOwner = ownerRepository.findById(ownerId).orElseThrow();

      assertThat(savedOwner.getFullName()).isEqualTo(owner.getFullName());
      assertThat(savedOwner.getPhoneNumber()).isEqualTo(owner.getPhoneNumber());
      assertThat(savedOwner.getPassword()).isEqualTo(owner.getPassword());
      assertThat(savedOwner.getEmail()).isEqualTo(owner.getEmail());
    }

    @Test
    void
        given_owner_entity_with_phone_number_already_registered_by_other_when_saving_then_failed() {
      final OwnerEntity owner =
          OwnerEntity.builder()
              .fullName("Ngô Đình Lộc")
              .phoneNumber("0393238017")
              .password("super_secret?#")
              .email("danielngo1998@gmail.com")
              .build();

      ownerRepository.save(owner);

      final OwnerEntity invalidOwner =
          OwnerEntity.builder()
              .fullName("Invalid User")
              .password("super_secret?#")
              .phoneNumber(owner.getPhoneNumber())
              .email("john.doe@gmail.com")
              .build();

      final DataIntegrityViolationException exception =
          assertThrows(
              DataIntegrityViolationException.class, () -> ownerRepository.save(invalidOwner));

      /*
       * TODO: Revert when setup CD is done
       *
       * final String expectedErrorMessage =
       *    String.format("(phone_number)=(%s) already exists", invalidOwner.getPhoneNumber());
       * assertThat(exception.getMessage()).contains(expectedErrorMessage);
       *
       * */
    }

    @Test
    void given_owner_entity_with_email_already_registered_by_other_when_saving_then_failed() {
      final OwnerEntity owner =
          OwnerEntity.builder()
              .fullName("Ngô Đình Lộc")
              .phoneNumber("0393238017")
              .password("super_secret?#")
              .email("danielngo1998@gmail.com")
              .build();

      ownerRepository.save(owner);

      final OwnerEntity invalidOwner =
          OwnerEntity.builder()
              .fullName("Invalid User")
              .password("super_secret?#")
              .phoneNumber("0393238018")
              .email(owner.getEmail())
              .build();

      final DataIntegrityViolationException exception =
          assertThrows(
              DataIntegrityViolationException.class, () -> ownerRepository.save(invalidOwner));

      /*
       * TODO: Revert when setup CD is done
       *
       * final String expectedErrorMessage =
       *   String.format("(email)=(%s) already exists", invalidOwner.getEmail());
       * assertThat(exception.getMessage()).contains(expectedErrorMessage);
       *
       * */
    }
  }

  @Nested
  class ExistsByPhoneNumberTest {

    @Test
    void should_return_true_when_phone_number_was_used_by_an_owner() {
      final String phoneNumber = "0393238017";

      final OwnerEntity owner =
          OwnerEntity.builder()
              .fullName("Ngô Đình Lộc")
              .phoneNumber("0393238017")
              .password("super_secret?#")
              .email("danielngo1998@gmail.com")
              .build();

      ownerRepository.save(owner);

      final boolean result = ownerRepository.existsByPhoneNumber(phoneNumber);
      assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_phone_number_is_not_used_by_any_owner() {
      final String phoneNumber = "0393238017";

      final boolean result = ownerRepository.existsByPhoneNumber(phoneNumber);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class ExistsByEmailTest {

    @Test
    void should_return_true_when_email_was_already_used_by_an_owner() {
      final String email = "danielngo1998@gmail.com";

      final OwnerEntity owner =
          OwnerEntity.builder()
              .fullName("Ngô Đình Lộc")
              .phoneNumber("0393238017")
              .password("super_secret?#")
              .email(email)
              .build();

      ownerRepository.save(owner);

      final boolean result = ownerRepository.existsByEmail(email);
      assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_email_is_not_used_by_any_owner() {
      final String email = "danielngo1998@gmail.com";

      final boolean result = ownerRepository.existsByEmail(email);

      assertThat(result).isFalse();
    }
  }
}
