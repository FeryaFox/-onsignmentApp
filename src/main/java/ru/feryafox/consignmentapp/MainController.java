package ru.feryafox.consignmentapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import ru.feryafox.consignmentapp.ProductRepository.Product;
import ru.feryafox.consignmentapp.ProductRepository.ProductRepository;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MainController {

    private ProductRepository pr;

    public void setProductRepository(ProductRepository pr) {
        this.pr = pr;
    }

    @FXML
    private TableView<Product> mainTable;

    @FXML
    private HBox searchContainer;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addProductButton;

    @FXML
    private HBox addContainer;

    private Product newProduct = new Product();

    @FXML
    protected void addProductButtonClicked() {
        pr.addProduct(newProduct.clone());

        ObservableList<Product> data = FXCollections.observableList(pr.getAllProducts());
        mainTable.setItems(data);
    }

    public void init() {
        newProduct.setAvailability(Product.Availability.IN_STOCK);
        mainTable.setEditable(true);
        addProductButton.setDisable(true);

        TableColumn<Product, Date> deliveryDate = createColumn("Дата", "deliveryDate", 20);
        TableColumn<Product, String> name = createColumn("Название", "name", 30);
        TableColumn<Product, Double> deliveryPrice = createColumn("Цена прихода", "deliveryPrice", 15);
        TableColumn<Product, Double> displayPrice = createColumn("Цена на витрине", "displayPrice", 15);
        TableColumn<Product, Product.Availability> availability = createColumn("Кол-во", "availability", 20);

        setCurrencyFormat(deliveryPrice);
        setCurrencyFormat(displayPrice);
        setAvailabilityFormat(availability);


        mainTable.getColumns().addAll(deliveryDate, name, deliveryPrice, displayPrice, availability);

        addSearchFieldDate(deliveryDate, 20);
        addSearchField(name, 30);
        addSearchField(deliveryPrice, 15);
        addSearchField(displayPrice, 15);
        addSearchComboBox(availability, 20);


        addAddDataField(deliveryDate, 20);
        addAddField(name, 30);
        addAddField(deliveryPrice, 15);
        addAddField(displayPrice, 15);
        addAddComboBox(availability, 20);
        ObservableList<Product> data = FXCollections.observableList(pr.getAllProducts());

        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainTable.setItems(data);

        setEditableCells(deliveryDate, name, deliveryPrice, displayPrice, availability);


        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteButton.setDisable(true);

        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteButton.setDisable(mainTable.getSelectionModel().getSelectedItems().isEmpty());
        });


        deleteButton.setOnAction(event -> {
            deleteSelectedProducts();
        });

    }

    private <T> TableColumn<Product, T> createColumn(String title, String propertyName, double widthPercentage) {
        TableColumn<Product, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.prefWidthProperty().bind(mainTable.widthProperty().multiply(widthPercentage / 100.0));
        return column;
    }

    private void addAddDataField(TableColumn<Product, Date> column, double widthPercentage) {
        DatePicker datePicker = new DatePicker();
        datePicker.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));
        datePicker.setPromptText("Дата");

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue != null)
                    updateDateNewProduct(Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                else updateDateNewProduct(null);
            }
            catch (DateTimeParseException e){
                updateDateNewProduct(null);
            }
            toggleAddButton();
        });

        addContainer.getChildren().add(datePicker);
    }

    private void updateDateNewProduct(Date date) {
        newProduct.setDeliveryDate(date);
    }

    private void updateAvailabilityNewProduct(String availability) {
        switch (availability) {
            case "В наличии":
                newProduct.setAvailability(Product.Availability.IN_STOCK);
                break;
            case "Мало":
                newProduct.setAvailability(Product.Availability.LOW_STOCK);
                break;
            case "Нет в наличии":
                newProduct.setAvailability(Product.Availability.OUT_OF_STOCK);
                break;
        }
    }

    private void updateFieldNewProduct(String columnName, String data) {
        switch (columnName) {
            case "Название":
                if (data.isBlank()) {
                    newProduct.setName(null);
                    break;
                }
                newProduct.setName(data);
                break;
            case "Цена прихода":
                try {
                    if (data == null || data.isEmpty() || data.isBlank()) throw new NumberFormatException();
                    String fixData = data.replace(",", ".");
                    newProduct.setDeliveryPrice(Double.parseDouble(fixData));
                }
                catch (NumberFormatException e) {
                    newProduct.setDeliveryPrice(null);
                }
                break;
            case "Цена на витрине":
                try {
                    if (data == null || data.isEmpty() || data.isBlank()) throw new NumberFormatException();
                    String fixData = data.replace(",", ".");
                    newProduct.setDisplayPrice(Double.parseDouble(fixData));
                }
                catch (NumberFormatException e) {
                    newProduct.setDisplayPrice(null);
                }
                break;
        }

    }

    private void addAddField(TableColumn<Product, ?> column, double widthPercentage) {
        TextField textField = new TextField();
        textField.setPromptText(column.getText());
        textField.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFieldNewProduct(column.getText(), newValue);
            toggleAddButton();
        });

        addContainer.getChildren().add(textField);
    }

    private void addAddComboBox(TableColumn<Product, Product.Availability> column, double widthPercentage) {
        ComboBox<String> comboBox = new ComboBox<>();

        comboBox.getItems().addAll("В наличии", "Мало", "Нет в наличии");
        comboBox.getSelectionModel().select("В наличии");
        comboBox.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateAvailabilityNewProduct(newValue);
        });

        addContainer.getChildren().add(comboBox);
    }

    private void addSearchField(TableColumn<Product, ?> column, double widthPercentage) {
        TextField searchField = new TextField();
        searchField.setPromptText(column.getText());
        searchField.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(column, newValue);
        });

        searchContainer.getChildren().add(searchField);
    }

    private void addSearchFieldDate(TableColumn<Product, Date> column, double widthPercentage) {
        DatePicker datePicker = new DatePicker();
        datePicker.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));
        datePicker.setPromptText("Дата");

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterTableByDate(column, Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else {
                filterTableByDate(column, null);
            }
        });

        searchContainer.getChildren().add(datePicker);
    }

    private void filterTableByDate(TableColumn<Product, Date> column, Date searchDate) {
        ObservableList<Product> items = FXCollections.observableList(pr.getAllProducts());
        FilteredList<Product> filteredData = new FilteredList<>(items);

        filteredData.setPredicate(product -> {
            Date productDate = column.getCellData(product);
            if (searchDate == null) {
                return true;
            } else {
                return productDate != null && productDate.equals(searchDate);
            }
        });

        mainTable.setItems(filteredData);
    }

    private void addSearchComboBox(TableColumn<Product, Product.Availability> column, double widthPercentage) {
        ComboBox<String> searchComboBox = new ComboBox<>();
        searchComboBox.getItems().addAll("Все", "В наличии", "Мало", "Нет в наличии");
        searchComboBox.setPromptText("Наличие");
        searchComboBox.prefWidthProperty().bind(searchContainer.widthProperty().multiply(widthPercentage / 100.0));

        searchComboBox.getSelectionModel().select("Все");

        searchComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByAvailability(column, newValue);
        });

        searchContainer.getChildren().add(searchComboBox);
    }


    private void filterTableByAvailability(TableColumn<Product, Product.Availability> column, String selectedAvailability) {
        ObservableList<Product> items = FXCollections.observableList(pr.getAllProducts());
        FilteredList<Product> filteredData = new FilteredList<>(items);

        filteredData.setPredicate(product -> {
            if (selectedAvailability == null || selectedAvailability.equals("Все")) {
                return true;
            } else {
                Product.Availability availability = stringToAvailability(selectedAvailability);
                return product.getAvailability() == availability;
            }
        });

        mainTable.setItems(filteredData);
    }

    private void toggleAddButton(){
        if (newProduct.getDeliveryDate() != null && newProduct.getDeliveryPrice() != null && newProduct.getDisplayPrice() != null && newProduct.getName() != null) {
            addProductButton.setDisable(false);
        }
        else addProductButton.setDisable(true);
    }

    private void filterTable(TableColumn<Product, ?> column, String searchText) {

        ObservableList<Product> items = FXCollections.observableList(pr.getAllProducts());
        FilteredList<Product> filteredData = new FilteredList<>(items);

        filteredData.setPredicate(product -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            if (column.getCellData(product) == null) {
                return false;
            }

            String cellValue;
            if (column.getCellData(product) instanceof Float) {
                cellValue = String.format("%.2f ₽", (Float) column.getCellData(product));
            } else if (column.getCellData(product) instanceof Product.Availability) {
                switch ((Product.Availability) column.getCellData(product)) {
                    case IN_STOCK:
                        cellValue = "Много";
                        break;
                    case LOW_STOCK:
                        cellValue = "Мало";
                        break;
                    case OUT_OF_STOCK:
                        cellValue = "Нету";
                        break;
                    default:
                        cellValue = column.getCellData(product).toString();
                }
            } else {
                cellValue = column.getCellData(product).toString();
            }

            return cellValue.toLowerCase().contains(lowerCaseFilter);
        });

        mainTable.setItems(filteredData);
    }

    private void setCurrencyFormat(TableColumn<Product, Double> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format(java.util.Locale.US, "%.2f ₽", price));
                }
            }
        });
    }

    private void setAvailabilityFormat(TableColumn<Product, Product.Availability> column) {
        column.setCellFactory(tc -> new TableCell<Product, Product.Availability>() {
            @Override
            protected void updateItem(Product.Availability availability, boolean empty) {
                super.updateItem(availability, empty);
                if (empty || availability == null) {
                    setText(null);
                } else {
                    setText(availabilityToString(availability));
                }
            }
        });
    }

    private void setEditableCells(TableColumn<Product, Date> deliveryDate,
                                  TableColumn<Product, String> name,
                                  TableColumn<Product, Double> deliveryPrice,
                                  TableColumn<Product, Double> displayPrice,
                                  TableColumn<Product, Product.Availability> availability) {

        deliveryDate.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Date>() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public String toString(Date date) {
                return date != null ? dateFormat.format(date) : "";
            }

            @Override
            public Date fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? dateFormat.parse(string) : null;
                } catch (ParseException e) {
                    return null;
                }
            }
        }));
        deliveryDate.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setDeliveryDate(event.getNewValue());
            pr.updateProduct(product);
        });

        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setName(event.getNewValue());
            pr.updateProduct(product);
        });

        StringConverter<Double> priceConverter = new StringConverter<Double>() {
            private final DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());

            {
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                formatter.setDecimalFormatSymbols(symbols);
            }

            @Override
            public String toString(Double value) {
                return value != null ? formatter.format(value) : "";
            }

            @Override
            public Double fromString(String string) {
                return parseDoubleFlexible(string);
            }
        };

        deliveryPrice.setCellFactory(TextFieldTableCell.forTableColumn(priceConverter));
        deliveryPrice.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setDeliveryPrice(event.getNewValue());
            pr.updateProduct(product);
        });

        displayPrice.setCellFactory(TextFieldTableCell.forTableColumn(priceConverter));
        displayPrice.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setDisplayPrice(event.getNewValue());
            pr.updateProduct(product);
        });

        availability.setCellFactory(ComboBoxTableCell.forTableColumn(
                new StringConverter<Product.Availability>() {
                    @Override
                    public String toString(Product.Availability availability) {
                        return availabilityToString(availability);
                    }

                    @Override
                    public Product.Availability fromString(String string) {
                        return stringToAvailability(string);
                    }
                },
                Product.Availability.values()
        ));
        availability.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setAvailability(event.getNewValue());
            pr.updateProduct(product);
        });
    }

    private String availabilityToString(Product.Availability availability) {
        if (availability == null) {
            return "";
        }

        switch (availability) {
            case IN_STOCK:
                return "В наличии";
            case LOW_STOCK:
                return "Мало";
            case OUT_OF_STOCK:
                return "Нет в наличии";
            default:
                return "";
        }
    }

    private Product.Availability stringToAvailability(String string) {
        switch (string) {
            case "В наличии":
                return Product.Availability.IN_STOCK;
            case "Мало":
                return Product.Availability.LOW_STOCK;
            case "Нет в наличии":
                return Product.Availability.OUT_OF_STOCK;
            default:
                throw new IllegalArgumentException("Unknown availability: " + string);
        }
    }

    private void deleteSelectedProducts() {
        List<Product> selectedProducts = mainTable.getSelectionModel().getSelectedItems();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить выбранные продукты?");
        alert.setContentText("Вы уверены, что хотите удалить выбранные продукты? Это действие необратимо.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pr.deleteProducts(selectedProducts);

            mainTable.getItems().removeAll(selectedProducts);
        }
    }

    private Double parseDoubleFlexible(String string) {
        try {
            string = string.replace(',', '.');
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}