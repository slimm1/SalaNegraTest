package tablemodel;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Sale;

public class SaleTableModel extends AbstractTableModel {
    private List<Sale> sales;
    private String[] columnNames = {"ID", "User", "Event", "Sale Date Time", "Number of Tickets", "Total Price"};

    public SaleTableModel(List<Sale> sales) {
        this.sales = sales;
    }

    @Override
    public int getRowCount() {
        return sales.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Sale sale = sales.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return sale.getId();
            case 1:
                return sale.getUser();
            case 2:
                return sale.getEvent();
            case 3:
                return sale.getSaleDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            case 4:
                return sale.getNumTickets();
            case 5:
                return sale.getTotalPrice();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}

