package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataMongoTest()
@ContextConfiguration(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=true")
@TestPropertySource(
    properties =
        "consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific")
public class ConsultingTypeMongoTenantAwareRespositoryIT {

  private static final long FIRST_TENANT = 1L;
  private static final long SECOND_TENANT = 2L;
  private final String MONGO_COLLECTION_NAME = "consulting_types";

  @Autowired private ConsultingTypeTenantAwareRepository consultingTypeMongoTenantAwareRepository;

  @Autowired MongoTemplate mongoTemplate;

  @BeforeEach
  public void initializeMongoDbWithData() throws IOException {
    mongoTemplate.dropCollection(MONGO_COLLECTION_NAME);
    insertJsonFromFilename("consulting-type-0.json");
    insertJsonFromFilename("consulting-type-1.json");
    insertJsonFromFilename("consulting-type-2.json");
  }

  private void insertJsonFromFilename(String fileName) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    ConsultingType consultingType =
        objectMapper.readValue(
            new ClassPathResource("consulting-type-settings-tenant-specific/" + fileName).getFile(),
            new TypeReference<>() {});
    mongoTemplate.insert(consultingType, MONGO_COLLECTION_NAME);
  }

  @Test
  public void findConsultingTypeByIdAndTenantId_Should_ReturnCorrectConsultingType() {
    // given
    Integer consultingTypeId = 10;
    String slug = "consultingtype10";

    // when
    ConsultingType result =
        consultingTypeMongoTenantAwareRepository.findConsultingTypeByIdAndTenantId(
            consultingTypeId, FIRST_TENANT);

    ConsultingType resultForAnotherTenant =
        consultingTypeMongoTenantAwareRepository.findConsultingTypeByIdAndTenantId(
            consultingTypeId, SECOND_TENANT);

    // then
    assertThat(consultingTypeId).isEqualTo(result.getId());
    assertThat(slug).isEqualTo(result.getSlug());
    assertThat(resultForAnotherTenant).isNull();
  }

  @Test
  public void findConsultingTypeByTenantId_Should_ReturnCorrectConsultingType() {
    // given
    Integer consultingTypeId = 10;
    String slug = "consultingtype10";

    // when
    ConsultingTypeEntity result =
        consultingTypeMongoTenantAwareRepository.findByTenantId((int) FIRST_TENANT);
    ConsultingTypeEntity resultForAnotherTenant =
        consultingTypeMongoTenantAwareRepository.findByTenantId(2);
    // then
    assertThat(consultingTypeId).isEqualTo(result.getId());
    assertThat(slug).isEqualTo(result.getSlug());
    assertThat(result.getTenantId()).isEqualTo(1);

    assertThat(consultingTypeId).isNotEqualTo(resultForAnotherTenant.getId());
    assertThat(resultForAnotherTenant.getTenantId()).isEqualTo(2);
  }

  @Test
  public void findByConsultingTypeId_Should_ReturnCorrectConsultingType() {
    // given
    Integer consultingTypeId = 10;
    String slug = "consultingtype10";

    // when
    ConsultingType result =
        consultingTypeMongoTenantAwareRepository.findByConsultingTypeId(consultingTypeId);

    // then
    assertThat(consultingTypeId).isEqualTo(result.getId());
    assertThat(slug).isEqualTo(result.getSlug());
  }

  @Test
  public void findBySlugAndTenantId_Should_ReturnCorrectConsultingType() {
    // given
    Integer consultingTypeId = 10;
    String slug = "consultingtype10";

    // when
    List<ConsultingTypeEntity> result =
        consultingTypeMongoTenantAwareRepository.findBySlugAndTenantId(slug, FIRST_TENANT);

    List<ConsultingTypeEntity> resultForAnotherTenant =
        consultingTypeMongoTenantAwareRepository.findBySlugAndTenantId(slug, SECOND_TENANT);

    // then
    assertThat(consultingTypeId).isEqualTo(result.get(0).getId());
    assertThat(slug).isEqualTo(result.get(0).getSlug());
    assertThat(resultForAnotherTenant).isEmpty();
  }

  @Test
  public void findBySlug_Should_ReturnCorrectConsultingTyp() {
    // given
    Integer consultingTypeId = 10;
    String slug = "consultingtype10";

    // when
    List<ConsultingTypeEntity> result = consultingTypeMongoTenantAwareRepository.findBySlug(slug);

    // then
    assertThat(consultingTypeId).isEqualTo(result.get(0).getId());
    assertThat(slug).isEqualTo(result.get(0).getSlug());
  }

  @Test
  public void findAllHavingTenantId_Should_ReturnFilteredListOfConsultingTypes() {
    // given
    List<ConsultingTypeEntity> result1 =
        consultingTypeMongoTenantAwareRepository.findAllHavingTenantId(FIRST_TENANT);
    assertThat(result1).hasSize(1);

    // when
    List<ConsultingTypeEntity> result2 =
        consultingTypeMongoTenantAwareRepository.findAllHavingTenantId(SECOND_TENANT);

    // then
    assertThat(result2).hasSize(1);
  }

  @Test
  public void findAll_Should_ReturnAllConsultingTypes() {
    // when
    List<ConsultingTypeEntity> result = consultingTypeMongoTenantAwareRepository.findAll();
    // then
    assertThat(result).hasSize(3);
  }
}
