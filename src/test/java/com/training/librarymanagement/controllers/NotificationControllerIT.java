package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.dtos.NotificationDTO;
import com.training.librarymanagement.jwt.AuthenticationRequest;
import com.training.librarymanagement.utils.CommonTestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationControllerIT extends CommonTestUtils {

    @LocalServerPort
    private int port;

    @Value("${library.notification.server.port}")
    private int notificationServerPort;

    private ClientAndServer mockServer;
    private Account admin;
    private String tokenAdmin;


    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(notificationServerPort);
        admin = createAccount("giutav", "password", "Giulia", "Tavolaro", null);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("giutav", "password");
        tokenAdmin = RestAssured.given().port(port)
            .body(authenticationRequest)
            .expect()
            .when().post("/library-management/signin")
            .then().assertThat().statusCode(200)
            .extract().header("Authorization");
    }

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void testGetNotifications_Succeed() {

        MockServerClient client = new MockServerClient("127.0.0.1", notificationServerPort);

        client
            .when(
                request()
                    .withMethod("GET")
                    .withPath(String.format("/library-notification/api/notifications/v1/accounts/%s", admin.getId())),
                exactly(1))
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeaders(
                        new Header("Content-Type", "application/json;charset=UTF-8"))
                    .withBody(String.format("[{\"accountId\":\"%s\",\"message\":\"Strange Message\"}]", admin.getId()))
                    .withDelay(TimeUnit.MILLISECONDS,100)
            );

        NotificationDTO[] notifications = RestAssured.given().port(port)
            .pathParam("accountId", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/notification/v1/notifications/accounts/{accountId}")
            .then().assertThat().statusCode(200)
            .extract().as(NotificationDTO[].class);

        List<NotificationDTO> notificationList = Arrays.asList(notifications);
        assertEquals(1, notificationList.size());

    }
}
