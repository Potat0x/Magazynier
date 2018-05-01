package magazynier;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class CRUDTest {

    private static Warehouse warehouse;

    @BeforeClass
    public static void createWarehouse() {
        warehouse = new Warehouse();
    }

    @Test
    public void test() {
        create();
        read();
        update();
        delete();
    }

    private void create() {
        Session session = HibernateSessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        warehouse = new Warehouse("_test_warehouse_create_");
        session.save(warehouse);
        transaction.commit();
        session.close();
    }

    private void read() {
        Session session = HibernateSessionFactory.openSession();
        
        //noinspection unchecked
        List<Warehouse> wh = session.createQuery("from Warehouse where id = " + warehouse.getId()).list();
        if (wh.isEmpty()) {
            fail("warehouses list empty");
        } else {
            System.out.println("assert: ");
            System.out.println(wh.get(0));
            System.out.println(warehouse);
            assertEquals(wh.get(0), warehouse);
        }
        session.close();
    }

    private void update() {
        Session session = HibernateSessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String newName = warehouse.getName() + "update_";
        warehouse.setName(newName);
        session.update(warehouse);
        transaction.commit();

        //noinspection unchecked
        List<Warehouse> wh = session.createQuery("from Warehouse where id = " + warehouse.getId()).list();
        if (wh.isEmpty()) {
            fail("warehouses list empty");
        } else {
            assertEquals(wh.get(0), warehouse);
            assertEquals(wh.get(0).getName(), newName);
        }
        session.close();
    }

    private void delete() {
        Session session = HibernateSessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(warehouse);
        if (!session.createQuery("from Warehouse where id = " + warehouse.getId()).list().isEmpty()) {
            fail("delete fail");
        }
        transaction.commit();
        session.close();
    }

}
