package magazynier;

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
            System.out.println("update failed");
            e.printStackTrace();
        }
        return whl;
    }

    public static void update(Object object) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.update(object);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            System.out.println("update failed");
            e.printStackTrace();
        }
    }

    public static void add(Object object) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.save(object);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            System.out.println("add failed");
            e.printStackTrace();
        }
    }

    public static void delete(Object object) throws HibernateException {
        Session session = HibernateSessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        session.delete(object);
        tr.commit();
        session.close();
    }

    public static boolean checkIfExistsById(Class c, Integer id) {
        Object object = new Object();
        Session session = HibernateSessionFactory.openSession();
        Object obj = session.get(c, id);
        session.close();
        return obj != null;
    }
}
