package magazynier;

import magazynier.entities.Warehouse;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

class WarehousesModel {

    ArrayList getWarehousesList() {

        ArrayList whl = new ArrayList();

        try (Session session = HibernateSessionFactory.openSession()) {
            whl = (ArrayList) session.createQuery("from Warehouse").list();
            session.close();
        } catch (HibernateException e) {
            System.out.println("update failed");
            e.printStackTrace();
        }
        return whl;
    }

    void updateWarehouse(Warehouse warehouse) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.update(warehouse);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            System.out.println("update failed");
            e.printStackTrace();
        }
    }
}
