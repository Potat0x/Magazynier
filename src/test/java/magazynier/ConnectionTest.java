package magazynier;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static junit.framework.TestCase.fail;

public class ConnectionTest {
    @Test
    public void connectionTest() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "magazynier", "m1234");

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT sysdate FROM dual");

            if (rs.next()) {
                //System.out.println(rs.getString(1));
            } else {
                fail("select sysdate fail");
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            //e.printStackTrace();
            fail(e.getClass().getSimpleName());
        }
    }
}
