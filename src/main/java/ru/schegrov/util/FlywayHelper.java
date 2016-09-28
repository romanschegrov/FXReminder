package ru.schegrov.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 * Created by ramon on 28.09.2016.
 */
public class FlywayHelper {

    private static FlywayHelper instance;
    private Flyway flyway;
    private String url;
    private String username;
    private String password;

    private FlywayHelper(String url, String username, String password) {
        flyway = new Flyway();
        flyway.setDataSource(url, username, password);
        flyway.setBaselineOnMigrate(true);
        flyway.setTable("T_FXR_FLYWAY");
    }

    public static FlywayHelper getInstance(String url, String username, String password) {
        if (instance == null) {
            instance = new FlywayHelper(url, username, password);
        }
        return instance;
    }

    public void migrate() throws FlywayException {
        flyway.migrate();
    }
}
