package magazynier;

import magazynier.entities.VatRate;
import org.junit.Test;

import java.util.ArrayList;

public class VatRateTest {
    @Test
    public void test() {
        ArrayList<VatRate> ar = DAO.readTable("VatRate");
        for (VatRate vr : ar) {
            //System.out.println(ar);
        }
    }
}
