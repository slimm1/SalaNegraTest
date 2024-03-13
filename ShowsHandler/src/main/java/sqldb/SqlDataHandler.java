package sqldb;

import controller.SaleJpaController;
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
}
