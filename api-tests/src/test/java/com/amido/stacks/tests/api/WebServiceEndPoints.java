package com.amido.stacks.tests.api;

import lombok.Getter;

@Getter
public enum WebServiceEndPoints {
  BASE_URL(System.getenv("BASE_URL")),
  CATEGORY("/category"),
  STATUS("/health"),
  MENU("/v1/menu"),
  MENU_V2("/v2/menu"),
  ITEMS("/items"),
  SECRETS("/v1/secrets");

  private final String url;

  WebServiceEndPoints(String url) {
    this.url = url;
  }
}
