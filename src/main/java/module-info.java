module ru.feryafox.heatandcoldapp {
    requires java.naming;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires javafx.controls;
    requires java.datatransfer;
    requires jakarta.persistence;
    requires jakarta.cdi;
    requires java.logging;
    requires org.jboss.logging;



    opens ru.feryafox.consignmentapp.ProductRepository to org.hibernate.orm.core, javafx.base, org.hibernate.search.mapper.orm;
    opens ru.feryafox.consignmentapp to javafx.fxml;
    exports ru.feryafox.consignmentapp;
}