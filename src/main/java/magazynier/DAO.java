package magazynier;

import magazynier.utils.Indexed;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

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
            Transaction tr = session.beginTransaction();
            if (checkIfExists(session, object)) {
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
                tr.commit();
            } else {
                throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
            }
        }
    }

    public static void refresh(Indexed object) throws RowNotFoundException {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            if (checkIfExists(session, object)) {
                session.refresh(object);
                tr.commit();
            } else {
                throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
            }
        }
    }

    private static boolean checkIfExists(Session session, Indexed object) {

        String className = object.getClass().getSimpleName();

        if (object instanceof HibernateProxy) {
            className = Hibernate.getClass(object).getSimpleName();
        }

        Query query = session.createQuery("select 'exists' from " + className + " c where c.id = :id");
        query.setProperties(object);
        return query.uniqueResult() != null;
    }

    public static Integer countDocumentsByDay(LocalDate day) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Document wrapper = new Document();
            wrapper.setDate(Date.valueOf(day));
            Query query = session.createQuery("select count(*) from Document d where d.date = :date");
            query.setProperties(wrapper);
            return ((Long) query.uniqueResult()).intValue();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //assortment DAO<>
    public static void delete(Integer id) {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            Query query = session.createQuery("delete from Assortment d where d.documentItemId = " + id);//todo: prepared
            query.executeUpdate();
            tr.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    public static Object findWarehouseIdByDocItemId(Integer documentItemId) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Query query = session.createQuery("select warehouseId from Assortment d where d.documentItemId = " + documentItemId);//todo: prepared
            return query.uniqueResult();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveOrUpdate(Object object) {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.saveOrUpdate(object);
            tr.commit();
        }
    }

    public static void updateDocument(Indexed object, Set<DocumentItem> deletedItems) throws RowNotFoundException {

        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            if (checkIfExists(session, object)) {
                //deletedItems.forEach(di -> delete(di.getId()));
                deletedItems.forEach(di -> {
                    Query query = session.createQuery("delete from Assortment d where d.documentItemId = " + di.getId());//todo: prepared
                    query.executeUpdate();
                });
                session.update(object);
                tr.commit();
            } else {
                throw new RowNotFoundException(object.getClass() + " object:" + object + " does not exist in database.");
            }
        }
    }
    //assortment DAO</>
}
