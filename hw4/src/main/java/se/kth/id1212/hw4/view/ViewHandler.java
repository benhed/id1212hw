package se.kth.id1212.hw4.view;

import kth.id1212.hw4.controller.Controller;
import kth.id1212.hw4.model.CurrencyDTO;

import javax.inject.Named;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Conversation;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Inject;

/**
 *
 * @author Anton
 * @author Benjamin
 */
@Named("viewHandler")
@ConversationScoped
public class ViewHandler implements Serializable{
    @EJB
    private Controller con;
    private CurrencyDTO haveCurrency;
    private CurrencyDTO wantCurrency;
    private String haveCurrencyAbbreviation;
    private String wantCurrencyAbbreviation;   
    private float haveCurrencyAmount;
    private float convertedValue;
    @Inject
    private Conversation conversation;
    
    private void startConversation(){
        if(conversation.isTransient()){
            conversation.begin();
        }
    }
    
    private void stopConversation(){
        if(conversation.isTransient()){
            conversation.end();
        }
    }
    
    private void handleException(Exception e){
        stopConversation();
        e.printStackTrace(System.err);
    }

    public void findHaveCurrency(){
        try{
            startConversation();
            haveCurrency = con.getCurrency(haveCurrencyAbbreviation);
        } catch (Exception e){
            handleException(e);
        }
    }
    
    public void findWantCurrency(){
        try{
            startConversation();
            wantCurrency = con.getCurrency(wantCurrencyAbbreviation);
        } catch (Exception e){
            handleException(e);
        }
    }
    
    public void convert(){
        haveCurrency = con.getCurrency(haveCurrencyAbbreviation);
        wantCurrency = con.getCurrency(wantCurrencyAbbreviation);
        convertedValue = con.convert(haveCurrency, wantCurrency, haveCurrencyAmount);
    }
    
    public void updateDB(){
        try{
            startConversation();
            con.updateCurrency("SEK", 1);   
            con.updateCurrency("USD", 8.3f);
            con.updateCurrency("EUR", 10);
            con.updateCurrency("JPY", 0.077f);
            con.updateCurrency("MXN", 0.44f);
        } catch(Exception e){
            handleException(e);
        }
    }
        
    public CurrencyDTO getHaveCurrency(){
        return haveCurrency;
    }
    
    public CurrencyDTO getWantCurrency(){
        return wantCurrency;
    }
    
    public float getHaveCurrencyAmount(){
        return haveCurrencyAmount;
    }
    
    public float getConvertedValue(){
        return convertedValue;
    }
    
    public String getWantCurrencyAbbreviation(){
        return null;
    }  
    
    public String getHaveCurrencyAbbreviation(){
        return null;
    }  
     
    public void setHaveCurrencyAmount(float haveCurrencyAmount){
        this.haveCurrencyAmount = haveCurrencyAmount;
    }

    public void setConvertedValue(float convertedValue){
        this.convertedValue = convertedValue;
    }

    public void setHaveCurrencyAbbreviation(String haveCurrencyAbbreviation){
        this.haveCurrencyAbbreviation = haveCurrencyAbbreviation;
    }

    public void setWantCurrencyAbbreviation(String wantCurrencyAbbreviation){
        this.wantCurrencyAbbreviation = wantCurrencyAbbreviation;
    }
}
