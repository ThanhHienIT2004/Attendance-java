package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Index extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tạo một nhãn và nút
        Label label = new Label("Nhấn nút để hiển thị thông điệp!");
        label.getStyleClass().add("label"); // Thêm lớp CSS cho nhãn

        Button button = new Button("Nhấn tôi");
        button.getStyleClass().add("button"); // Thêm lớp CSS cho nút
        button.setOnAction(e -> label.setText("Chào mừng đến với JavaFX!"));

        // Tạo VBox để chứa nhãn và nút ở giữa
        VBox vbox = new VBox(10, label, button);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        vbox.getStyleClass().add("vbox");

        // Tạo BorderPane để chia phân khu
        BorderPane borderPane = new BorderPane();

        // Phân khu trên
        Label topLabel = new Label("Phân khu trên");
        HBox topBox = new HBox(topLabel);
        topBox.getStyleClass().add("top-box"); // Thêm lớp CSS cho topBox
        borderPane.setTop(topBox);

        // Phân khu trái
        Label leftLabel = new Label("Phân khu trái");
        VBox leftBox = new VBox(leftLabel);
        leftBox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10;");
        borderPane.setLeft(leftBox);

        // Phân khu giữa (chứa VBox với nhãn và nút)
        borderPane.setCenter(vbox);

        // Tạo cảnh với kích thước tùy ý và thêm file CSS
        Scene scene = new Scene(borderPane, 500, 400);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setTitle("JavaFX với CSS");
        primaryStage.setMaximized(true); // Chế độ full window (không fullscreen)
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}