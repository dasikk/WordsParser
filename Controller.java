package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class Controller{

    private File file_to_parse;

    @FXML
    public Button start_button;

    @FXML
    private Label info_label;

    @FXML
    private TableView<Pairs> result_table;

    @FXML
    private TableColumn<Pairs, String> word_column;

    @FXML
    private TableColumn<Pairs, Integer> num_column;

    @FXML
    private ProgressBar progress_bar;

    @FXML
    private void addFile()
    {
       FileChooser filechooser = new FileChooser();
       filechooser.setTitle("Select File");
       ExtensionFilter filter = new ExtensionFilter("TEXT FILES", "*.txt", "*.csv", "*.md",
               "*.log", "*.xml", "*.dat");
       filechooser.getExtensionFilters().add(filter);
       File file = filechooser.showOpenDialog(new Stage());
       if(file != null)
       {
            info_label.setText(file.getName());
            file_to_parse = file;
       }
    }

    @FXML
    private void parseFile() {
        if(file_to_parse == null)
        {
            info_label.setText("File is not selected!\nSelect File");
            return;
        }
        if(Parser.getIsRunning() == Boolean.TRUE)
        {
            Parser.setT_exit();
            return;
        }
        Parser worker = new Parser(result_table, file_to_parse);
        progress_bar.progressProperty().bind( worker.progressProperty());
        start_button.textProperty().bind(worker.messageProperty());
        word_column.setCellValueFactory(new PropertyValueFactory<>("word"));
        num_column.setCellValueFactory(new PropertyValueFactory<>("amount"));
        ChangeListener<Integer> listener_progress = (observable, oldValue, newValue) -> {
        };
        ChangeListener<String> listener_button = (observable, oldValue, newValue) -> {
        };
        worker.valueProperty().addListener( listener_progress );
        worker.messageProperty().addListener(listener_button);
        new Thread(worker).start();
    }
}
class Parser extends Task<Integer> {
    private final File file_to_parse;

    private final TableView<Pairs> result_list;

    private static boolean is_running;
    private static boolean exit_thread = Boolean.FALSE;


    public static void setT_exit()
    {
        exit_thread = Boolean.TRUE;
    }

    public static boolean getIsRunning() { return is_running; }

    public Parser(TableView<Pairs> list, File file)
    {
        file_to_parse = file;
        result_list = list;
    }

    public Integer call()
    {
        is_running = Boolean.TRUE;
        try {
            File file_to_read = new File(String.valueOf(file_to_parse));
            Scanner myReader = new Scanner(file_to_read);
            List<Pairs> words = new ArrayList<>();
            boolean word_was_found = Boolean.FALSE;
            double max_work = file_to_read.length();
            double current_work = 0;

            //clear TableView, not to mix result from different files
            for ( int i = 0; i<result_list.getItems().size(); i++) {
                result_list.getItems().clear();
            }

            //some updates for Listeners
            updateProgress(0, 0);
            updateMessage("Cancel");

            //finding words and adding them to TableView
            while(myReader.hasNext())
            {
                //cancel thread if user pressed cancel
                if (exit_thread == Boolean.TRUE) {
                    exit_thread = Boolean.FALSE;
                    is_running = Boolean.FALSE;
                    updateMessage("Start");
                    return 0;
                }

                String data = myReader.next();
                current_work += data.length();
                updateProgress(current_work, max_work);

                for(Pairs obj : words)
                {
                    if(obj.getWord().equals(data))
                    {
                        obj.add_counter();
                        word_was_found = Boolean.TRUE;
                        break;
                    }
                }
                if(!word_was_found)
                {
                    words.add(new Pairs(data));
                }
                word_was_found = Boolean.FALSE;
            }
            updateProgress(max_work, max_work);
            for(Pairs obj : words)
            {
                result_list.getItems().add(obj);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        is_running = Boolean.FALSE;
        updateMessage("Start");
        return 0;
    }
}

