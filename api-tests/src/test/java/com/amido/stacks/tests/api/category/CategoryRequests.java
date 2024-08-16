package com.amido.stacks.tests.api.category;

import com.amido.stacks.tests.api.WebServiceEndPoints;
import java.util.HashMap;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

public class CategoryRequests {
  private static final String menuUrl =
      WebServiceEndPoints.BASE_URL.getUrl() + WebServiceEndPoints.MENU.getUrl();

  private static final EnvironmentVariables environmentVariables =
      SystemEnvironmentVariables.createEnvironmentVariables();

  private static final String generateAuthorisation =
      EnvironmentSpecificConfiguration.from(environmentVariables)
          .getProperty("generate.auth0.token");

  boolean generateToken = Boolean.parseBoolean(generateAuthorisation);
  private static final Map<String, String> commonHeaders = new HashMap<>();

  public CategoryRequests() {
    String authorizationToken = String.valueOf(Serenity.getCurrentSession().get("Access Token"));

    if (generateToken) {
      commonHeaders.put("Authorization", "Bearer " + authorizationToken);
    }
  }

  @Step("Create a new category")
  public void createCategory(String body, String menuID) {
    SerenityRest.given()
        .headers(commonHeaders)
        .contentType("application/json")
        .header("Content-Type", "application/json")
        .body(body)
        .when()
        .post(menuUrl.concat("/").concat(menuID).concat(WebServiceEndPoints.CATEGORY.getUrl()));
  }

  @Step("Update the category")
  public void updateCategory(String body, String menuID, String categoryID) {
    SerenityRest.given()
        .headers(commonHeaders)
        .contentType("application/json")
        .header("Content-Type", "application/json")
        .body(body)
        .when()
        .put(
            menuUrl
                .concat("/")
                .concat(menuID)
                .concat(WebServiceEndPoints.CATEGORY.getUrl())
                .concat("/")
                .concat(categoryID));
  }

  @Step("Delete the category")
  public void deleteTheCategory(String menuID, String categoryID) {
    SerenityRest.given()
        .headers(commonHeaders)
        .contentType("application/json")
        .when()
        .delete(
            menuUrl
                .concat("/")
                .concat(menuID)
                .concat(WebServiceEndPoints.CATEGORY.getUrl())
                .concat("/")
                .concat(categoryID));
  }
}
