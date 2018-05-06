package magazynier;

import magazynier.entities.Item;

import java.util.ArrayList;

public class ItemModel {

    private static ArrayList VatList = DAO.readTable("VatRate");

    public static ArrayList getVatList() {
        return VatList;
    }

    ArrayList getItemsList() {
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
