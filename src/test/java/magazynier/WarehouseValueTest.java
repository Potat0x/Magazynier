package magazynier;

import magazynier.warehouse.Warehouse;
import org.junit.Test;

import java.util.ArrayList;

public class WarehouseValueTest {
    @Test
    public void test() {
        ArrayList<Warehouse> warehouses = DAO.readTable("Warehouse");
        warehouses.stream().forEach(w -> System.out.println(w.getId() + " -> " + DAO.getWarehouseGrossValue(w.getId())));
    }
}
