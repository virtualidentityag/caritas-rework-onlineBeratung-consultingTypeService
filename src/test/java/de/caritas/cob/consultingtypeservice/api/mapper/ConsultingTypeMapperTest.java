package de.caritas.cob.consultingtypeservice.api.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOGroupChat;
import de.caritas.cob.consultingtypeservice.api.model.SessionDataInitializingDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeMessageDTO;
import de.caritas.cob.consultingtypeservice.api.model.WhiteSpotDTO;
import org.junit.jupiter.api.Test;

public class ConsultingTypeMapperTest {

  @Test
  public void mapWhiteSpot_Should_InitializeDefaultObjectIfNull() {

    WhiteSpotDTO result = ConsultingTypeMapper.mapWhiteSpot(null);
    assertThat(result, notNullValue());
    assertThat(result.getWhiteSpotAgencyAssigned(), is(false));
    assertThat(result.getWhiteSpotAgencyId(), nullValue());
  }

  @Test
  public void mapGroupChat_Should_InitializeDefaultObjectIfNull() {

    BasicConsultingTypeResponseDTOGroupChat result = ConsultingTypeMapper.mapGroupChat(null);
    assertThat(result, notNullValue());
    assertThat(result.getIsGroupChat(), is(false));
    assertThat(result.getGroupChatRules(), nullValue());
  }

  @Test
  public void mapWelcomeMessage_Should_InitializeDefaultObjectIfNull() {

    WelcomeMessageDTO result = ConsultingTypeMapper.mapWelcomeMessage(null);
    assertThat(result, notNullValue());
    assertThat(result.getSendWelcomeMessage(), is(false));
    assertThat(result.getWelcomeMessageText(), nullValue());
  }

  @Test
  public void mapSessionDataInitializing_Should_InitializeDefaultObjectIfNull() {

    SessionDataInitializingDTO result = ConsultingTypeMapper.mapSessionDataInitializing(null);
    assertThat(result, notNullValue());
    assertThat(result.getAge(), is(false));
    assertThat(result.getAddictiveDrugs(), is(false));
    assertThat(result.getState(), is(false));
    assertThat(result.getGender(), is(false));
    assertThat(result.getRelation(), is(false));
  }
}
