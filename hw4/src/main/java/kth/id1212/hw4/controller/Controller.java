package kth.id1212.hw4.controller;

import kth.id1212.hw4.integration.CurrencyDAO;
import kth.id1212.hw4.model.CurrencyDTO;
import kth.id1212.hw4.model.Currency;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.Stateless;
import javax.ejb.EJB;

/**
 *
 * @author Anton
 * @author Benjamin
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {
    @EJB CurrencyDAO currencyDB;
    
    public CurrencyDTO updateCurrency(String abbreviation, float conversionRate){
        Currency currency = new Currency(abbreviation, conversionRate);
        currencyDB.updateCurrency(currency);
        return currency;
    }
    
    
    public CurrencyDTO getCurrency(String abbreviation){
        return currencyDB.getCurrency(abbreviation);
    }
    
    public float convert(CurrencyDTO haveCurrency, CurrencyDTO wantCurrency, float amount){
        
        float value = amount*(haveCurrency.getRate()/wantCurrency.getRate());
        return value;
    }
}
