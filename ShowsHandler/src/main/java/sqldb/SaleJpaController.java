package sqldb;

import controller.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import model.Sale;

/**
 * @author Martin Ramonda
 */
public class SaleJpaController implements Serializable {

    public SaleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public SaleJpaController(){
        this.emf = Persistence.createEntityManagerFactory("salesPersistence");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sale sale) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(sale);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sale sale) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            sale = em.merge(sale);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                long id = sale.getId();
                if (findSale(id) == null) {
                    throw new NonexistentEntityException("The sale with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sale sale;
            try {
                sale = em.getReference(Sale.class, id);
                sale.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sale with id " + id + " no longer exists.", enfe);
            }
            em.remove(sale);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sale> findSaleEntities() {
        return findSaleEntities(true, -1, -1);
    }

    public List<Sale> findSaleEntities(int maxResults, int firstResult) {
        return findSaleEntities(false, maxResults, firstResult);
    }

    private List<Sale> findSaleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sale.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Sale findSale(long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sale.class, id);
        } finally {
            em.close();
        }
    }

    public int getSaleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sale> rt = cq.from(Sale.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public List<Sale> findSalesByEvent(String eventName) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Sale.findByEvent", Sale.class)
                     .setParameter("eventName", eventName)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Sale> findSalesByUser(String username) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Sale.findByUser", Sale.class)
                     .setParameter("user", username)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Sale> findSalesByMonth(int month) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Sale.findByMonth", Sale.class)
                     .setParameter("month", month)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Sale> findSalesByYear(int year) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Sale.findByYear", Sale.class)
                     .setParameter("year", year)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Sale> findSalesByPriceGreaterThan(double price) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Sale.findByPriceGreaterThan", Sale.class)
                     .setParameter("price", price)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
