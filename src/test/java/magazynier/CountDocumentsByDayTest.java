package magazynier;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class CountDocumentsByDayTest {
    @Test
    public void test() {
        assertTrue(DAO.countDocumentsByDay(LocalDate.now()) >= 0);
    }
}
