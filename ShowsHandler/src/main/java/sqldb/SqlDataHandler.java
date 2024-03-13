package sqldb;

import java.util.List;
import model.Sale;

/**
 * @author Martin Ramonda
 */
public class SqlDataHandler {
    
    private static SqlDataHandler instance;
    
    SaleJpaController salesController;
    
    private SqlDataHandler(){
        this.salesController = new SaleJpaController();
    }
    
    public static SqlDataHandler getInstance(){
        if(instance == null){
            instance = new SqlDataHandler();
        }
        return instance;
    }
    
    public boolean insertSale(Sale sale){
        try{
            salesController.create(sale);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public List<Sale> getAll(){
        return salesController.findSaleEntities();
    }
    
    public List<Sale> findByEvent(String eventName){
        return salesController.findSalesByEvent(eventName);
    }
    
    public List<Sale> findByCategory(String username){
        return salesController.findSalesByUser(username);
    }
    
    public List<Sale> findByMonth(int month){
        return salesController.findSalesByMonth(month);
    }
    
    public List<Sale> findByYear(int year){
        return salesController.findSalesByYear(year);
    }     

    public List<Sale> findByPriceGreaterThan(double price){
        return salesController.findSalesByPriceGreaterThan(price);
    }  
}
