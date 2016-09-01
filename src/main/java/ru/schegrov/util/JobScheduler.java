package ru.schegrov.util;


import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.log4j.Logger;
import ru.schegrov.dao.JobDao;
import ru.schegrov.entity.Job;
import ru.schegrov.entity.JobTableRow;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by ramon on 08.08.2016.
 */
public class JobScheduler extends ScheduledService<Job> {

    private static final Logger logger = Logger.getLogger(JobScheduler.class);

    private Job job;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobScheduler(Job job) {
        this.job = job;
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
                List<Map<String, Object>> sqlRows = reader.getAllRows(job.getCondition("SQL").getValue());
                boolean isFirstRow = true;
                for (Map<String, Object> sqlRow : sqlRows) {
                    JobTableRow row = new JobTableRow();
                    Set<Map.Entry<String, Object>> sqlColumns = sqlRow.entrySet();
                    Iterator<Map.Entry<String, Object>> iterator = sqlColumns.iterator();
                    int columnNameIndex = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> sqlColumn = iterator.next();

                        if (sqlColumn.getValue() instanceof Timestamp){
                            Timestamp value = (Timestamp) sqlColumn.getValue();
                            sqlColumn.setValue(DateFormatHelper.format(value));
                        }

                        row.setColumn(sqlColumn.getValue().toString());

                        if (isFirstRow){
                            TableColumn column = new TableColumn(sqlColumn.getKey());
                            column.setCellFactory(TextFieldTableCell.forTableColumn());
                            column.setCellValueFactory(new PropertyValueFactory("column" + ++columnNameIndex));
                            job.getColumns().add(column);
                        }
                    }
                    job.getRows().add(row);
                    isFirstRow = false;
                }
                job.setCount(job.getRows().size());
                updateValue(job);
                return job;
            }
        };
    }
}
