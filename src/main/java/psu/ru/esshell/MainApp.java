package psu.ru.esshell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(MainApp.class);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        fxmlLoader.setLocation(getClass().getResource("/sample/MainFrm.fxml"));
        rootNode = fxmlLoader.load();

        primaryStage.setTitle("Оболочка");
        Scene scene = new Scene(rootNode, 1200, 616);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.stop();
    }

}