package connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSQL { // Đổi tên lớp từ connectSQL thành ConnectSQL
    private Connection connection;

    // Phương thức để tạo kết nối với database, trả về Connection
    public boolean connect_db() {
        String url = "jdbc:mysql://localhost:3306/face_id?useUnicode=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Kết nối database thành công!"); // Thông báo kết nối thành công
            return true; // Trả về true nếu kết nối thành công
        } catch (SQLException e) {
            System.err.println("Kết nối database thất bại!"); // Thông báo lỗi
            e.printStackTrace();
            return false; // Trả về false nếu kết nối thất bại
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
