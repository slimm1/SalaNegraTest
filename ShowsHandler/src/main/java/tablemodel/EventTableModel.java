package tablemodel;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Event;

public class EventTableModel extends AbstractTableModel {
    private List<Event> events;
    private String[] columnNames = {"ID", "Title", "Start Date Time", "Finish Date Time", "Description", "URL", "Categories", "Price"};

    public EventTableModel(List<Event> events) {
        this.events = events;
    }
    
    public void addEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return event.getId();
            case 1:
                return event.getTitle();
            case 2:
                return event.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            case 3:
                return event.getFinishDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            case 4:
                return event.getDescription();
            case 5:
                return event.getUrl();
            case 6:
                return event.getCats().toString(); // Aquí podrías ajustar la presentación de las categorías según tu requerimiento
            case 7:
                return event.getPrice();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}