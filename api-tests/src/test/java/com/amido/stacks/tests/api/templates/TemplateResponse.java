package com.amido.stacks.tests.api.templates;

import static java.util.stream.Collectors.toMap;

import io.restassured.common.mapper.TypeRef;
import java.util.Map;
import net.serenitybdd.rest.SerenityRest;

public class TemplateResponse {
  public Map<String, String> returned() {
    return mapOfStringsFrom(SerenityRest.lastResponse().getBody().as(new TypeRef<>() {}));
  }

  private Map<String, String> mapOfStringsFrom(Map<String, Object> map) {
    return map.entrySet().stream()
        .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
  }
}
