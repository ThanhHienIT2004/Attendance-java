package controller;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.*;
import java.util.*;

public class FaceTrainer {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final String datasetPath = "dataset";
    private static final String cascadePath = "open_cv/haarcascade_frontalface_default.xml";
    private static final String trainerPath = "trainer/trainer.yml";

    private CascadeClassifier detector;
    private Map<Integer, Mat> faceHistograms;

    public FaceTrainer() {
        detector = new CascadeClassifier(cascadePath);
        faceHistograms = new HashMap<>();
    }

    private void calculateHistogram(Mat image, Mat histogram) {
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat histRange = new MatOfFloat(0f, 256f);
        MatOfInt channels = new MatOfInt(0);
        
        Imgproc.calcHist(Arrays.asList(image), channels, new Mat(), histogram, histSize, histRange);
        Core.normalize(histogram, histogram, 0, 1, Core.NORM_MINMAX);
    }

    private List<Mat> getImagesAndLabels(List<Integer> ids) {
        File datasetDir = new File(datasetPath);
        File[] studentDirs = datasetDir.listFiles(File::isDirectory); // Lấy danh sách thư mục sinh viên
        List<Mat> faceSamples = new ArrayList<>();

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
                } catch (NumberFormatException e) {
                    System.out.println("ID không hợp lệ trong tên thư mục: " + studentName);
                    continue;
                }

                // Lấy danh sách các tệp hình ảnh trong thư mục sinh viên
                File[] imageFiles = studentDir.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
                if (imageFiles != null) {
                    for (File imageFile : imageFiles) {
                        System.out.println("Attempting to read: " + imageFile.getAbsolutePath()); // Debugging output
                        Mat img = Imgcodecs.imread(imageFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                        if (img.empty()) {
                            System.out.println("Không thể đọc hình ảnh: " + imageFile.getName());
                            continue;
                        }

                        MatOfRect faceDetections = new MatOfRect();
                        detector.detectMultiScale(img, faceDetections);

                        for (Rect rect : faceDetections.toArray()) {
                            Mat faceMat = new Mat(img, rect);
                            faceSamples.add(faceMat);
                            ids.add(id); // Thêm ID vào danh sách
                        }
                    }
                }
            }
        }
        return faceSamples;
    }

    public void trainFaces() {
        List<Integer> ids = new ArrayList<>();
        List<Mat> faces = getImagesAndLabels(ids);
        
        if (!faces.isEmpty()) {
            for (int i = 0; i < faces.size(); i++) {
                Mat histogram = new Mat();
                calculateHistogram(faces.get(i), histogram);
                faceHistograms.put(ids.get(i), histogram);
            }
            
            saveTrainingData();
            System.out.println(ids.size() + " khuôn mặt đã được train và lưu vào " + trainerPath);
        } else {
            System.out.println("Không tìm thấy hình ảnh hợp lệ.");
        }
    }

    private void saveTrainingData() {
        try (FileOutputStream fos = new FileOutputStream(trainerPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // Chuyển đổi faceHistograms thành định dạng có thể tuần tự hóa
            Map<Integer, byte[]> serializableHistograms = new HashMap<>();
            for (Map.Entry<Integer, Mat> entry : faceHistograms.entrySet()) {
                // Kiểm tra kiểu dữ liệu của Mat
                if (entry.getValue().type() == CvType.CV_8UC1) { // Kiểu dữ liệu 8-bit đơn kênh
                    byte[] byteArray = new byte[(int) (entry.getValue().total() * entry.getValue().channels())];
                    entry.getValue().get(0, 0, byteArray);
                    serializableHistograms.put(entry.getKey(), byteArray);
                } else {
                    System.out.println("Kiểu dữ liệu không tương thích cho ID: " + entry.getKey());
                }
            }
            oos.writeObject(serializableHistograms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FaceTrainer faceTrainer = new FaceTrainer();
        faceTrainer.trainFaces();
    }
}
