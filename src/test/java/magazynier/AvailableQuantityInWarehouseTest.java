package magazynier;

import org.junit.Test;

public class AvailableQuantityInWarehouseTest {
    @Test
    public void test() {
        System.out.println(DAO.getAvailableQuantityInWarehouse(null, null));
    }
}
