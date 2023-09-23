package com.testing.api.stepDefinitions;

import com.testing.api.models.Client;
import com.testing.api.models.Resource;
import com.testing.api.requests.ClientRequest;
import com.testing.api.requests.ResourceRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class ClientSteps {
    private static final Logger logger = LogManager.getLogger(ClientSteps.class);
    private final ClientRequest clientRequest = new ClientRequest();
    private final ResourceRequest resourceRequest = new ResourceRequest();
    private Response response;
    private Client client;
    private Resource resource;

    @Given("there are registered clients in the system")
    public void thereAreRegisteredClientsInTheSystem() {
        response = clientRequest.getClients();
        Assert.assertEquals(response.getStatusCode(), 200);

        List <Client> clientList = clientRequest.getClientsEntity(response);
        Assert.assertFalse(clientList.isEmpty());
        if (clientList.size() < 3) {
            response = clientRequest.createDefaultClient();
            Assert.assertEquals(201, response.getStatusCode());
        }
        logger.info("Successful! there are registered clients in the system");
    }

    @Given("there are registered resources in the system")
    public void thereAreRegisteredResourcesInTheSystem() {
        response = resourceRequest.getResources();
        Assert.assertEquals(response.getStatusCode(), 200);
        List <Resource> resourceList = resourceRequest.getResourcesEntity(response);

        if (resourceList.size() < 5) {
            for (int i = 0; i < 5 - resourceList.size(); i++) {
                Response resTemp = resourceRequest.createDefaultResource();
                Assert.assertEquals(201, resTemp.getStatusCode());
            }
        }

        Assert.assertFalse(resourceList.isEmpty());
        response = resourceRequest.getResources();
        Assert.assertEquals(response.getStatusCode(), 200);
        logger.info("Successful! there are registered resources in the system");
    }

    @Given("I have a client with the following details:")
    public void iHaveAClientWithTheFollowingDetails(DataTable clientData) {
        Map<String, String> data = clientData.asMaps().get(0);
        client = Client.builder()
                .name(data.get("Name"))
                .lastName(data.get("LastName"))
                .country(data.get("Country"))
                .city(data.get("City"))
                .phone(data.get("Phone"))
                .email(data.get("Email"))
                .build();
        logger.info("Successful! I have a client with the following details:" + data);
    }

    @When("I send a GET request to view all the clients")
    public void iSendAGETRequestToViewAllTheClients() {
        response = clientRequest.getClients();
        logger.info("Successful! I send a GET request to view all the clients");
    }

    @When("I send a GET request to view all the resources")
    public void iSendAGETRequestToViewAllTheResources() {
        response = resourceRequest.getResources();
        logger.info("Successful! I send a GET request to view all the resources");
    }

    @When("I retrieve the details of the latest resource")
    public void iRetrieveTheDetailsOfTheLatestResource() {
        response = resourceRequest.getResources();
        List <Resource> resources = resourceRequest.getResourcesEntity(response);
        resource = resources.get(resources.size() - 1);
        logger.info("Successful! Got the last resource!");
    }

    @When("I send a PUT request to update the latest resource")
    public void iSendAPutRequestToUpdateTheLatestResource(String requestBody) {
        response = resourceRequest.updateResource(resourceRequest.getResourceEntity(requestBody), resource.getId());
        logger.info("Successful! I send a PUT request to update the latest resource");
    }

    @When("I send a POST request to create a client")
    public void iSendAPOSTRequestToCreateAClient() {
        response = clientRequest.createClient(client);
        logger.info("Successful! I send a POST request to create a client");
    }

    @Then("the response should have a status code of {int}")
    public void theResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
        logger.info("Successful! the response should have a status code of" + statusCode);
    }

    @Then("the response should have the following details:")
    public void theResponseShouldHaveTheFollowingDetails(DataTable expectedData) {
        resource = resourceRequest.getResourceEntity(response);
        logger.info(resource.getName(), " hola");
        Map <String, String> expectedDataMap = expectedData.asMaps().get(expectedData.asMaps().size() - 1);
        logger.info(expectedDataMap.get("name"), " adios");

        Assert.assertEquals(expectedDataMap.get("name"), resource.getName());
        Assert.assertEquals(expectedDataMap.get("trademark"), resource.getTrademark());
        Assert.assertEquals(Integer.parseInt(expectedDataMap.get("stock")), resource.getStock());
        Assert.assertEquals(Float.parseFloat(expectedDataMap.get("price")), resource.getPrice(), 0.1);
        Assert.assertEquals(expectedDataMap.get("description"), resource.getDescription());
        Assert.assertEquals(expectedDataMap.get("tags"), resource.getTags());
        Assert.assertEquals(Boolean.parseBoolean(expectedDataMap.get("is_active")), resource.getIs_active());

        logger.info("Successful! the response should have the following details:");
    }

    @Then("the response should include the details of the created client")
    public void theResponseShouldIncludeTheDetailsOfTheCreatedClient() {
        Client newClient = clientRequest.getClientEntity(response);
        newClient.setId(null);
        Assert.assertEquals(client, newClient);
        logger.info("Succcesful! the response should include the details of the created client");
    }

    @Then("validates the response with client JSON schema")
    public void userValidatesResponseWithClientJSONSchema() {
        String path = "schemas/clientSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, path));
        logger.info("Successful! validates the response with client JSON schema");
    }

    @Then("validates the response with client list JSON schema")
    public void userValidatesResponseWithClientListJSONSchema() {
        String path = "schemas/clientListSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, path));
        logger.info("Successful! validates the response with client list JSON schema");
    }

    @Then("validates the response with resource list JSON schema")
    public void userValidatesResponseWithResourceListJSONSchema() {
        String path = "schemas/resourceListSchema.json";
        Assert.assertTrue(resourceRequest.validateSchema(response, path));
        logger.info("Successful! validates the response with resource list JSON schema");
    }
}
