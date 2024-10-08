package com.amido.stacks.tests.api;

public enum OAuthConfigurations {
  CLIENT_ID(System.getenv("AUTH0_CLIENT_ID")),
  CLIENT_SECRET(System.getenv("AUTH0_CLIENT_SECRET")),
  AUDIENCE(System.getenv("AUTH0_AUDIENCE")),
  GRANT_TYPE(System.getenv("GRANT_TYPE")),
  OAUTH_TOKEN_URL(System.getenv("AUTH0_TOKEN_URL"));

  private final String config;

  OAuthConfigurations(String config) {
    this.config = config;
  }

  public String getOauthConfiguration() {
    return config;
  }
}
