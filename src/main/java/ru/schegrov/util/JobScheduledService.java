package ru.schegrov.util;


import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import ru.schegrov.dao.JobDao;
import ru.schegrov.model.Job;
import ru.schegrov.model.JobTableRow;

import java.util.*;

/**
 * Created by ramon on 08.08.2016.
 */
public class JobScheduledService extends ScheduledService<List<JobTableRow>> {

    private Job job;
//    private TreeView<Job> tree;
//    private TableView<JobTableRow> table;

    public JobScheduledService(Job job /*, TreeView<Job> tree, TableView<JobTableRow> table*/) {
        this.job = job;
//        this.tree = tree;
//        this.table = table;
    }

    @Override
    protected Task<List<JobTableRow>> createTask() {
        return new Task<List<JobTableRow>>() {
            @Override
            protected List<JobTableRow> call() throws Exception {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}


                JobDao reader = new JobDao();
                List sqlRows = reader.getAllRows(job.getCondition("SQL"));

                JobTableRow row;
                List<JobTableRow> tableRows = new ArrayList<>();

                for (Object object : sqlRows){
                    Map map = (Map) object;
                    row = new JobTableRow();

                    Set keySet = map.keySet();
                    Iterator iterator = keySet.iterator();
                    while (iterator.hasNext()){
                        if (row.getColumn1() == null) row.setColumn1(String.valueOf(map.get(iterator.next())));
                        if (row.getColumn2() == null) row.setColumn2(String.valueOf(map.get(iterator.next())));
                    }
                    tableRows.add(row);
                }
                job.setName(job.getName()+" "+tableRows.size());
                return tableRows;
            }
        };
    }
}
