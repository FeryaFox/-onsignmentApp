package ru.feryafox.consignmentapp.ProductRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ProductRepository {

    private SessionFactory sessionFactory;

    public ProductRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addProduct(Product product) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(product);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        System.out.println(product);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(product);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product getProduct(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Product.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Product", Product.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteProducts(List<Product> products) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            for (Product product : products) {
                session.delete(product);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}