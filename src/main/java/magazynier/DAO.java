package magazynier;

import magazynier.utils.Indexed;
import magazynier.worker.Worker;
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

    public static Double getAvailableQuantityInWarehouse(Integer itemId, Integer warehouseId) {

        if (itemId != null && warehouseId != null) {
            try (Session session = HibernateSessionFactory.openSession()) {
                String qs = "select sum(di.quantity * di.transactionSign) " +
                        "FROM DocumentItem as di " +
                        "JOIN Assortment as a on di.id = a.documentItemId " +
                        "JOIN Item as i on i.id = di.item.id " +
                        "where a.warehouseId = " + warehouseId + " " +
                        "and i.id = " + itemId + " " +
//                    "where a.warehouseId = 3 and i.id = 4 " +
//                    "where a.warehouseId = :id and i.id = :id " +
                        "GROUP BY i.id, a.warehouseId";
//                    "having di.item.id = 4";

                Query query = session.createQuery(qs);
//            query.setProperties(warehouse);
//            query.setProperties(docItem.getItem());
                Object o = query.uniqueResult();
                if (o != null) {
                    return ((Double) o).doubleValue();
                } else {
                    return -3.79;
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return -4.99;
    }

    public static Double getAvailableQuantity(Integer itemId) {
        if (itemId != null) {
            try (Session session = HibernateSessionFactory.openSession()) {
                String qs = "select sum(di.quantity * di.transactionSign) " +
                        "FROM DocumentItem as di " +
                        "where di.item.id = " + itemId;

                Query query = session.createQuery(qs);
                Object o = query.uniqueResult();
                System.out.println("o = " + o);
                if (o != null) {
                    return ((Double) o).doubleValue();
                } else {
                    return 0.0;
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public static Double getWarehouseGrossValue(Integer warehouseId) {
        if (warehouseId != null) {

            try (Session session = HibernateSessionFactory.openSession()) {
                /*String qs = "select sum(di.quantity * di.price +2) " +
                        "FROM Assortment as a " +
                        "JOIN DocumentItem as di on di.item.id = a.documentItemId group by a.warehouseId";
                */
                String qs = "select sum(di.quantity * di.price) " +
                        "FROM Assortment as a " +
                        "JOIN DocumentItem as di on di.id = a.documentItemId " +
                        "where a.warehouseId = " + warehouseId;

                Query query = session.createQuery(qs);
                Object o = query.uniqueResult();
                System.out.println("o = " + o);
                if (o != null) {
                    return ((Double) o).doubleValue();
                } else {
                    return 0.0;
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public static Double getWarehouseNetValue(Integer warehouseId) {
        if (warehouseId != null) {

            try (Session session = HibernateSessionFactory.openSession()) {
                String qs = "select sum(di.quantity * di.price * ((100.0 - di.tax) / 100.0)) " +
                        "FROM Assortment as a " +
                        "JOIN DocumentItem as di on di.id = a.documentItemId " +
                        "where a.warehouseId = " + warehouseId;

                Query query = session.createQuery(qs);
                Object o = query.uniqueResult();
                System.out.println("o = " + o);
                if (o != null) {
                    return ((Double) o).doubleValue();
                } else {
                    return 0.0;
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }
    //assortment DAO</>

    public static ArrayList<Message> getMessagesBetween(Worker person1, Worker person2) {

        int p1 = person1.getId();
        int p2 = person2.getId();

        String qs = "FROM Message where " +
                "(sender.id = " + p1 + " and recipient.id = " + p2 + ")" +
                " or " +
                "(sender.id = " + p2 + " and recipient.id = " + p1 + ")";
        try (Session session = HibernateSessionFactory.openSession()) {
            Query query = session.createQuery(qs);
            Object o = query.list();
            System.out.println("o = " + o);
            return (ArrayList<Message>) o;
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static ArrayList<MessageNotification> getNotificationsList(Worker thisWorker) {

        String qs = "from MessageNotification where recipient.id = " + thisWorker.getId() + "";
        try (Session session = HibernateSessionFactory.openSession()) {
            Query query = session.createQuery(qs);
            Object o = query.list();
            System.out.println("o = " + o);
            return (ArrayList<MessageNotification>) o;
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
