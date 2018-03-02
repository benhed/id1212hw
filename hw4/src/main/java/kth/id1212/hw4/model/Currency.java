package kth.id1212.hw4.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *
 * @author Anton
 * @author Benjamin
 */
@Entity
public class Currency implements CurrencyDTO, Serializable{
    @Id
    private String abbr;
    private float rate;
    
    public Currency(){}
    
    public Currency(String abbr, float rate){
        this.abbr = abbr;
        this.rate = rate;
    }

    @Override
    public String getAbbr(){ return abbr; }
    
    @Override
    public float getRate(){ return rate; }  
}
