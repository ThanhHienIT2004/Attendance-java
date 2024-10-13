package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tạo một nhãn
        Label label = new Label("Nhấn nút để hiển thị thông điệp!");

        // Tạo một nút
        Button button = new Button("Nhấn tôi");
        button.setOnAction(e -> label.setText("Chào mừng đến với JavaFX!"));

        // Tạo một VBox để chứa nhãn và nút
        VBox vbox = new VBox(10, label, button);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Tạo một cảnh và thêm vào cửa sổ
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
