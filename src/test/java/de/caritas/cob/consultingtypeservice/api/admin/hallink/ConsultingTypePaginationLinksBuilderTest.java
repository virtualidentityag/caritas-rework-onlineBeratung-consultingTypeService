package de.caritas.cob.consultingtypeservice.api.admin.hallink;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import de.caritas.cob.consultingtypeservice.api.model.ExtendedConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.model.HalLink.MethodEnum;
import de.caritas.cob.consultingtypeservice.api.model.PaginationLinks;
import java.util.List;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.support.PagedListHolder;

public class ConsultingTypePaginationLinksBuilderTest {

  private final int NUMBER_OF_CONSULTING_TYPES = 15;

  @Test
  public void buildPaginationLinks_Should_returnPaginationLinks_When_allParametersAreSet() {
    EasyRandom easyRandom = new EasyRandom();
    List<ExtendedConsultingTypeResponseDTO> randomConsultingTypeResultDTOList =
        easyRandom
            .objects(ExtendedConsultingTypeResponseDTO.class, NUMBER_OF_CONSULTING_TYPES)
            .collect(Collectors.toList());
    PagedListHolder<ExtendedConsultingTypeResponseDTO> pagedListHolder =
        new PagedListHolder<>(randomConsultingTypeResultDTOList);
    pagedListHolder.setPage(1);
    pagedListHolder.setPageSize(11);
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(2)
            .withPerPage(10)
            .withPagedListHolder(pagedListHolder)
            .buildPaginationLinks();

    assertThat(paginationLinks, notNullValue());
    assertThat(paginationLinks.getSelf(), notNullValue());
    assertThat(paginationLinks.getSelf().getMethod(), is(MethodEnum.GET));
    assertThat(
        paginationLinks.getSelf().getHref(),
        is("/consultingtypeadmin/consultingtypes?page=2&perPage=10"));
    assertThat(paginationLinks.getPrevious(), notNullValue());
    assertThat(paginationLinks.getPrevious().getMethod(), is(MethodEnum.GET));
    assertThat(
        paginationLinks.getPrevious().getHref(),
        is("/consultingtypeadmin/consultingtypes?page=1&perPage=10"));
    assertThat(paginationLinks.getNext(), nullValue());
  }

  @Test
  public void buildPaginationLinks_Should_havePreviousLink_When_currentPageIsNotTheFirst() {
    EasyRandom easyRandom = new EasyRandom();
    List<ExtendedConsultingTypeResponseDTO> randomConsultingTypeResultDTOList =
        easyRandom
            .objects(ExtendedConsultingTypeResponseDTO.class, NUMBER_OF_CONSULTING_TYPES)
            .collect(Collectors.toList());
    PagedListHolder<ExtendedConsultingTypeResponseDTO> pagedListHolder =
        new PagedListHolder<>(randomConsultingTypeResultDTOList);
    pagedListHolder.setPage(1);
    pagedListHolder.setPageSize(10);
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(2)
            .withPerPage(10)
            .withPagedListHolder(pagedListHolder)
            .buildPaginationLinks();

    assertThat(paginationLinks.getPrevious(), notNullValue());
    assertThat(
        paginationLinks.getPrevious().getHref(),
        endsWith("/consultingtypeadmin/consultingtypes?page=1&perPage=10"));
  }

  @Test
  public void buildPaginationLinks_ShouldNot_havePreviousLink_When_currentPageIsTheFirst() {
    EasyRandom easyRandom = new EasyRandom();
    List<ExtendedConsultingTypeResponseDTO> randomConsultingTypeResultDTOList =
        easyRandom
            .objects(ExtendedConsultingTypeResponseDTO.class, NUMBER_OF_CONSULTING_TYPES)
            .collect(Collectors.toList());
    PagedListHolder<ExtendedConsultingTypeResponseDTO> pagedListHolder =
        new PagedListHolder<>(randomConsultingTypeResultDTOList);
    pagedListHolder.setPage(0);
    pagedListHolder.setPageSize(10);
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(1)
            .withPerPage(10)
            .withPagedListHolder(pagedListHolder)
            .buildPaginationLinks();

    assertThat(paginationLinks.getPrevious(), nullValue());
  }

  @Test
  public void buildPaginationLinks_Should_haveNextLink_When_currentPageIsNotTheLast() {
    EasyRandom easyRandom = new EasyRandom();
    List<ExtendedConsultingTypeResponseDTO> randomConsultingTypeResultDTOList =
        easyRandom
            .objects(ExtendedConsultingTypeResponseDTO.class, NUMBER_OF_CONSULTING_TYPES)
            .collect(Collectors.toList());
    PagedListHolder<ExtendedConsultingTypeResponseDTO> pagedListHolder =
        new PagedListHolder<>(randomConsultingTypeResultDTOList);
    pagedListHolder.setPage(0);
    pagedListHolder.setPageSize(10);
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(1)
            .withPerPage(10)
            .withPagedListHolder(pagedListHolder)
            .buildPaginationLinks();

    assertThat(paginationLinks.getNext(), notNullValue());
    assertThat(
        paginationLinks.getNext().getHref(),
        endsWith("/consultingtypeadmin/consultingtypes?page=2&perPage=10"));
  }

  @Test
  public void buildPaginationLinks_ShouldNot_haveNextLink_When_currentPageIsTheLast() {
    EasyRandom easyRandom = new EasyRandom();
    List<ExtendedConsultingTypeResponseDTO> randomConsultingTypeResultDTOList =
        easyRandom
            .objects(ExtendedConsultingTypeResponseDTO.class, NUMBER_OF_CONSULTING_TYPES)
            .collect(Collectors.toList());
    PagedListHolder<ExtendedConsultingTypeResponseDTO> pagedListHolder =
        new PagedListHolder<>(randomConsultingTypeResultDTOList);
    pagedListHolder.setPage(1);
    pagedListHolder.setPageSize(11);

    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(2)
            .withPerPage(10)
            .withPagedListHolder(pagedListHolder)
            .buildPaginationLinks();

    assertThat(paginationLinks.getNext(), nullValue());
  }

  @Test
  public void buildPaginationLinks_Should_returnSelfLink() {
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance()
            .withPage(0)
            .withPerPage(10)
            .buildPaginationLinks();

    assertThat(paginationLinks, notNullValue());
    assertThat(
        paginationLinks.getSelf().getHref(),
        is("/consultingtypeadmin/consultingtypes?page=0&perPage=10"));
  }

  @Test
  public void buildPaginationLinks_Should_returnDefaultPaginationValues_When_noParametersAreSet() {
    PaginationLinks paginationLinks =
        ConsultingTypePaginationLinksBuilder.getInstance().buildPaginationLinks();

    assertThat(paginationLinks, notNullValue());
    assertThat(
        paginationLinks.getSelf().getHref(),
        is("/consultingtypeadmin/consultingtypes?page=1&perPage=20"));
  }
}
