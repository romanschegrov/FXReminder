package ru.schegrov.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import ru.schegrov.dao.JobDao;
import ru.schegrov.util.JobScheduler;

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

    public AppModel(TreeView<Job> tree, TableView<JobTableRow> table) {
        this.tree = tree;
        this.table = table;
        this.tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                refreshTable(newValue.getValue()));
        this.executor = Executors.newFixedThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        return thread;
                    }
                });

        logger.info("init");
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void fillTreeView(){

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setParent_id(0);

        TreeItem<Job> rootItem = new TreeItem<>(rootJob, loadImage("/pic/title16.png"));
        rootItem.valueProperty().addListener((observable, oldJob, newJob) -> refreshTable(newJob));
        rootItem.setExpanded(true);
        tree.setRoot(rootItem);
        addChildren(rootItem);
    }

    private void addChildren(TreeItem<Job> parent){
        JobDao dao = new JobDao();
        List<Job> jobs = dao.getAllByParentId(parent.getValue().getId());
        jobs.forEach(job -> {
            TreeItem<Job> child = new TreeItem<Job>(job);
            child.valueProperty().addListener((observable, oldJob, newJob) -> refreshTable(newJob));

            if (job.isJob()) {
                child.setGraphic(loadImage("/pic/document.png"));

                //
                job.getConditions().add(new JobCondition("SQL","select doc_type as Тип, header_no as \"Номер Транзакции\", value_date as Дата from dkb.trans_all where value_date>=trunc(sysdate)-"+((int)(Math.random()*40))));
                job.getConditions().add(new JobCondition("TIMER","15"));
//              (new JobCondition("SHOW","RAMONHD"));
//              job.add(new JobCondition("ACCESS","RAMONHD"));

                JobScheduler service = new JobScheduler(job);
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

    private void refreshTable(Job newJob){
        if (newJob != null) {
            if (!tree.getSelectionModel().isEmpty()) {

                Job selectedJob = tree.getSelectionModel().getSelectedItem().getValue();

                if (newJob.equals(selectedJob)) {
                    table.getColumns().clear();
                    table.getColumns().addAll(newJob.getColumns());

                    table.setItems(newJob.getRows());

                }
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
