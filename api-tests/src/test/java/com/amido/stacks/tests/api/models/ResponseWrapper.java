package com.amido.stacks.tests.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWrapper {

  private final Integer pageSize;
  private final Integer pageNumber;
  private final List<Menu> results;

  @JsonCreator
  public ResponseWrapper(
      @JsonProperty("pageSize") Integer pageSize,
      @JsonProperty("pageNumber") Integer pageNumber,
      @JsonProperty("results") List<Menu> results) {
    this.pageSize = pageSize;
    this.pageNumber = pageNumber;
    this.results = results;
  }
}
