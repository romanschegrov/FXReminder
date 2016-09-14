package ru.schegrov.listener;

import javafx.scene.control.TreeItem;
import ru.schegrov.entity.Job;

/**
 * Created by ramon on 01.09.2016.
 */
public interface JobSelected {
    void selectJob(TreeItem<Job> jobTreeItem);
}
