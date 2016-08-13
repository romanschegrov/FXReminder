package ru.schegrov.model;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import ru.schegrov.dao.JobDao;
import ru.schegrov.util.JobScheduledService;

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
    private TableView<JobTableRow> table;
    private Executor executor;

    public AppModel(ResourceBundle resources, TreeView<Job> tree) {

        this.resources = resources;
        this.tree = tree;
        this.executor = Executors.newFixedThreadPool(5,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        return thread;
                    }
                });

        logger.info("AppModel init!");
    }

    public void fillTreeView(){

        TreeItem<Job> rootItem = tree.getRoot();
        if (rootItem != null) {
            rootItem.getChildren().clear();
        }

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setParent_id(0);

        rootItem = new TreeItem<>(rootJob, loadImage("/pic/title16.png"));
        rootItem.setExpanded(true);
        tree.setRoot(rootItem);
        addChildren(rootItem);
    }

    private void addChildren(TreeItem<Job> parent){
        JobDao dao = new JobDao();
        List<Job> jobs = dao.getAllByParentId(parent.getValue().getId());
        jobs.forEach(job -> {
            TreeItem<Job> child = new TreeItem<Job>(job);

            if (job.isJob()) {
                child.setGraphic(loadImage("/pic/document.png"));

                job.getConditions().add(new JobCondition("SQL","select header_no, value_date as Дата, doc_type from dkb.trans_all where value_date>=trunc(sysdate)-30"));
                job.getConditions().add(new JobCondition("TIMER","15"));
//              (new JobCondition("SHOW","RAMONHD"));
//              job.add(new JobCondition("ACCESS","RAMONHD"));

                JobScheduledService service = new JobScheduledService(job);
                service.setExecutor(executor);
                service.setPeriod(Duration.seconds(Double.valueOf(job.getCondition("TIMER"))));        //////seconds!!!!!!!!!!!!!!!
                service.setOnRunning(event -> {
                    logger.info("Running job " + job.getName());
                    child.setGraphic(loadImage("/pic/refresh.png"));
                });
                service.setOnSucceeded(event -> {
                    logger.info("Succeeded job " + job.getName());
                    child.setGraphic(loadImage("/pic/document.png"));
                });
                service.setOnFailed(event -> {
                    logger.warn("Failed job " + job.getName(), event.getSource().getException());
                    child.setGraphic(loadImage("/pic/warning.png"));
                });
                service.start();

            } else {
                child.setGraphic(loadImage("/pic/folder.png"));
                child.setExpanded(true);
            }

            parent.getChildren().add(child);
            addChildren(child);
        });
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
