package com.training.librarymanagement.controllers;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library-management/api/library")
@Api(value = "LibraryManagement")
public class LibraryController {

    private static Logger LOG = LoggerFactory.getLogger(LibraryController.class);

}
