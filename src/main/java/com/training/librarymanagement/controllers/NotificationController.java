package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.dtos.NotificationDTO;
import com.training.librarymanagement.services.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/library-management/api/notification")
@Api(tags = {"notification"})
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @ApiOperation(value = "Just a ping", authorizations = { @Authorization(value="library-jwt")}, tags = {"notification"})
    @GetMapping("/v1/notifications/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public String ping() {
        return notificationService.justAPing();
    }

    @ApiOperation(value = "Get all the notifications by account id", authorizations = { @Authorization(value="library-jwt")}, tags = {"notification"})
    @GetMapping("/v1/notifications/accounts/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationDTO> getNotificationsByAccountId(@PathVariable("accountId") String accountId) {
        return notificationService.getNotificationsByAccountId(accountId);
    }
}
