@Functional
Feature: Update Menu

  @DeleteCreatedMenu @Smoke
  Scenario: Update menu - happy path
    Given the application is running
    And the following menu data:
      | name                                  | description     | tenantId                             | enabled |
      | The Lounge Cafe (Automated Test Data) | British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | false   |
    When I create the menu
    Then the menu was successfully created
    And the returned status code is 201

    When I update the menu with the following data:
      | name                             | description                  | tenantId                             | enabled |
      | The Lounge (Automated Test Data) | New Style Of British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | true    |
    Then the returned status code is 200
    When I search the updated menu
    Then the menu should include the following data:
      | name                             | description                  | tenantId                             | enabled |
      | The Lounge (Automated Test Data) | New Style Of British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | true    |


  Scenario: Update the menu that doesn't exist
    Given the application is running
    When I update the menu for "a0da8282-bfc6-4cc8-9f83-a111ad0e111c" id with data:
      | name          | description     | tenantId                             | enabled |
      | New Menu Name | New Description | d211f1ee-6c54-4b01-90e6-d701748f0852 | true    |
    Then the returned status code is 404
    And the 'menu does not exist' message is returned

  @DeleteCreatedMenu
  Scenario: Bad request for update menu - empty 'name' field
    Given the application is running
    And the following menu data:
      | name                                  | description     | tenantId                             | enabled |
      | The Lounge Cafe (Automated Test Data) | British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | false   |
    When I create the menu
    Then the menu was successfully created
    And the returned status code is 201

    When I update the menu with the following data:
      | name | description                  | tenantId                             | enabled |
      |      | New Style Of British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | true    |
    Then the returned status code is 400

  @DeleteCreatedMenu
  Scenario: Bad request for update menu - empty 'description' field
    Given the application is running
    And the following menu data:
      | name                                  | description     | tenantId                             | enabled |
      | The Lounge Cafe (Automated Test Data) | British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | false   |
    When I create the menu
    Then the menu was successfully created
    And the returned status code is 201
    When I update the menu with the following data:
      | name              | description | tenantId                             | enabled |
      | The new menu name |             | d211f1ee-6c54-4b01-90e6-d701748f0852 | true    |
    Then the returned status code is 400
    When I search the menu by criteria
      | searchTerm | The Lounge Cafe (Automated Test Data) |
    Then the returned status code is 200

  @DeleteCreatedMenu
  Scenario: Bad request for update menu - empty 'enabled' field
    Given the application is running
    And the following menu data:
      | name                                  | description     | tenantId                             | enabled |
      | The Lounge Cafe (Automated Test Data) | British Cuisine | d211f1ee-6c54-4b01-90e6-d701748f0852 | false   |
    When I create the menu
    Then the menu was successfully created
    And the returned status code is 201
    When I update the menu with the following data:
      | name                                    | description         | tenantId                             | enabled |
      | The new menu name (Automated Test Data) | Updated Description | d211f1ee-6c54-4b01-90e6-d701748f0852 |         |
    Then the returned status code is 400
