package controller;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import model.Student;

import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

public class FaceDetect {

    private VideoCapture camera;
    private CascadeClassifier faceDetector;
    private String datasetDir = "dataset";
    private int faceCount = 1;

    // Đổi tên hàm khởi tạo cho khớp với tên lớp
    public FaceDetect() {
        // Nạp thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Khởi tạo camera
        this.camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Không thể mở camera.");
            return;
        }

        // Khởi tạo bộ phát hiện khuôn mặt
        this.faceDetector = new CascadeClassifier("open_cv/haarcascade_frontalface_default.xml");

        // Tạo thư mục dataset nếu chưa có
        File directory = new File(datasetDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    // Phương thức tạo thư mục cho sinh viên
    private void createStudentDirectory(int studentId, String studentName) {
        String studentDir = datasetDir + "/" + studentId + "_" + studentName; // Use id_name format
        File directory = new File(studentDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    // Phương thức phát hiện và lưu khuôn mặt
    public void detectFace(Student student) { // Accept Student object
        Mat frame = new Mat();
        
        // Create a directory for the student using their ID and name
        createStudentDirectory(student.getId(), student.getName()); // Call the new method

        String studentDir = datasetDir + "/" + student.getId() + "_" + student.getName(); // Define studentDir here

        try {
            while (true) {
                if (camera.read(frame)) {
                    Core.flip(frame, frame, 1);

                    // Chuyển đổi ảnh sang màu xám
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    MatOfRect facesArray = new MatOfRect();
                    faceDetector.detectMultiScale(grayFrame, facesArray);

                    Rect[] faces = facesArray.toArray();
                    for (Rect face : faces) {
                        Imgproc.rectangle(frame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(0, 255, 0), 2);

                        // Lưu khuôn mặt vào file
                        Mat faceImage = new Mat(frame, face);
                        String filename = studentDir + "/face_" + faceCount + ".jpg"; // Updated path to include student's ID and name
                        try {
                            Imgcodecs.imwrite(filename, faceImage);
                            System.out.println("Đã lưu khuôn mặt vào: " + filename);
                            student.setFaceImgPath(filename); // Save the path to the student object
                        } catch (Exception e) {
                            System.out.println("Không thể lưu khuôn mặt: " + e.getMessage());
                        }

                        // Tăng số lượng ảnh đã lưu
                        faceCount++;
                    }

                    if (faceCount > 30) {
                        System.out.println("Đã lưu đủ 30 khuôn mặt.");
                        break;
                    }

                    HighGui.imshow("Face Detection", frame);
                    if (HighGui.waitKey(1) == 'q') { 
                        break;
                    }
                } else {
                    System.out.println("Không thể đọc khung hình.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra trong quá trình phát hiện khuôn mặt: " + e.getMessage());
        } finally {
            camera.release();
            HighGui.destroyAllWindows();
        }
    }
}