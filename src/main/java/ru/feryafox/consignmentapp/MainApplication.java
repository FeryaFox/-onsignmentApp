package ru.feryafox.consignmentapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.feryafox.consignmentapp.ProductRepository.ProductRepository;


import java.io.IOException;

public class MainApplication extends Application {

    private ProductRepository pr;

    private void createProductRepository() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        pr = new ProductRepository(sessionFactory);
    }

    @Override
    public void start(Stage stage) throws IOException {
        createProductRepository();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        MainController controller = fxmlLoader.getController();
        controller.setProductRepository(pr);
        controller.init();
        stage.setTitle("Накладные");
        stage.setScene(scene);

        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        stage.getIcons().add(icon);

        stage.setMinWidth(600);
        stage.setMinHeight(400);

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}