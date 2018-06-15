package magazynier;

import org.junit.Test;

public class CalculateTotalTransactionsValTest {
    @Test
    public void noExceptionTest() {
        DAO.calculateTotalTransactionsValue(-1);
        DAO.calculateTotalTransactionsValue(0);
        DAO.calculateTotalTransactionsValue(1);
    }
}
