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
import ru.schegrov.util.ImageHelper;
import ru.schegrov.util.JobScheduledService;
import ru.schegrov.util.JobSchedulerHelper;
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
//    private boolean littleImage = true;
    private TreeView<Job> tree;
    private TableView <JobTableRow> table;
//    private Executor executor;
//    private List<JobScheduledService> schedulers;
    private JobSchedulerHelper scheduler;

    public AppModel(TreeView<Job> tree, TableView<JobTableRow> table) {
        this.tree = tree;
        this.table = table;
        scheduler = JobSchedulerHelper.getInstance();
        this.tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshTable(newValue.getValue());
            }
        });
        logger.info("init");
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void fillTreeView(){

        if (tree.getRoot() != null) {
            tree.getRoot().getChildren().clear();
            tree.setRoot(null);
//            schedulers.clear();
        }

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setId(0);
        rootJob.setParent_id(0);

        TreeItem<Job> rootItem = new TreeItem<>(rootJob, ImageHelper.loadImage("/pic/title16.png"));
        rootItem.setExpanded(true);
        tree.setRoot(rootItem);
        addChildren(rootItem);
    }

    private void addChildren(TreeItem<Job> parent){
        JobDao dao = new JobDao();
        List<Job> jobs = dao.getAllByParentId(parent.getValue().getId());

        for (Job job : jobs){
            TreeItem<Job> child = new TreeItem<>(job);
            scheduler.add(child);
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

//    public void schedulerAction(SchedulerAction action, Job job){
//        for (JobScheduledService scheduler : schedulers) {
//            switch (action){
//                case CANCEL_ALL:
//                    logger.info("cancel scheduler " + scheduler.getJob().getName());
//                    scheduler.cancel();
//                    break;
//                case CANCEL:
//                    if (scheduler.getJob().getId() == job.getId()) {
//                        logger.info("cancel scheduler " + scheduler.getJob().getName());
//                        scheduler.setJob(job);
//                        scheduler.cancel();
//                    }
//                    break;
//                case RESTART:
//                    if (scheduler.getJob().getId() == job.getId()) {
//                        logger.info("restart scheduler " + scheduler.getJob().getName());
//                        scheduler.setPeriod(Duration.seconds(Double.valueOf(job.getCondition("TIMER").getValue())));
//                        scheduler.setJob(job);
//                        scheduler.restart();
//                    }
//                    break;
//                default:
//                    logger.error("default action");
//                    break;
//            }
//        }
//    }

    public void exit() {
        System.exit(0);
    }

//    public ImageView loadImage(String path, boolean little) {
//        setLittleImage(little);
//        return loadImage(path);
//    }
//
//    public ImageView loadImage(String path) {
//        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
//        if (isLittleImage()){
//            view.setFitHeight(16);
//            view.setFitWidth(16);
//        }
//        return view;
//    }

//    public boolean isLittleImage() {
//        return littleImage;
//    }

//    public void setLittleImage(boolean littleImage) {
//        this.littleImage = littleImage;
//    }

    public JobSchedulerHelper getScheduler() {
        return scheduler;
    }
}
