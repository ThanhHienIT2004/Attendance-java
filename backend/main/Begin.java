package main;

import controller.FaceDetect;  
import connect.ConnectSQL; // Import lớp ConnectSQL
import model.Student; // Thêm import cho lớp Student

public class Begin {                  
    public static void main(String[] args) {  // Phương thức main
    	
        // Khởi tạo đối tượng ConnectDatabase
        ConnectSQL connect = new ConnectSQL();  
        FaceDetect faceDetect = new FaceDetect();      // Khởi tạo đối tượng FaceDetect
        Student newStudent = new Student(); // Tạo đối tượng Student
        
        // Gọi phương thức connect_db để kết nối đến cơ sở dữ liệu
        if (connect.connect_db()) { // Kiểm tra kết nối            
            newStudent = newStudent.enterStudentInfo(); // Nhập thông tin học sinh
            
            // Gọi phương thức detectFace và truyền ID học sinh
            faceDetect.detectFace(newStudent); // Call detectFace on the faceDetect instance
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu."); // Thông báo nếu không kết nối thành công
        }
    }
}   
