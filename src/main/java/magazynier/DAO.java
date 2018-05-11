package magazynier;

import magazynier.utils.Indexed;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    public static void update(Indexed object) throws RowNotFoundException {

        try (Session session = HibernateSessionFactory.openSession()) {
            if (checkIfExists(session, object)) {
                Transaction tr = session.beginTransaction();
                session.update(object);
                tr.commit();
            } else {
                throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
            }
        }
    }

    public static void delete(Indexed object) throws RowNotFoundException {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            if (checkIfExists(session, object)) {
                session.delete(object);
            } else {
                throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
            }
            tr.commit();
        }
    }

    private static boolean checkIfExists(Session session, Indexed object) {
        Query query = session.createQuery("select 'exists' from " + object.getClass().getSimpleName() + " c where c.id = :id");
        query.setProperties(object);
        return query.uniqueResult() != null;
    }
}
