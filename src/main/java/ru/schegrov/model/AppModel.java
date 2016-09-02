package ru.schegrov.model;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import ru.schegrov.dao.JobDao;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobCondition;
import ru.schegrov.entity.JobTableRow;
import ru.schegrov.util.JobScheduler;
import ru.schegrov.util.SchedulerAction;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by ramon on 06.06.2016.
 */
public class AppModel {

    private static final Logger logger = Logger.getLogger(AppModel.class);

    private ResourceBundle resources;
    private boolean littleImage = true;
    private TreeView<Job> tree;
    private TableView <JobTableRow> table;
    private Executor executor;
    private List<JobScheduler> schedulers;

    public AppModel(TreeView<Job> tree, TableView<JobTableRow> table) {
        this.tree = tree;
        this.table = table;
        this.tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshTable(newValue.getValue());
            }
        });
        executor = Executors.newFixedThreadPool(2,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        return thread;
                    }
                });
        schedulers = new ArrayList<>();
        logger.info("init");
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void fillTreeView(){

        if (tree.getRoot() != null) {
            tree.getRoot().getChildren().clear();
            tree.setRoot(null);
            schedulers.clear();
        }

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setId(0);
        rootJob.setParent_id(0);

        TreeItem<Job> rootItem = new TreeItem<>(rootJob, loadImage("/pic/title16.png"));
        rootItem.setExpanded(true);
        tree.setRoot(rootItem);
        addChildren(rootItem);
    }

    private void addChildren(TreeItem<Job> parent){
        JobDao dao = new JobDao();
        List<Job> jobs = dao.getAllByParentId(parent.getValue().getId());

        for (Job job : jobs){

            TreeItem<Job> child = new TreeItem<>(job);

            if (job.isJob()) {
                child.setGraphic(loadImage("/pic/document.png"));

                JobCondition scheduleCondition = job.getCondition("SCHEDULE");
                if (scheduleCondition == null) break;

                JobCondition timerCondition = job.getCondition("TIMER");
                if (timerCondition == null && scheduleCondition.getValue().equals("1")) break;

                JobCondition sqlCondition = job.getCondition("SQL");
                if (sqlCondition == null) break;

//                job.getConditions().add(new JobCondition("SQL","select created \"Тип\", to_char(created,'dd-mm-yyyy') \"Транзакция\", to_date(to_char(created,'dd-mm-yyyy'),'dd-mm-yyyy') as \"Дата документа\" from dkb.trans_all where value_date>=trunc(sysdate)-60 and  rownum<=round(dbms_random.value*10)"));
//                job.getConditions().add(new JobCondition("TIMER","15"));

                JobScheduler service = new JobScheduler(job);
                schedulers.add(service);
                service.setExecutor(executor);
                service.setOnRunning(event -> {
                    logger.info("Running job " + job.getName());
                    child.setGraphic(loadImage("/pic/refresh.png"));
                });
                service.setOnSucceeded(event -> {
                    Job onSucceededJob = (Job) event.getSource().getValue();
                    logger.info("Succeeded job: " + onSucceededJob.getName() + ", Count row: "+ onSucceededJob.getRows().size());
                    child.setGraphic(loadImage("/pic/document.png"));
                    refreshTable(onSucceededJob);
                });
                service.setOnFailed(event -> {
                    Job onFailedJob = (Job) event.getSource().getValue();
                    logger.warn("Failed job " + onFailedJob.getName(), event.getSource().getException());
                    child.setGraphic(loadImage("/pic/warning.png"));
                });
                if (scheduleCondition.getValue().equals("1")){
                    service.setPeriod(Duration.seconds(Double.valueOf(job.getCondition("TIMER").getValue())));        //////seconds!!!!!!!!!!!!!!!
                    service.start();
                }
            } else {
                child.setGraphic(loadImage("/pic/folder.png"));
                child.setExpanded(true);
            }

            parent.getChildren().add(child);
            addChildren(child);
        }
    }

    private void refreshTable(Job newJob){
        logger.trace("refreshTable");
        if (newJob != null) {
            if (!tree.getSelectionModel().isEmpty()) {

                Job selectedJob = tree.getSelectionModel().getSelectedItem().getValue();
                logger.trace("selectedJob = " + selectedJob);
                logger.trace("newJob = " + newJob);

                if (newJob.equals(selectedJob)) {
                    logger.trace("equals");
                    table.getColumns().clear();
                    table.getColumns().addAll(newJob.getColumns());

                    table.setItems(newJob.getRows());
                    logger.trace("newJob.getRows() " + newJob.getRows().size());
                } else {
                    logger.trace("not equals");
                }
            }
        }
    }

    public void schedulerAction(SchedulerAction action, Job job){
        for (JobScheduler scheduler : schedulers) {
            switch (action){
                case CANCEL_ALL:
                    logger.info("cancel scheduler " + scheduler.getJob().getName());
                    scheduler.cancel();
                    break;
                case CANCEL:
                    if (scheduler.getJob().getId() == job.getId()) {
                        logger.info("cancel scheduler " + scheduler.getJob().getName());
                        scheduler.setJob(job);
                        scheduler.cancel();
                    }
                    break;
                case RESTART:
                    if (scheduler.getJob().getId() == job.getId()) {
                        logger.info("restart scheduler " + scheduler.getJob().getName());
                        scheduler.setPeriod(Duration.seconds(Double.valueOf(job.getCondition("TIMER").getValue())));
                        scheduler.setJob(job);
                        scheduler.restart();
                    }
                    break;
                default:
                    logger.error("default action");
                    break;
            }
        }
    }

    public void exit() {
        System.exit(0);
    }

    public ImageView loadImage(String path, boolean little) {
        setLittleImage(little);
        return loadImage(path);
    }

    public ImageView loadImage(String path) {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
        if (isLittleImage()){
            view.setFitHeight(16);
            view.setFitWidth(16);
        }
        return view;
    }

    public boolean isLittleImage() {
        return littleImage;
    }

    public void setLittleImage(boolean littleImage) {
        this.littleImage = littleImage;
    }
}
