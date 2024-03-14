package view;

import controller.LoginController;
import controller.MainController;
import java.time.LocalDateTime;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Event;
import model.Sale;
import mongodb.MongoEventHandler;
import mongodb.MongoUserHandler;
import sqldb.SqlConnector;
import sqldb.SqlDataHandler;
import tablemodel.EventTableModel;
import tablemodel.SaleTableModel;
import utilities.DateHandler;
import utilities.ReportWriter;

/**
 * @author Martin Ramonda
 */
public class MainFrame extends javax.swing.JFrame {
    
    private EventTableModel eventModel;
    private SaleTableModel saleModel;
    private SqlConnector connector;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        connector = new SqlConnector();
        connector.checkOrCreateDatabase();
        initComponents();
        initView();
        setListeners();
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }
    
    public void initView(){
        this.usernameLabel.setText(MainController.getInstance().getCurrentUser().getUsername());
        this.showLabel.setText("-");
        this.priceLabel.setText("0.00");
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
        editor.getTextField().setEditable(false);
        setUpCategories();
        setUpEventComboBox();
        setUpUsersComboBox();
        setUpShowTable();
        setUpSalesTable();
    }
    
    private void setUpSalesTable(){
        saleModel = new SaleTableModel(SqlDataHandler.getInstance().getAll());
        this.salesSummaryDisplayTable.setModel(saleModel);
    }
    
    private void setUpShowTable(){
        eventModel = new EventTableModel(MongoEventHandler.getInstance().getAllEvents());
        this.allShowsDisplayTable.setModel(eventModel);
    }
    
    private void setUpUsersComboBox(){
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel();
        MongoUserHandler.getInstance().getAllUsers().forEach(user->{
            comboBoxModel.addElement(user.getUsername());
        });
        this.usersComboBox.setModel(comboBoxModel);
    }
    
    private void setUpCategories(){
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement("");
        MongoEventHandler.getInstance().getAllCategories().forEach(cat->{
            comboBoxModel.addElement(cat);
        });        
        this.categoryFilterCombo.setModel(comboBoxModel);
    }
    
    private void setUpEventComboBox(){
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel();
        MongoEventHandler.getInstance().getAllEvents().forEach(event->{
            comboModel.addElement(event.getTitle());
        });
        this.showsComboBox.setModel(comboModel);
    }
    
    public void setListeners(){
        
        // listener para boton de logout
        this.logoutButton.addActionListener(e->{
            this.setVisible(false);
            LoginController.getInstance().getLoginFrame().setVisible(true);
            MainController.getInstance().setCurrentUser(null);
        });
        
        // listener para cambio de fecha en el jcalendar
        this.progCalendar.addPropertyChangeListener(e->{
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Event item : MongoEventHandler.getInstance().getEventsOnDate(DateHandler.converToLocalDate(progCalendar.getDate()))){
                listModel.addElement(item.getTitle());
            }
            this.showList.setModel(listModel);
        });
        
        // listener para cambio de seleccion en el jlist
        this.showList.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                this.showInfoDisplay.setText("");
                if(showList.getSelectedValue()!=null){
                    selectedEventChanged();
                }
                else{
                    this.showLabel.setText("-");
                    this.priceLabel.setText("0.00");
                }
            }
        });
        
        // listener para cambio de item en el jspinner
        this.quantitySpinner.addChangeListener(e->{
            double ogPrice = 12;
            double price = ogPrice *(int)quantitySpinner.getModel().getValue();
            this.priceLabel.setText(String.valueOf(price));
        });
        
        // listener para el boton de comprar
        this.buyButton.addActionListener(e->{
            if(priceLabel.getText().equalsIgnoreCase("0.00")){
                JOptionPane.showMessageDialog(null, "Debes seleccionar un show para realizar compras", "ALERTA", JOptionPane.ERROR_MESSAGE);
            }
            else{
                int opcion = JOptionPane.showConfirmDialog(null, "¿Comprar entradas por valor de "+ this.priceLabel.getText() + "?", "Realizar Compra", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    String user = MainController.getInstance().getCurrentUser().getUsername();
                    String event = this.showList.getSelectedValue();
                    int numTickets = (int)this.quantitySpinner.getModel().getValue();
                    double totalPrice = Double.parseDouble(this.priceLabel.getText());
                    Sale newSale = new Sale(user,event,LocalDateTime.now(),numTickets,totalPrice);
                    SqlDataHandler.getInstance().insertSale(newSale);
                    JOptionPane.showMessageDialog(null, "Compra realizada con éxito!", "OKAY", JOptionPane.INFORMATION_MESSAGE);
                    setUpSalesTable();
                }
            }
        });
        
        // listener para el comboBox de categorias
        this.categoryFilterCombo.addActionListener(e->{
            loadQueryResult();
        });
        
        //listener para textfield de titulo
        nameShowFieldFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadQueryResult();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                loadQueryResult();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        //listener para jmonthchooser
        montShowFilter.addPropertyChangeListener("month", e->{
            loadQueryResult();
        });
        
        salesByShowButton.addActionListener(e->{
            reportsDisplay.setText("");
            String report = ReportWriter.writeSalesReport(SqlDataHandler.getInstance().findByEvent((String)showsComboBox.getSelectedItem()));
            reportsDisplay.setText(report);
        });
        
        salesByCategoryButton.addActionListener(e->{
            reportsDisplay.setText("");
            String report = ReportWriter.writeSalesReport(SqlDataHandler.getInstance().findByCategory((String)usersComboBox.getSelectedItem()));
            reportsDisplay.setText(report);
        });
        
        monthlySalesButton.addActionListener(e->{
            reportsDisplay.setText("");
            String report = ReportWriter.writeSalesReport(SqlDataHandler.getInstance().findByMonth(montChooser.getMonth()+1));
            reportsDisplay.setText(report);
        });
        
        annualSalesButton.addActionListener(e->{
            reportsDisplay.setText("");
            String report = ReportWriter.writeSalesReport(SqlDataHandler.getInstance().findByYear(yearChooser.getYear()+1));
            reportsDisplay.setText(report);
        });
        
        salesGreaterThanButton.addActionListener(e->{
            reportsDisplay.setText("");
            String report = ReportWriter.writeSalesReport(SqlDataHandler.getInstance().findByPriceGreaterThan(Double.parseDouble((String)priceValuesComboBox.getSelectedItem())));
            reportsDisplay.setText(report);
        });
    }
    
    private void selectedEventChanged(){
        Event selectedEvent = MongoEventHandler.getInstance().getEventByTitle(showList.getSelectedValue());
        showInfoDisplay.append("Titulo --> " + selectedEvent.getTitle() + "\n");
        showInfoDisplay.append("Descripcion --> " + selectedEvent.getDescription()+ "\n");
        showInfoDisplay.append("URL --> " + selectedEvent.getUrl()+ "\n");
        showInfoDisplay.append("Hora de comienzo --> " + selectedEvent.getStartDateTime().getHour()+":"+selectedEvent.getStartDateTime().getMinute() + "\n");
        if(!selectedEvent.getCats().isEmpty()){
            showInfoDisplay.append("Categorias:\n");
            selectedEvent.getCats().forEach(cat->{
                showInfoDisplay.append("\t->" + cat.getCatName() + "\n");
            });
        }
        this.showLabel.setText(selectedEvent.getTitle());
        this.priceLabel.setText(String.valueOf(selectedEvent.getPrice()));
    }
    
    private void loadQueryResult() {
        eventModel.addEvents(MongoEventHandler.getInstance().getEventsBy(nameShowFieldFilter.getText(), (String)categoryFilterCombo.getSelectedItem(), montShowFilter.getMonth()+1));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JTabbedPane();
        listPanel = new javax.swing.JPanel();
        progCalendar = new com.toedter.calendar.JCalendar();
        jScrollPane1 = new javax.swing.JScrollPane();
        showList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        showInfoDisplay = new javax.swing.JTextArea();
        buyPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        buyButton = new javax.swing.JButton();
        quantitySpinner = new javax.swing.JSpinner();
        showLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        allShowsPanel = new javax.swing.JPanel();
        filtersPanel = new javax.swing.JPanel();
        filtersLabel = new javax.swing.JLabel();
        categoryFilterCombo = new javax.swing.JComboBox<>();
        categoryLabel = new javax.swing.JLabel();
        nameShowFieldFilter = new javax.swing.JTextField();
        showNameLabel = new javax.swing.JLabel();
        monthShowSearchLabel = new javax.swing.JLabel();
        montShowFilter = new com.toedter.calendar.JMonthChooser();
        jScrollPane4 = new javax.swing.JScrollPane();
        allShowsDisplayTable = new javax.swing.JTable();
        myshowsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        salesSummaryDisplayTable = new javax.swing.JTable();
        historyTitleLabel = new javax.swing.JLabel();
        reportsPanel = new javax.swing.JPanel();
        salesByShowButton = new javax.swing.JButton();
        salesByCategoryButton = new javax.swing.JButton();
        showsComboBox = new javax.swing.JComboBox<>();
        usersComboBox = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        reportsDisplay = new javax.swing.JTextArea();
        salesGreaterThanButton = new javax.swing.JButton();
        annualSalesButton = new javax.swing.JButton();
        monthlySalesButton = new javax.swing.JButton();
        montChooser = new com.toedter.calendar.JMonthChooser();
        yearChooser = new com.toedter.calendar.JYearChooser();
        priceValuesComboBox = new javax.swing.JComboBox<>();
        usernameLabel = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(showList);

        showInfoDisplay.setEditable(false);
        showInfoDisplay.setColumns(20);
        showInfoDisplay.setLineWrap(true);
        showInfoDisplay.setRows(5);
        showInfoDisplay.setEnabled(false);
        jScrollPane3.setViewportView(showInfoDisplay);

        titleLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        titleLabel.setText("COMPRAR ENTRADAS");

        buyButton.setText("COMPRAR");

        quantitySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));

        showLabel.setText("ShowName");

        priceLabel.setText("prize");

        javax.swing.GroupLayout buyPanelLayout = new javax.swing.GroupLayout(buyPanel);
        buyPanel.setLayout(buyPanelLayout);
        buyPanelLayout.setHorizontalGroup(
            buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyPanelLayout.createSequentialGroup()
                .addGap(179, 179, 179)
                .addComponent(titleLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buyPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(showLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(priceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buyButton)
                .addGap(67, 67, 67))
        );
        buyPanelLayout.setVerticalGroup(
            buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(titleLabel)
                .addGap(43, 43, 43)
                .addGroup(buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyButton)
                    .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showLabel)
                    .addComponent(priceLabel))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout listPanelLayout = new javax.swing.GroupLayout(listPanel);
        listPanel.setLayout(listPanelLayout);
        listPanelLayout.setHorizontalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(progCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(listPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        listPanelLayout.setVerticalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(listPanelLayout.createSequentialGroup()
                        .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(buyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(progCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.addTab("Programación", listPanel);

        filtersLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        filtersLabel.setText("FILTROS");

        categoryFilterCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        categoryLabel.setText("Categoría:");

        showNameLabel.setText("Nombre:");

        monthShowSearchLabel.setText("Mes:");

        javax.swing.GroupLayout filtersPanelLayout = new javax.swing.GroupLayout(filtersPanel);
        filtersPanel.setLayout(filtersPanelLayout);
        filtersPanelLayout.setHorizontalGroup(
            filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filtersPanelLayout.createSequentialGroup()
                .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filtersPanelLayout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(filtersLabel))
                    .addGroup(filtersPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(categoryLabel)
                            .addComponent(showNameLabel)
                            .addComponent(monthShowSearchLabel))
                        .addGap(18, 18, 18)
                        .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameShowFieldFilter)
                            .addComponent(categoryFilterCombo, 0, 152, Short.MAX_VALUE)
                            .addGroup(filtersPanelLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(montShowFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        filtersPanelLayout.setVerticalGroup(
            filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filtersPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(filtersLabel)
                .addGap(29, 29, 29)
                .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryLabel))
                .addGap(35, 35, 35)
                .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameShowFieldFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showNameLabel))
                .addGap(37, 37, 37)
                .addGroup(filtersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(monthShowSearchLabel)
                    .addComponent(montShowFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(98, Short.MAX_VALUE))
        );

        allShowsDisplayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(allShowsDisplayTable);

        javax.swing.GroupLayout allShowsPanelLayout = new javax.swing.GroupLayout(allShowsPanel);
        allShowsPanel.setLayout(allShowsPanelLayout);
        allShowsPanelLayout.setHorizontalGroup(
            allShowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allShowsPanelLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(filtersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        allShowsPanelLayout.setVerticalGroup(
            allShowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allShowsPanelLayout.createSequentialGroup()
                .addGroup(allShowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(allShowsPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(filtersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(allShowsPanelLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        mainPanel.addTab("Todos los espectáculos", allShowsPanel);

        salesSummaryDisplayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(salesSummaryDisplayTable);

        historyTitleLabel.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        historyTitleLabel.setText("COMPRAS REALIZADAS");

        javax.swing.GroupLayout myshowsPanelLayout = new javax.swing.GroupLayout(myshowsPanel);
        myshowsPanel.setLayout(myshowsPanelLayout);
        myshowsPanelLayout.setHorizontalGroup(
            myshowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(myshowsPanelLayout.createSequentialGroup()
                .addGroup(myshowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(myshowsPanelLayout.createSequentialGroup()
                        .addGap(348, 348, 348)
                        .addComponent(historyTitleLabel))
                    .addGroup(myshowsPanelLayout.createSequentialGroup()
                        .addGap(227, 227, 227)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(228, Short.MAX_VALUE))
        );
        myshowsPanelLayout.setVerticalGroup(
            myshowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, myshowsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(historyTitleLabel)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        mainPanel.addTab("Historial", myshowsPanel);

        salesByShowButton.setText("Informe de ventas por Espectáculo");

        salesByCategoryButton.setText("Informe de ventas por Usuario");

        showsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        usersComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        reportsDisplay.setColumns(20);
        reportsDisplay.setRows(5);
        jScrollPane5.setViewportView(reportsDisplay);

        salesGreaterThanButton.setText("Ventas por un valor superior a...");

        annualSalesButton.setText("Informe de ventas Anual");

        monthlySalesButton.setText("Informe de ventas Menual");

        priceValuesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "40", "60", "80", "100", "120", " " }));

        javax.swing.GroupLayout reportsPanelLayout = new javax.swing.GroupLayout(reportsPanel);
        reportsPanel.setLayout(reportsPanelLayout);
        reportsPanelLayout.setHorizontalGroup(
            reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(annualSalesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(salesGreaterThanButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(salesByShowButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(salesByCategoryButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(monthlySalesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42)
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceValuesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(montChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        reportsPanelLayout.setVerticalGroup(
            reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salesByShowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salesByCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportsPanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(monthlySalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(reportsPanelLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(montChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportsPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(annualSalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(reportsPanelLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(reportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salesGreaterThanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceValuesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.addTab("Informes", reportsPanel);

        usernameLabel.setText("USERNAME");

        logoutButton.setText("LOGOUT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usernameLabel)
                .addGap(18, 18, 18)
                .addComponent(logoutButton)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1086, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(logoutButton))
                .addGap(24, 24, 24)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable allShowsDisplayTable;
    private javax.swing.JPanel allShowsPanel;
    private javax.swing.JButton annualSalesButton;
    private javax.swing.JButton buyButton;
    private javax.swing.JPanel buyPanel;
    private javax.swing.JComboBox<String> categoryFilterCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel filtersLabel;
    private javax.swing.JPanel filtersPanel;
    private javax.swing.JLabel historyTitleLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel listPanel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JTabbedPane mainPanel;
    private com.toedter.calendar.JMonthChooser montChooser;
    private com.toedter.calendar.JMonthChooser montShowFilter;
    private javax.swing.JLabel monthShowSearchLabel;
    private javax.swing.JButton monthlySalesButton;
    private javax.swing.JPanel myshowsPanel;
    private javax.swing.JTextField nameShowFieldFilter;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JComboBox<String> priceValuesComboBox;
    private com.toedter.calendar.JCalendar progCalendar;
    private javax.swing.JSpinner quantitySpinner;
    private javax.swing.JTextArea reportsDisplay;
    private javax.swing.JPanel reportsPanel;
    private javax.swing.JButton salesByCategoryButton;
    private javax.swing.JButton salesByShowButton;
    private javax.swing.JButton salesGreaterThanButton;
    private javax.swing.JTable salesSummaryDisplayTable;
    private javax.swing.JTextArea showInfoDisplay;
    private javax.swing.JLabel showLabel;
    private javax.swing.JList<String> showList;
    private javax.swing.JLabel showNameLabel;
    private javax.swing.JComboBox<String> showsComboBox;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JComboBox<String> usersComboBox;
    private com.toedter.calendar.JYearChooser yearChooser;
    // End of variables declaration//GEN-END:variables

}
