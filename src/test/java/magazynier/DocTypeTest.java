package magazynier;

import org.junit.Test;

public class DocTypeTest {
    @Test
    public void readTest() {
        DAO.readTable("DocumentType").stream().forEach(dt -> System.out.println(dt));
    }
}
