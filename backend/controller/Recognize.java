package controller;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class Recognize {

	// Đường dẫn tới tệp cascade phân loại khuôn mặt
	private static final String FACE_CASCADE_PATH = "open_cv/haarcascade_frontalface_alt.xml"; // Cần có file cascade này
	private static final String DATASET_PATH = "dataset"; // Đường dẫn tới thư mục dataset

	private CascadeClassifier faceCascade;
	private Map<Integer, String> studentMap; // Lưu trữ ID và tên sinh viên
	private Map<Integer, Mat> faceHistograms; // Lưu trữ histogram của khuôn mặt

	public Recognize() {
		// Khởi tạo bộ phân loại khuôn mặt
		faceCascade = new CascadeClassifier(FACE_CASCADE_PATH);
		studentMap = new HashMap<>();
		faceHistograms = new HashMap<>(); // Khởi tạo faceHistograms
		loadStudentData(); // Tải dữ liệu sinh viên
	}

	public static void main(String[] args) {
		// Nạp thư viện OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("OpenCV library loaded.");

		Recognize faceRecognizer = new Recognize();
		faceRecognizer.startRecognition();
	}

	// Hàm tải dữ liệu sinh viên từ thư mục dataset
	private void loadStudentData() {
		File datasetDir = new File(DATASET_PATH);
		File[] studentDirs = datasetDir.listFiles(File::isDirectory); // Lấy danh sách thư mục sinh viên

		if (studentDirs != null) {
			for (File studentDir : studentDirs) {
				String studentName = studentDir.getName(); // Lấy tên thư mục
				String[] parts = studentName.split("_"); // Giả sử tên thư mục có định dạng id_name

				if (parts.length < 2) {
					System.out.println("Tên thư mục không hợp lệ: " + studentName);
					continue; // Bỏ qua thư mục không hợp lệ
				}

				int id;
				try {
					id = Integer.parseInt(parts[0]); // Lấy ID từ phần trước dấu gạch dưới
					studentMap.put(id, studentName); // Lưu ID và tên sinh viên
					System.out.println("Đã thêm sinh viên: ID = " + id + ", Tên = " + studentName); // Debugging output
				} catch (NumberFormatException e) {
					System.out.println("ID không hợp lệ trong tên thư mục: " + studentName);
				}
			}
		}
	}

	// Hàm bắt đầu nhận diện khuôn mặt
	private void startRecognition() {
		VideoCapture camera = new VideoCapture(0);
		if (!camera.isOpened()) {
			System.out.println("Không thể mở camera.");
			return;
		}

		Mat frame = new Mat();
		while (true) {
			if (camera.read(frame)) {
				// Lật khung hình
				Core.flip(frame, frame, 1); // Lật khung hình theo chiều ngang

				// Phát hiện khuôn mặt
				detectAndDisplayFaces(frame);

				// Hiển thị khung hình lên cửa sổ
				HighGui.imshow("Camera", frame);

				// Thoát nếu nhấn phím 'q'
				if (HighGui.waitKey(1) == 'q') {
					break;
				}
			} else {
				System.out.println("Không thể đọc khung hình từ camera.");
				break;
			}
		}

		camera.release();
		HighGui.destroyAllWindows();
	}

	// Hàm phát hiện khuôn mặt và vẽ khung xung quanh khuôn mặt
	private void detectAndDisplayFaces(Mat frame) {
		Mat grayFrame = new Mat();

		// Chuyển đổi khung hình sang ảnh xám
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame); // Cải thiện chất lượng ảnh xám

		// Phát hiện khuôn mặt
		MatOfRect faces = new MatOfRect();
		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0, new Size(30, 30), new Size());

		// Chuyển đổi MatOfRect thành mảng Rect[] để duyệt
		Rect[] facesArray = faces.toArray();

		// Vẽ khung xung quanh khuôn mặt và hiển thị tên
		for (Rect face : facesArray) {
			Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2);
			// Hiển thị tên sinh viên
			int studentId = identifyStudent(face, frame); // Truyền frame vào identifyStudent
			if (studentId != -1) {
				String studentName = studentMap.get(studentId);
				Imgproc.putText(frame, studentName, new Point(face.x, face.y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
				Imgproc.putText(frame, "ID: " + studentId, new Point(face.x, face.y + face.height + 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
			}
		}
	}

	// Hàm xác định ID sinh viên từ khuôn mặt (cần được triển khai)
	private int identifyStudent(Rect face, Mat frame) {
		// Cắt khuôn mặt từ khung hình
		Mat faceMat = new Mat(frame, face);
		Mat histogram = new Mat();
		calculateHistogram(faceMat, histogram); // Tính toán histogram cho khuôn mặt

		int bestMatchId = -1;
		double bestMatchValue = Double.MAX_VALUE;

		// So sánh với các histogram đã lưu
		for (Map.Entry<Integer, Mat> entry : faceHistograms.entrySet()) {
			double matchValue = Imgproc.compareHist(histogram, entry.getValue(), Imgproc.CV_COMP_CHISQR);
			if (matchValue < bestMatchValue) {
				bestMatchValue = matchValue;
				bestMatchId = entry.getKey();
			}
		}

		return bestMatchId;
	}

	// Hàm tính toán histogram
	private void calculateHistogram(Mat image, Mat histogram) {
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat histRange = new MatOfFloat(0f, 256f);
		MatOfInt channels = new MatOfInt(0);
		
		Imgproc.calcHist(Arrays.asList(image), channels, new Mat(), histogram, histSize, histRange);
		Core.normalize(histogram, histogram, 0, 1, Core.NORM_MINMAX);
	}
}
