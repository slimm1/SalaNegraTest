package utilities;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import model.Sale;

 /**
 * @author Martin Ramonda
 */
public class ReportWriter {
    public static String writeSalesReport(List<Sale> salesList){
        int count = 1;
        double reportTotal = 0;
        StringBuilder out = new StringBuilder();
        out.append("---------------[ SALES ]---------------");
        out.append(System.getProperty("line.separator"));
        for(Sale sale : salesList){
            out.append(count);
            out.append(". SALE DETAIL :");
            out.append(System.getProperty("line.separator"));
            out.append("\tUSER: ");
            out.append(sale.getUser());
            out.append(System.getProperty("line.separator"));
            out.append("\tEVENT: ");
            out.append(sale.getEvent());
            out.append(System.getProperty("line.separator"));
            out.append("\tSALE DATETIME: ");
            out.append(sale.getSaleDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
            out.append(System.getProperty("line.separator"));
            out.append("\tTICKETS PURCHASED: ");
            out.append(sale.getNumTickets());
            out.append(System.getProperty("line.separator"));
            out.append("\tTOTAL EARNED: ");
            out.append(sale.getTotalPrice());
            out.append(System.getProperty("line.separator"));
            reportTotal+=sale.getTotalPrice();
            count++;
        }
        out.append("TOTAL EARNED: ");
        out.append(reportTotal);
        out.append("€");
        return out.toString();
    }
}
