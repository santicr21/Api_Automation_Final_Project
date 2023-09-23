@active
Feature: Client testing CRUD
  @smoke
  Scenario: Get the list of all clients registered
    Given there are registered clients in the system
    When I send a GET request to view all the clients
    Then the response should have a status code of 200
    And validates the response with client list JSON schema

  @smoke
  Scenario: Get the list of all resources registered
    Given there are registered resources in the system
    When I send a GET request to view all the resources
    Then the response should have a status code of 200
    And validates the response with resource list JSON schema

  @smoke
  Scenario: Create a new client in the system
    Given I have a client with the following details:
      | Name     | LastName    | Country | City     | Email              | Phone               |
      | Santiago | Canedo      | USA     | New York | new-mail@gmail.com | 427-528-7246 x97323 |
    When I send a POST request to create a client
    Then the response should have a status code of 201
    And the response should include the details of the created client
    And validates the response with client JSON schema

  @smoke
  Scenario: Verify that a registered resource can be updated successfully
    Given there are registered resources in the system
    And I retrieve the details of the latest resource
    When I send a PUT request to update the latest resource
    """
    {
      "name": "Prueba de oriental",
      "trademark": "Mor, Zulauf and Schaefer",
      "stock": 50000,
      "price": 27000,
      "description": "I like pants",
      "tags": "new status",
      "is_active": true
    }
    """
    Then the response should have a status code of 200
    And the response should have the following details:
      | name               | trademark                | stock   | price     | description  | tags       | is_active |
      | Prueba de oriental | Mor, Zulauf and Schaefer | 50000   | 27000.07  | I like pants | new status | true      |