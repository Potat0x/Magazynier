package magazynier.item;

import magazynier.DAO;
import magazynier.IncomeType;
import magazynier.MeasurementUnit;
import magazynier.RowNotFoundException;
import magazynier.utils.NullableCalc;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class ItemModel {

    private static ArrayList<VatRate> VatList = DAO.readTable("VatRate");
    private static ArrayList<MeasurementUnit> MeasurementUnitList = DAO.readTable("MeasurementUnit");
    private static ArrayList ItemsList = DAO.readTable("Item");

    public static ArrayList<VatRate> getVatList() {
        return VatList;
    }

    public static ArrayList<MeasurementUnit> getMeasurementUnitsList() {
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

    public Double getAvailableQuantity(Item item) {

        if (item == null)
            return -1.99;
        else {
            System.out.println("ITEM: " + item.getId() + ", " + item.getName());
            return DAO.getAvailableQuantity(item.getId());
        }
    }

    public Double getItemTotalGrossValue(Item item) {
        return NullableCalc.multiplyNullable(DAO.getAvailableQuantity(item.getId()), item.getCurrentPrice());
    }

    public Double getItemTotalNetValue(Item item) {
        return NullableCalc.netValue(getItemTotalGrossValue(item), item.getVatRate().getTax());
    }

    public Double getItemRevenue(Item value) {
        return DAO.calculateIncome(value.getId(), IncomeType.REVENUE);
    }

    public Double getItemProfit(Item value) {
        return DAO.calculateIncome(value.getId(), IncomeType.PROFIT);
    }
}
