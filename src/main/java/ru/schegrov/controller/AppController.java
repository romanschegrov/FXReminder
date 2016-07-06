package ru.schegrov.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ru.schegrov.model.AppModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AppController {

    private static final Logger logger = Logger.getLogger(AppController.class);

    private AppModel model;

    public AppController(AppModel model) {
        this.model = model;
        logger.info("AppController init!");
    }
}
