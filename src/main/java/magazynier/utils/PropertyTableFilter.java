package magazynier.utils;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Filtr pozwalajacy na filtrowanie tabeli w czasie rzeczywistym przez powiazanie pol tekstowych z polami elementow bedacych zawartoscia tabeli
 *
 * @param <T> typ zawartosci tabeli, ktora bedzie filtrowana
 * @author ziemniak
 */
public class PropertyTableFilter<T> {

    private ArrayList<Pair<TextField, Function<T, String>>> filters;

    private TableView<T> table;
    private FilteredList<T> filteredItems;
    private ChangeListener listener;

    /**
     * @param items elementy, ktore maja sie znalezc w tabeli
     * @param table tabela, ktora bedzie filtrowana
     */
    public PropertyTableFilter(ArrayList<T> items, TableView<T> table) {
        this.table = table;
        filters = new ArrayList<>();
        setItems(items);
        listener = (observable, oldValue, newValue) -> filteredItems.setPredicate(item -> filters.stream().allMatch(p -> p.getValue().apply(item) == null ||
                p.getValue().apply(item).toLowerCase().replaceAll("\n", " ").contains(p.getKey().getText().toLowerCase().trim())));
    }

    /**
     * Ustawia zawartosc tabli.
     * Zawartosc filtrowanej tabeli musi zostac ustawiona przez ta metode lub konstruktor. Nie moze byc ustawiana zewnetrznie.
     *
     * @param items zawartosc tabeli
     */
    public void setItems(ArrayList<T> items) {
        filteredItems = new FilteredList<>(FXCollections.observableArrayList(items), x -> true);

        SortedList<T> sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedItems);
    }

    /**
     * Ustawia powiazanie miedzy polem tekstowym a polem klasy przechowywanych obiektow
     *
     * @param propertyField  pole tekstowe
     * @param propertyGetter getter dla pola klasy
     */
    public void tie(TextField propertyField, Function<T, String> propertyGetter) {
        filters.add(new Pair<>(propertyField, propertyGetter));
        propertyField.textProperty().addListener(listener);
    }
}
