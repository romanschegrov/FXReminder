package ru.schegrov.util;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import ru.schegrov.dao.JobDao;
import ru.schegrov.model.Job;
import ru.schegrov.model.JobTableRow;

import java.util.*;

/**
 * Created by ramon on 08.08.2016.
 */
public class JobScheduler extends ScheduledService<Job> {

    private static final Logger logger = Logger.getLogger(JobScheduler.class);

    private Job job;
//    private TreeView<Job> tree;
//    private TableView<JobTableRow> table;

    public JobScheduler(Job job /*, TreeView<Job> tree, TableView<JobTableRow> table*/) {
        this.job = job;
//        this.tree = tree;
//        this.table = table;

        logger.info("init " + job.getName());
    }

    @Override
    protected Task<Job> createTask() {
        return new Task<Job>() {
            @Override
            protected Job call() throws Exception {

                job.getColumns().clear();
                job.getRows().clear();

                JobDao reader = new JobDao();
                List<Map<String, Object>> sqlRows = reader.getAllRows(job.getCondition("SQL"));
                boolean isFirstRow = true;
                for (Map<String, Object> sqlRow : sqlRows) {
                    JobTableRow row = new JobTableRow();
                    Set<Map.Entry<String, Object>> sqlColumns = sqlRow.entrySet();
                    Iterator<Map.Entry<String, Object>> iterator = sqlColumns.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> sqlColumn = iterator.next();
                        row.setColumn(sqlColumn.getValue());
                        //logger.info("key " + sqlColumn.getKey() + " value " + sqlColumn.getValue());
                        if (isFirstRow){
                            TableColumn<JobTableRow,Object> column = new TableColumn<>(sqlColumn.getKey());
                            //column.setCellFactory(TableCell. <JobTableRow>forTableColumn());
                            //column.setCellValueFactory
                            job.getColumns().add(column);
                        }
                        job.getRows().add(row);
                    }
                    isFirstRow = false;
                }
                job.setCount(sqlRows.size());
                logger.info("size " + sqlRows.size());
                updateValue(job);
                return job;
            }
        };
    }

    //    @Override
//    protected Task<List<JobTableRow>> createTask() {
//        return new Task<List<JobTableRow>>() {
//            @Override
//            protected List<JobTableRow> call() throws Exception {
//
//                try {Thread.sleep(5000);} catch (InterruptedException e) {}
//
//
//                JobDao reader = new JobDao();
//                List sqlRows = reader.getAllRows(job.getCondition("SQL"));
//
//                for (Object object : sqlRows){
//                    Map map = (Map) object;
//                    row = new JobTableRow();
//
//                    Set keySet = map.keySet();
//                    Iterator iterator = keySet.iterator();
//                    while (iterator.hasNext()){
//                        if (row.getColumn1() == null) row.setColumn1(String.valueOf(map.get(iterator.next())));
//                        if (row.getColumn2() == null) row.setColumn2(String.valueOf(map.get(iterator.next())));
//                    }
//                    tableRows.add(row);
//                }
//                job.setName(job.getName()+" "+tableRows.size());
//                return tableRows;
//                return job;
//            }
//        };
//    }
}
