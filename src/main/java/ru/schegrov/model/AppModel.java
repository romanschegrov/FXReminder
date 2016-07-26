package ru.schegrov.model;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.schegrov.dao.GenericDao;
import ru.schegrov.dao.JobDaoImpl;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ramon on 06.06.2016.
 */
public class AppModel {

    private static final Logger logger = Logger.getLogger(AppModel.class);
    private ResourceBundle resources;
    private boolean littleImage = true;

    public AppModel(ResourceBundle resources) {

        this.resources = resources;
        logger.info("AppModel init!");
    }

    public void fillTreeView(TreeView<Job> treeView){

        TreeItem<Job> rootItem = treeView.getRoot();
        if (rootItem != null) {
            rootItem.getChildren().clear();
        }

        Job rootJob = new Job();
        rootJob.setName(resources.getString("app.accordion.titledpane.jobs.root"));
        rootJob.setParent_id(0);

        rootItem = new TreeItem<>(rootJob, loadImage("/pic/title16.png"));
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        addChildren(rootItem);
    }

    private void addChildren(TreeItem<Job> parent){
        GenericDao<Job> dao = new JobDaoImpl();
        List<Job> jobs = dao.getAllByParentId(parent.getValue().getId());
        jobs.forEach(job -> {
            ImageView image;
            TreeItem<Job> child = new TreeItem<Job>(job);
            if (job.isJob()) {
                child.setGraphic(loadImage("/pic/document.png"));
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
