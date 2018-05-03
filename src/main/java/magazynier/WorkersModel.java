package magazynier;

import magazynier.entities.Worker;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

public class WorkersModel {

    ArrayList getWorkersList() {

        ArrayList whl = new ArrayList();

        try (Session session = HibernateSessionFactory.openSession()) {
            whl = (ArrayList) session.createQuery("from Worker").list();
            session.close();
        } catch (HibernateException e) {
            System.out.println("getWorkersList failed");
            e.printStackTrace();
        }
        return whl;
    }

    void updateWorker(Worker worker) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.update(worker);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            System.out.println("update failed");
            e.printStackTrace();
        }
    }

    public void addWorker(Worker newWorker) {
        try (Session session = HibernateSessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.save(newWorker);
            tr.commit();
            session.close();
        } catch (HibernateException e) {
            System.out.println("add failed");
            e.printStackTrace();
        }
    }

    public void deleteWorker(Worker worker) throws HibernateException {
        Session session = HibernateSessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        session.delete(worker);
        tr.commit();
        session.close();
    }
}

