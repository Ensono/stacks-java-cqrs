package com.amido.stacks.menu.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchMenuResultItem {

  @JsonProperty("id")
  private UUID id;

  @JsonProperty("restaurantId")
  private UUID restaurantId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("enabled")
  private Boolean enabled;
}
