package magazynier.item;

import magazynier.DAO;
import magazynier.RowNotFoundException;
import magazynier.item.Item;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class ItemModel {

    private static ArrayList VatList = DAO.readTable("VatRate");
    private static ArrayList MeasurementUnitList = DAO.readTable("MeasurementUnit");
    private static ArrayList ItemsList = DAO.readTable("Item");

    public static ArrayList getVatList() {
        return VatList;
    }

    public static ArrayList getMeasurementUnitsList() {
        return MeasurementUnitList;
    }

    public ArrayList<Item> getItemsList() {
        return ItemsList;
    }

    public void updateItem(Item item) throws RowNotFoundException {
        DAO.update(item);
        ItemsList.replaceAll((UnaryOperator<Item>) i -> i.getId().equals(item.getId()) ? item : i);
    }

    public void addItem(Item item) {
        DAO.add(item);
    }

    public void deleteItem(Item item) throws RowNotFoundException {
        DAO.delete(item);
    }
}
