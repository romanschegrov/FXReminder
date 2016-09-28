package ru.schegrov.util;

import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import ru.schegrov.dao.ObjectDao;
import ru.schegrov.entity.Group;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.entity.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by ramon on 08.09.2016.
 */
public class JobSchedulerHelper {

    private static final Logger logger = Logger.getLogger(JobSchedulerHelper.class);
    private List<JobScheduledService> schedulers;
    private Executor executor;
    private static JobSchedulerHelper instance;
    private ResourceBundle resources;

    public static JobSchedulerHelper getInstance(ResourceBundle resources) {
        if (instance == null) {
            instance = new JobSchedulerHelper(resources);
        }
        return instance;
    }

    private JobSchedulerHelper(ResourceBundle resources) {
        this.resources = resources;
        schedulers = new ArrayList<>();
        executor = Executors.newFixedThreadPool(5,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        return thread;
                    }
                });
    }

    public void add(TreeItem<Job> item){
        Job job = item.getValue();

        if (job.isJob()) {
            item.setGraphic(ImageHelper.loadImage("/pic/document.png"));

            JobCondition scheduleCondition = job.getCondition("SCHEDULE");
            if (scheduleCondition == null) return;

            JobCondition timerCondition = job.getCondition("TIMER");
            if (timerCondition == null && scheduleCondition.getValue().equals("1")) return;

            JobCondition sqlCondition = job.getCondition("SQL");
            if (sqlCondition == null) return;

            JobScheduledService scheduler = new JobScheduledService(job);
            schedulers.add(scheduler);
            scheduler.setExecutor(executor);
            scheduler.setOnRunning(event -> {
                logger.info("Running job " + job.getName());
                item.setGraphic(ImageHelper.loadImage("/pic/refresh.png"));
            });
            scheduler.setOnSucceeded(event -> {
                Job onSucceededJob = (Job) event.getSource().getValue();
                logger.info("Succeeded job: " + onSucceededJob.getName() + ", Count row: "+ onSucceededJob.getRows().size());
                item.setGraphic(ImageHelper.loadImage("/pic/document.png"));

                String connectedUser = HibernateHelper.getConnectedUser().getCode();
                boolean notify = false;
                notifed:
                for (JobCondition condition : job.getConditions("NOTIFY")){
                    if (condition.getValue().equals(connectedUser)){
                        notify = true;
                        break notifed;
                    }
                    ObjectDao<Group> objectDao = new ObjectDao(Group.class);
                    Group group = objectDao.getByCode(condition.getValue());
                    if (group != null) {
                        for (User user : group.getUsers()){
                            if (user.getCode().equals(connectedUser)){
                                notify = true;
                                break notifed;
                            }
                        }
                    }
                }

                if (notify){
                    NotificationHelper notification = new NotificationHelper(resources, job, (Stage) item.getGraphic().getScene().getWindow());
                    notification.notify(Pos.BOTTOM_RIGHT);
                }
            });
            scheduler.setOnFailed(event -> {
                Job onFailedJob = (Job) event.getSource().getValue();
                logger.warn("Failed job " + onFailedJob.getName(), event.getSource().getException());
                item.setGraphic(ImageHelper.loadImage("/pic/warning.png"));
            });
            if (scheduleCondition.getValue().equals("1")){
                scheduler.setPeriod(Duration.minutes(Double.valueOf(job.getCondition("TIMER").getValue())));
                scheduler.start();
            }
        } else {
            item.setGraphic(ImageHelper.loadImage("/pic/folder.png"));
            item.setExpanded(true);
        }
    }

    public void cancelAll(){
        for (Iterator<JobScheduledService> iterator = schedulers.iterator(); iterator.hasNext(); ) {
            JobScheduledService scheduler = iterator.next();
            logger.info("canceled scheduler " + scheduler.getJob().getName());
            scheduler.cancel();
            iterator.remove();
        }
    }

    public void cancel(Job job){
        for (Iterator<JobScheduledService> iterator = schedulers.iterator(); iterator.hasNext(); ) {
            JobScheduledService scheduler = iterator.next();
            if (scheduler.getJob().getId() == job.getId()) {
                logger.info("canceled scheduler " + scheduler.getJob().getName());
                scheduler.cancel();
                iterator.remove();
                return;
            }
        }
    }

    public void restart(TreeItem<Job> item){
        Job job = item.getValue();
        cancel(job);
        add(item);
    }
}
