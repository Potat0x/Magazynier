package magazynier;

import org.junit.Test;

public class CalculateIncomeTest {
    @Test
    public void noExceptionTest() {

        for (IncomeType inct : IncomeType.values()) {
            DAO.calculateIncome(-1, inct);
            DAO.calculateIncome(0, inct);
            DAO.calculateIncome(1, inct);
        }
    }
}
