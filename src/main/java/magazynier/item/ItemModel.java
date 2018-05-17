package magazynier.item;

import magazynier.DAO;
import magazynier.RowNotFoundException;
import magazynier.item.Item;

import java.util.ArrayList;

public class ItemModel {

    private static ArrayList VatList = DAO.readTable("VatRate");
    private static ArrayList MeasurementUnitList = DAO.readTable("MeasurementUnit");

    public static ArrayList getVatList() {
        return VatList;
    }

    public static ArrayList getMeasurementUnitsList() {
        return MeasurementUnitList;
    }

    public ArrayList getItemsList() {
        return DAO.readTable("Item");
    }

    public void updateItem(Item item) throws RowNotFoundException {
        DAO.update(item);
    }

    public void addItem(Item item) {
        DAO.add(item);
    }

    public void deleteItem(Item item) throws RowNotFoundException {
        DAO.delete(item);
    }
}
