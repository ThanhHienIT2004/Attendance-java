package main;

import controller.FaceDetect;  
import repository.ConnectDatabase;
import model.Student; // Thêm import cho lớp student

public class Begin {                  
    public static void main(String[] args) {  // Phương thức main
    	
        // Khởi tạo đối tượng ConnectDatabase
        ConnectDatabase connect = new ConnectDatabase();  
        FaceDetect faceDetect = new FaceDetect();      // Khởi tạo đối tượng FaceDetect
        Student newStudent = new Student(); // Tạo đối tượng student
        
        connect.connect_db();     // Gọi phương thức connect_db để kết nối đến cơ sở dữ liệu
                
        newStudent = newStudent.enterStudentInfo(); // Nhập thông tin học sinh
                    
        // Gọi phương thức detectFace và truyền ID học sinh
        faceDetect.detectFace(newStudent); // Call detectFace on the faceDetect instance
        ConnectDatabase.saveStudentData(newStudent); // Lưu thông tin học sinh vào cơ sở dữ liệu

    }
}   
