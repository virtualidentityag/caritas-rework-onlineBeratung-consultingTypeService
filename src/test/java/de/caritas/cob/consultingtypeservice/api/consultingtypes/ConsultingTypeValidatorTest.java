package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_BROKEN_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_CONSULTING_TYPE_JSON_SCHEMA;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_INVALID_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_NULL_VALUE_CONSULTING_TYPE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
import java.io.File;
import java.util.Objects;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class ConsultingTypeValidatorTest {

  private static final String FIELD_NAME_CONSULTING_TYPE_JSON_SCHEMA_FILE =
      "consultingTypeJsonSchemaFile";

  private ConsultingTypeValidator consultingTypeValidator;

  @Mock private Logger logger;

  @BeforeEach
  void setup() {
    consultingTypeValidator = new ConsultingTypeValidator();
    setField(
        consultingTypeValidator,
        FIELD_NAME_CONSULTING_TYPE_JSON_SCHEMA_FILE,
        FILE_CONSULTING_TYPE_JSON_SCHEMA);
  }

  @Test
  void
      validateConsultingTypeConfigurationJsonFile_Should_ThrowUnexpectedErrorException_WhenJsonViolatesSchema() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_INVALID_CONSULTING_TYPE))
                .getFile());

    assertThrows(
        UnexpectedErrorException.class,
        () ->
            consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
                invalidConsultingSettingsFile));
  }

  @Test
  void
      validateConsultingTypeConfigurationJsonFile_Should_ThrowUnexpectedErrorException_WhenJsonFileCouldNotBeParsed() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_BROKEN_CONSULTING_TYPE))
                .getFile());

    assertThrows(
        UnexpectedErrorException.class,
        () ->
            consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
                invalidConsultingSettingsFile));
  }

  @Test
  void validateConsultingTypeConfigurationJsonFile_ShouldNot_ThrowException_For_NullValues() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_NULL_VALUE_CONSULTING_TYPE))
                .getFile());

    try {
      consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
          invalidConsultingSettingsFile);
    } catch (Exception e) {
      Fail.fail("No exception should be thrown");
    }
  }
}
