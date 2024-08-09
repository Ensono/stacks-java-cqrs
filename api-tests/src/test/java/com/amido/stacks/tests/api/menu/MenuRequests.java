package com.amido.stacks.tests.api.menu;

import com.amido.stacks.tests.api.OAuthConfigurations;
import com.amido.stacks.tests.api.WebServiceEndPoints;
import java.util.HashMap;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

public class MenuRequests {

  private static final String menuUrl =
      WebServiceEndPoints.BASE_URL.getUrl().concat(WebServiceEndPoints.MENU.getUrl());
  private static final String OAUTH_TOKEN_URL =
      OAuthConfigurations.OAUTH_TOKEN_URL.getOauthConfiguration();

  private static final EnvironmentVariables environmentVariables =
      SystemEnvironmentVariables.createEnvironmentVariables();

  private static final String generateAuthorisation =
      EnvironmentSpecificConfiguration.from(
              (net.thucydides.model.util.EnvironmentVariables) environmentVariables)
          .getProperty("generate.auth0.token");

  boolean generateToken = Boolean.parseBoolean(generateAuthorisation);
  private static final Map<String, String> commonHeaders = new HashMap<>();

  public MenuRequests() {
    String authorizationToken = String.valueOf(Serenity.getCurrentSession().get("Access Token"));

    if (generateToken) {
      commonHeaders.put("Authorization", "Bearer " + authorizationToken);
    }
  }

  @Step("Create a new menu")
  public void createMenu(String body) {
    SerenityRest.given()
        .contentType("application/json")
        .headers(commonHeaders)
        .body(body)
        .when()
        .post(menuUrl);
  }

  @Step("Update the menu")
  public void updateMenu(String body, String id) {
    SerenityRest.given()
        .contentType("application/json")
        .headers(commonHeaders)
        .body(body)
        .when()
        .put(menuUrl + "/" + id);
  }

  @Step("Get the menu")
  public void getMenu(String id) {
    SerenityRest.given().headers(commonHeaders).get(menuUrl.concat("/").concat(id));
  }

  @Step("Delete the menu")
  public static void deleteTheMenu(String id) {
    SerenityRest.given()
        .headers(commonHeaders)
        .contentType("application/json")
        .when()
        .delete(menuUrl.concat("/").concat(id));
  }

  @Step("Get all menus")
  public void getAllMenus() {
    SerenityRest.given().headers(commonHeaders).when().get(menuUrl);
  }

  public static void getMenusBySearchTerm(String searchTerm) {
    SerenityRest.given()
        .header("Authorization", "Bearer " + retrieveAccessTokenFromSerenity())
        .when()
        .get(menuUrl.concat("?searchTerm=").concat(searchTerm));
  }

  public static void getMenuByParametrisedPath(String parametrisedPath) {
    SerenityRest.given().headers(commonHeaders).when().get(parametrisedPath);
  }

  public static void getAuthorizationToken(String body) {
    SerenityRest.given().contentType("application/json").body(body).when().post(OAUTH_TOKEN_URL);
  }

  private static String retrieveAccessTokenFromSerenity() {
    return String.valueOf(Serenity.getCurrentSession().get("Access Token"));
  }

  public static void getMenuByParam(String parameter) {
    SerenityRest.given().headers(commonHeaders).when().get(menuUrl.concat("/").concat(parameter));
  }

  public static void getMenuByParam_V2(String parameter) {
    SerenityRest.given()
        .headers(commonHeaders)
        .when()
        .get(
            WebServiceEndPoints.BASE_URL
                .getUrl()
                .concat(WebServiceEndPoints.MENU_V2.getUrl())
                .concat("/")
                .concat(parameter));
  }
}
