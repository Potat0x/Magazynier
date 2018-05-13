package magazynier;

import magazynier.item.VatRate;
import org.junit.Test;

import java.util.ArrayList;

public class DocItemsReadingTest {
    @Test
    public void test() {
        ArrayList<DocumentItem> l = DAO.readTable("DocumentItem");
    }
}
