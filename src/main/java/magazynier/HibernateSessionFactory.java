package magazynier;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {

    static final SessionFactory sessionFactory = build();

    private HibernateSessionFactory() {
    }

    private static SessionFactory build() {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate/hibernate.cfg.xml");
        return cfg.buildSessionFactory();
    }

    static Session openSession() {
        return sessionFactory.openSession();
    }

    public static void close() {
        sessionFactory.close();
    }

}
