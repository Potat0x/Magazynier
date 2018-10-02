package magazynier.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class PropertyTableFilter<T> {

    private ArrayList<Pair<TextField, Function<T, String>>> textFilters;
    private ArrayList<Pair<Pair<DatePicker, DatePicker>, Function<T, Date>>> dateRangeFilters;
    private ArrayList<Predicate<T>> checkboxFilters;

    private TableView<T> table;
    private FilteredList<T> filteredItems;
    private ChangeListener listener;

    public PropertyTableFilter(ArrayList<T> items, TableView<T> table) {
        this.table = table;
        textFilters = new ArrayList<>();
        dateRangeFilters = new ArrayList<>();
        checkboxFilters = new ArrayList<>();

        setItems(items);
        listener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                Predicate<T> substringPredicate = new Predicate<T>() {
                    @Override
                    public boolean test(T item) {
                        return textFilters.stream().allMatch(new Predicate<Pair<TextField, Function<T, String>>>() {
                            @Override
                            public boolean test(Pair<TextField, Function<T, String>> p) {
                                return p.getValue().apply(item) == null ||
                                        p.getValue().apply(item).toLowerCase().replaceAll("\n", " ").contains(p.getKey().getText().toLowerCase().trim());
                            }
                        });
                    }
                };

                Predicate<T> dateRangePredicate = new Predicate<T>() {
                    @Override
                    public boolean test(T item) {
                        return dateRangeFilters.stream().allMatch(new Predicate<Pair<Pair<DatePicker, DatePicker>, Function<T, Date>>>() {
                            @Override
                            public boolean test(Pair<Pair<DatePicker, DatePicker>, Function<T, Date>> p) {
                                LocalDate date = p.getValue().apply(item).toLocalDate();
                                LocalDate startDate = p.getKey().getKey().getValue();
                                LocalDate endDate = p.getKey().getValue().getValue();

                                boolean afterStart = startDate == null || startDate.isBefore(date) || startDate.isEqual(date);
                                boolean beforeEnd = endDate == null || endDate.isAfter(date) || endDate.isEqual(date);
                                return afterStart && beforeEnd;
                            }
                        });
                    }
                };

                Predicate<T> mainPredicate = substringPredicate.and(dateRangePredicate);

                if (!checkboxFilters.isEmpty()) {
                    Predicate<T> checkboxesUnionPredicate = o -> false;
                    for (Predicate<T> p : checkboxFilters) {
                        checkboxesUnionPredicate = checkboxesUnionPredicate.or(p);
                    }
                    mainPredicate = mainPredicate.and(checkboxesUnionPredicate);

                }

                filteredItems.setPredicate(mainPredicate);
            }
        };
    }

    public void setItems(ArrayList<T> items) {
        filteredItems = new FilteredList<>(FXCollections.observableArrayList(items), x -> true);

        SortedList<T> sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedItems);
    }

    public void tie(TextField propertyField, Function<T, String> propertyGetter) {
        textFilters.add(new Pair<>(propertyField, propertyGetter));
        propertyField.textProperty().addListener(listener);
    }

    public void tieWithRange(DatePicker startDatePicker, DatePicker endDatePicker, Function<T, Date> propertyGetter) {
        dateRangeFilters.add(new Pair<>(new Pair<>(startDatePicker, endDatePicker), propertyGetter));
        startDatePicker.valueProperty().addListener(listener);
        endDatePicker.valueProperty().addListener(listener);
    }

    public void tieWithCheckBox(CheckBox checkBox, Predicate<T> checkBoxToPropertyPredicate) {
        checkboxFilters.add(checkBoxToPropertyPredicate);
        checkBox.selectedProperty().addListener(listener);
    }
}
