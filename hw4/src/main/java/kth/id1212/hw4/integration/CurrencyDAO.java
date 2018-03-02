package kth.id1212.hw4.integration;

import kth.id1212.hw4.model.Currency;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager; 

/**
 *
 * @author Anton
 * @author Benjamin
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class CurrencyDAO {
    @PersistenceContext(unitName = "converterPU")
    private EntityManager em;
    
    public Currency getCurrency(String abbreviation){
        Currency currency = em.find(Currency.class, abbreviation);
        return currency;
    }
    
    public void updateCurrency(Currency currency){
        em.merge(currency);
        em.flush();
    }
}
