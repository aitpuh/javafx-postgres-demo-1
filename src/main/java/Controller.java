import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.postgresql.ds.PGSimpleDataSource;

import javax.swing.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class Controller {

    @FXML
    private TextField description;

    @FXML
    private DatePicker dueDate;

    @FXML
    private Button addButton;

    @FXML
    private TableView<Task> tasksView;

    private PGSimpleDataSource pgSimpleDataSource;

    public void initialize() throws SQLException {
       pgSimpleDataSource = new PGSimpleDataSource();
       pgSimpleDataSource.setServerName("jangels.bounceme.net");
       pgSimpleDataSource.setUser("demo_user");
       pgSimpleDataSource.setPassword("cougar2017");
       pgSimpleDataSource.setDatabaseName("java_db");

       dueDate.setValue(LocalDate.now());
       reload(null);
       //adding a random comment
    }


    @FXML
    public void reload(ActionEvent event) throws SQLException {

        int idx = 0;
        if (tasksView.getSelectionModel().getSelectedIndex() > -1 ) {
            idx = tasksView.getSelectionModel().getSelectedIndex();
        }
        QueryRunner queryRunner = new QueryRunner(pgSimpleDataSource);
        ResultSetHandler<List<Task>> h = new BeanListHandler<Task>(Task.class);
        List<Task> tasks = queryRunner.query("SELECT * FROM tasks ORDER BY duedate asc", h);

        tasksView.setItems(FXCollections.observableArrayList(tasks));
        tasksView.getSelectionModel().select(idx);
    }

    public void insert() throws SQLException {
       QueryRunner queryRunner = new QueryRunner(pgSimpleDataSource);

       int inserts = queryRunner.update("INSERT INTO tasks (description, duedate) VALUES (?, ?)",
               description.getText(), dueDate.getValue());

       System.out.println("Successful inserts: " + inserts);

       reload(null);
    }

    @FXML
    public void markTask(MouseEvent event) throws  SQLException {
       if (event.getClickCount() % 2 == 0) {
           QueryRunner queryRunner = new QueryRunner(pgSimpleDataSource);

           Task t = tasksView.getSelectionModel().getSelectedItem();

           queryRunner.update("UPDATE tasks set complete = ? where task_id = ?", !t.isComplete(), t.getTask_id());
           reload(null);
       }

    }

}
