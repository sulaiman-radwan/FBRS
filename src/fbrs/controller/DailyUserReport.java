package fbrs.controller;

import fbrs.model.DatabaseModel;
import fbrs.model.Entry;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DailyUserReport implements Initializable {
    //UI
    public TableColumn<LocalDate, String> date;
    public TableView<LocalDate> table;
    public BorderPane rootPane;

    public TableView<LocalDate> sumTable;
    public TableColumn<LocalDate, String> sumColumn;

    private DatabaseModel model;
    private Map<LocalDate, Map<Integer, Integer>> entriesMap;
    private Map<Integer, Long> sumMap;
    private FilteredList<LocalDate> dates;
    private Set<Integer> types;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = DatabaseModel.getModel();
        entriesMap = new HashMap<>();
        sumMap = new HashMap<>();
        types = new HashSet<>();

        sumTable.setPlaceholder(new Label(""));

        date.setCellValueFactory((param -> new SimpleStringProperty(param.getValue().toString())));

        table.setEditable(true);
    }

    public void showReport(FilteredList<Entry> entries) {
        LocalDate date;
        for (Entry entry : entries) {
            types.add(entry.getType());
            date = entry.getDateCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            entriesMap.putIfAbsent(date, new HashMap<>());
            entriesMap.get(date).put(entry.getType(),
                    entriesMap.get(date).getOrDefault(entry.getType(), 0) + entry.getQuantity());
            sumMap.put(entry.getType(), sumMap.getOrDefault(entry.getType(), 0L) + entry.getQuantity());
        }
        ObservableList<LocalDate> observableList = FXCollections.observableArrayList();
        dates = new FilteredList<>(observableList);
        observableList.addAll(entriesMap.keySet());

        TableColumn<LocalDate, String> column;
        for (int type : types) {
            addColumnToTable(table, type);
            column = new TableColumn<>(String.valueOf(sumMap.getOrDefault(type, 0L)));
            column.setSortable(false);
            column.setPrefWidth(120);
            sumTable.getColumns().add(column);
        }

        table.setItems(dates);

        SortedList<LocalDate> sortedList = new SortedList<>(dates);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

    }

    private void addColumnToTable(TableView<LocalDate> tableView, int entryType) {

        TableColumn<LocalDate, Integer> column = new TableColumn<>(model.getEntryTypeName(entryType));

        column.setCellValueFactory(param -> new ObservableValue<Integer>() {

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

            @Override
            public void addListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public Integer getValue() {
                return entriesMap.get(param.getValue()).getOrDefault(entryType, 0);
            }
        });

        column.setPrefWidth(120);
        tableView.getColumns().add(column);
    }

}
