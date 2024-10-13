package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Student; // Changed import to follow Java naming conventions

public class ConnectDatabase {
    
    // Phương thức để tạo kết nối với database, trả về Connection
    public static Connection connect_db() {
        // Thông tin kết nối
        String url = "jdbc:mysql://localhost:3306/face_id"; // Thay "face_id" bằng tên database của bạn
        String username = "root"; // Tên tài khoản MySQL
        String password = ""; // Mật khẩu của tài khoản MySQL

        // Kết nối với MySQL
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Kết nối database thành công!");
            return connection; // Trả về đối tượng Connection
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Trả về null nếu kết nối thất bại
    }

    // Phương thức để lưu dữ liệu học sinh vào database
    public static void saveStudentData(Student student) { // Changed parameter to follow Java naming conventions
        String query = "INSERT INTO students (name, studentClass, faceImgPath, id) VALUES (?, ?, ?, ?)"; // Câu lệnh SQL để chèn dữ liệu

        try (Connection connection = connect_db()) { // Kết nối đến database
            if (connection != null) { // Check if connection is not null
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Thiết lập các tham số cho câu lệnh SQL
                     preparedStatement.setInt(4, student.getId()); // Uncomment if ID is needed
                    preparedStatement.setString(1, student.getName());
                    preparedStatement.setString(2, student.getStudentClass());
                    preparedStatement.setString(3, student.getFaceImgPath()); // Call without parameters

                    // Thực thi câu lệnh
                    preparedStatement.executeUpdate();
                    System.out.println("Dữ liệu học sinh đã được lưu thành công!");
                }
            } else {
                System.out.println("Kết nối database thất bại, không thể lưu dữ liệu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
