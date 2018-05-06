package magazynier;

import magazynier.utils.Indexed;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

public class DAO {

    public static ArrayList readTable(String tableName) {

        ArrayList whl = new ArrayList();

        try (Session session = HibernateSessionFactory.openSession()) {
            whl = (ArrayList) session.createQuery("from " + tableName).list();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return whl;
    }

    public static void add(Object object) {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.save(object);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    public static void update(Indexed object) throws RowNotFoundException {

        if (checkIfExistsById(object.getClass(), object.getId())) {
            try (Session session = HibernateSessionFactory.openSession()) {
                Transaction tr = session.beginTransaction();
                session.update(object);
                tr.commit();
                session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        } else {
            throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
        }
    }

    public static void delete(Indexed object) throws RowNotFoundException {

        if (checkIfExistsById(object.getClass(), object.getId())) {
            Session session = HibernateSessionFactory.openSession();
            Transaction tr = session.beginTransaction();
            session.delete(object);
            tr.commit();
            session.close();
        } else {
            throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
        }
    }

    private static boolean checkIfExistsById(Class c, Integer id) {
        Object object = new Object();
        Session session = HibernateSessionFactory.openSession();
        Object obj = session.get(c, id);
        session.close();
        return obj != null;
    }
}
