package com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreatedResponse {

  @JsonProperty("id")
  private UUID id = null;
}
