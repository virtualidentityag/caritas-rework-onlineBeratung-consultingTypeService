package de.caritas.cob.consultingtypeservice.api.controller;

import static de.caritas.cob.consultingtypeservice.api.auth.Authority.AuthorityValue.CREATE_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.api.auth.Authority.AuthorityValue.LIMITED_PATCH_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_BASIC_CONSULTING_TYPE_BY_ID;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_BASIC_CONSULTING_TYPE_LIST;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_CONSULTING_TYPE_GROUPS;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_EXTENDED_CONSULTING_TYPE_BY_ID;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_FULL_CONSULTING_TYPE_BY_ID;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.PATH_GET_FULL_CONSULTING_TYPE_BY_SLUG;
import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.ROOT_PATH;
import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.caritas.cob.consultingtypeservice.api.auth.RoleAuthorizationAuthorityMapper;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.consultingtypeservice.api.mapper.BasicConsultingTypeMapper;
import de.caritas.cob.consultingtypeservice.api.mapper.ExtendedConsultingTypeMapper;
import de.caritas.cob.consultingtypeservice.api.mapper.FullConsultingTypeMapper;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ExtendedConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.service.ConsultingTypeGroupService;
import de.caritas.cob.consultingtypeservice.api.service.ConsultingTypeService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver;
import de.caritas.cob.consultingtypeservice.testHelper.HelperMethods;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.random.EasyRandom;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=testing")
class ConsultingTypeControllerIT {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mvc;

  @Autowired private WebApplicationContext context;

  @Autowired private RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper;

  @MockBean private ConsultingTypeService consultingTypeService;

  @MockBean private ConsultingTypeGroupService consultingTypeGroupService;
  @MockBean private LinkDiscoverers linkDiscoverers;
  @MockBean private TenantResolver tenantResolver;

  @BeforeEach
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  private final ConsultingTypeConverter consultingTypeConverter = new ConsultingTypeConverter();

  @Test
  void getBasicConsultingTypeList_Should_ReturnNoContent_When_ServiceReturnsEmptyList()
      throws Exception {

    when(consultingTypeService.fetchBasicConsultingTypesList()).thenReturn(null);

    mvc.perform(get(PATH_GET_BASIC_CONSULTING_TYPE_LIST).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void getBasicConsultingTypeList_Should_ReturnConsultingTypeBasicList() throws Exception {

    BasicConsultingTypeResponseDTO basicConsultingTypeResponseDTO =
        BasicConsultingTypeMapper.mapConsultingType(HelperMethods.getConsultingType());
    BasicConsultingTypeResponseDTO[] basicConsultingTypeResponseDTOArray = {
      basicConsultingTypeResponseDTO, basicConsultingTypeResponseDTO
    };
    String basicConsultingTypeResponseDTOJson =
        new ObjectMapper().writeValueAsString(basicConsultingTypeResponseDTOArray);

    when(consultingTypeService.fetchBasicConsultingTypesList())
        .thenReturn(Arrays.asList(basicConsultingTypeResponseDTO, basicConsultingTypeResponseDTO));

    mvc.perform(get(PATH_GET_BASIC_CONSULTING_TYPE_LIST).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(json().isEqualTo(basicConsultingTypeResponseDTOJson));
  }

  @Test
  void getFullConsultingTypeById_Should_ReturnFullConsultingTypeDTO() throws Exception {

    Integer consultingTypeId = 1;
    when(consultingTypeService.fetchFullConsultingTypeSettingsById(consultingTypeId))
        .thenReturn(FullConsultingTypeMapper.mapConsultingType(HelperMethods.getConsultingType()));

    mvc.perform(
            get(String.format(PATH_GET_FULL_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            json()
                .isEqualTo(
                    removeGroupsNode(HelperMethods.getConsultingTypeSettingsAsJsonString())));
  }

  @Test
  void getFullConsultingTypeById_Should_ReturnNotFound_WhenConsultingTypeIsMissing()
      throws Exception {

    Integer consultingTypeId = 1;
    when(consultingTypeService.fetchFullConsultingTypeSettingsById(consultingTypeId))
        .thenThrow(new NotFoundException("Not found"));

    mvc.perform(
            get(String.format(PATH_GET_FULL_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(json().isStringEqualTo(StringUtils.EMPTY));
  }

  @Test
  void getFullConsultingTypeBySlug_Should_ReturnFullConsultingTypeDTO() throws Exception {

    String consultingTypeSlug = "consultingtype0";
    when(consultingTypeService.fetchFullConsultingTypeSettingsBySlug(consultingTypeSlug))
        .thenReturn(FullConsultingTypeMapper.mapConsultingType(HelperMethods.getConsultingType()));

    mvc.perform(
            get(String.format(PATH_GET_FULL_CONSULTING_TYPE_BY_SLUG, consultingTypeSlug))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            json()
                .isEqualTo(
                    removeGroupsNode(HelperMethods.getConsultingTypeSettingsAsJsonString())));
  }

  @Test
  void getFullConsultingTypeBySlug_Should_ReturnNotFound_WhenConsultingTypeIsMissing()
      throws Exception {

    String consultingTypeSlug = "consultingtype0";
    when(consultingTypeService.fetchFullConsultingTypeSettingsBySlug(consultingTypeSlug))
        .thenThrow(new NotFoundException("Not found"));

    mvc.perform(
            get(String.format(PATH_GET_FULL_CONSULTING_TYPE_BY_SLUG, consultingTypeSlug))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(json().isStringEqualTo(StringUtils.EMPTY));
  }

  @Test
  void getExtendedConsultingTypeById_Should_ReturnFullConsultingTypeDTO() throws Exception {

    Integer consultingTypeId = 1;
    ExtendedConsultingTypeResponseDTO extendedConsultingTypeResponseDTO =
        ExtendedConsultingTypeMapper.mapConsultingType(HelperMethods.getConsultingType());
    when(consultingTypeService.fetchExtendedConsultingTypeSettingsById(consultingTypeId))
        .thenReturn(extendedConsultingTypeResponseDTO);

    mvc.perform(
            get(String.format(PATH_GET_EXTENDED_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            json()
                .isEqualTo(
                    new ObjectMapper().writeValueAsString(extendedConsultingTypeResponseDTO)));
  }

  @Test
  void getExtendedConsultingTypeId_Should_ReturnNotFound_WhenConsultingTypeIsMissing()
      throws Exception {

    Integer consultingTypeId = 1;
    when(consultingTypeService.fetchExtendedConsultingTypeSettingsById(consultingTypeId))
        .thenThrow(new NotFoundException("Not found"));

    mvc.perform(
            get(String.format(PATH_GET_EXTENDED_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(json().isStringEqualTo(StringUtils.EMPTY));
  }

  @Test
  void getBasicConsultingTypeById_Should_ReturnBasicConsultingTypeDTO() throws Exception {

    Integer consultingTypeId = 1;
    BasicConsultingTypeResponseDTO basicConsultingTypeResponseDTO =
        BasicConsultingTypeMapper.mapConsultingType(HelperMethods.getConsultingType());
    when(consultingTypeService.fetchBasicConsultingTypeSettingsById(consultingTypeId))
        .thenReturn(basicConsultingTypeResponseDTO);

    mvc.perform(
            get(String.format(PATH_GET_BASIC_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            json()
                .isEqualTo(new ObjectMapper().writeValueAsString(basicConsultingTypeResponseDTO)));
  }

  @Test
  void getBasicConsultingTypeById_Should_ReturnNotFound_WhenConsultingTypeIsMissing()
      throws Exception {

    Integer consultingTypeId = 1;
    when(consultingTypeService.fetchBasicConsultingTypeSettingsById(consultingTypeId))
        .thenThrow(new NotFoundException("Not found"));

    mvc.perform(
            get(String.format(PATH_GET_BASIC_CONSULTING_TYPE_BY_ID, consultingTypeId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(json().isStringEqualTo(StringUtils.EMPTY));
  }

  @Test
  void getConsultingTypeGroups_Should_ReturnNoContent_WhenNoGroupsDefined() throws Exception {

    when(consultingTypeGroupService.fetchConsultingTypeGroupList())
        .thenReturn(Collections.emptyList());

    mvc.perform(get(PATH_GET_CONSULTING_TYPE_GROUPS).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void getConsultingTypeGroups_Should_ReturnListOfConsultingTypeGroupResponseDTO()
      throws Exception {

    var consultingTypeGroupsReponseJson =
        HelperMethods.loadConsultingTypeGroupResponseAsJsonString();
    var consultingTypeGroupsResponse = HelperMethods.loadConsultingTypeGroupResponse();
    when(consultingTypeGroupService.fetchConsultingTypeGroupList())
        .thenReturn(consultingTypeGroupsResponse);

    mvc.perform(get(PATH_GET_CONSULTING_TYPE_GROUPS).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(json().isEqualTo(consultingTypeGroupsReponseJson));
  }

  @Test
  void getConsultingTypeGroups_Should_ReturnInternalServerError_WhenUnexpectedErrorOccurs()
      throws Exception {

    when(consultingTypeGroupService.fetchConsultingTypeGroupList())
        .thenThrow(new UnexpectedErrorException());

    mvc.perform(get(PATH_GET_CONSULTING_TYPE_GROUPS).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(authorities = CREATE_CONSULTING_TYPE)
  void createConsultingType_Should_returnOk_When_requiredConsultingTypeDTOIsGiven()
      throws Exception {
    // given
    ConsultingTypeDTO consultingTypeDTO =
        new EasyRandom().nextObject(ConsultingTypeDTO.class).roles(null);

    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    // when
    this.mvc
        .perform(
            post(ROOT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultingTypeDTO)))
        // then
        .andExpect(status().isOk());

    verify(consultingTypeService).createConsultingType(consultingTypeDTO);
  }

  @Test
  @WithMockUser(authorities = LIMITED_PATCH_CONSULTING_TYPE)
  void
      createConsultingType_Should_returnForbidden_When_AttemptToCreateConsultingTypeWithoutPermissions()
          throws Exception {
    // given
    ConsultingTypeDTO consultingTypeDTO =
        new EasyRandom().nextObject(ConsultingTypeDTO.class).roles(null);

    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    // when
    this.mvc
        .perform(
            post(ROOT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultingTypeDTO)))
        // then
        .andExpect(status().isForbidden());
  }

  private String removeGroupsNode(String consultingTypeSettingsAsJsonString) {
    JSONObject jsonObject = new JSONObject(consultingTypeSettingsAsJsonString);
    jsonObject.remove("groups");
    return jsonObject.toString();
  }
}
