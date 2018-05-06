package magazynier;

import javassist.NotFoundException;
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

    public void updateItem(Item item) throws NotFoundException {
        DAO.update(item);
    }

    public void addItem(Item item) {
        DAO.add(item);
    }

    public void deleteItem(Item item) throws NotFoundException {
        DAO.delete(item);
    }
}
