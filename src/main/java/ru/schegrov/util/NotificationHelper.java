package ru.schegrov.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import ru.schegrov.entity.Job;

import java.util.ResourceBundle;

/**
 * Created by ramon on 24.09.2016.
 */
public class NotificationHelper {

    private enum NotificationType{
        WARNING, INFO, CONFIRM, ERROR
    }

    private final double SECONDS = 10;
    private final boolean OWNER = false;
    private ResourceBundle resources;
    private Job job;
    private Stage stage;
    private NotificationType type;

    public NotificationHelper(ResourceBundle resources, Job job) {
        this(resources, job, null, NotificationType.INFO);
    }

    public NotificationHelper(ResourceBundle resources, Job job, Stage stage) {
        this(resources, job, stage, NotificationType.INFO);
    }

    public NotificationHelper(ResourceBundle resources, Job job, Stage stage, NotificationType type) {
        this.resources = resources;
        this.job = job;
        this.stage = stage;
        this.type = type;
    }

    public void notify(Pos pos) {
        Notifications notificationBuilder = Notifications.create()
            .title(resources.getString("app.title"))
            .text(job.getName() + " (" + job.getCount() + ")")
            .graphic(null)
            .hideAfter(Duration.seconds(SECONDS))
            .position(pos)
            .onAction(event -> {
                if (stage != null) {
                    stage.setIconified(false);
                    stage.requestFocus();
                }
            });

        switch (type) {
            case WARNING: notificationBuilder.showWarning(); break;
            case INFO: notificationBuilder.showInformation(); break;
            case CONFIRM: notificationBuilder.showConfirm(); break;
            case ERROR: notificationBuilder.showError(); break;
            default: notificationBuilder.show();
        }
    }
}
