package org.example.Server;

import org.example.Client.RMIClient;
import org.example.EmployeeInfo;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.*;
//
//static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        }
public class AbstractServerFunctionClass extends RemoteObject implements RMIServer {
    private final Map<String, String> employeeReports = new HashMap<>();
    private Map<String, EmployeeInfo> employees = new HashMap<>();
    private Map<String, String> employeeIPs = new HashMap<>();
    private RMIClient managerClient;
    private int managerPort;
    public String passingRemoteObject() throws RemoteException {
        return "Hello from the employee! remote object has been exchanged";
    }

    public void registerManager(RMIClient manager, int portNumber) throws RemoteException {
        this.managerClient = manager;
        this.managerPort = portNumber;
        System.out.println("Manager registered on port " + portNumber);
    }
    public synchronized void sendEmployeeDetails(String name, int portNumber, String ipAddress) throws RemoteException {
        EmployeeInfo info = employees.get(name);
        if (info != null) {
            System.out.println("Client: " + name + " is active now !");
        } else {
            // Create a new EmployeeInfo object with the provided portNumber
            EmployeeInfo newInfo = new EmployeeInfo(portNumber);

            // Add the new EmployeeInfo object to the employees map
            employees.put(name, newInfo);
        }

        // Check if employeeIPs is not empty
        if (!employeeIPs.isEmpty()) {
            // Clear all entries in employeeIPs
            employeeIPs.clear();
        }

        // Add the new entry for the name and ipAddress
        employeeIPs.put(name, ipAddress);
    }


    public synchronized Map<String, String> getEmployeeIPs() throws RemoteException {
        return new HashMap<>(employeeIPs);
    }
    public synchronized Map<String, EmployeeInfo> getActiveEmployees() throws RemoteException {
        // Return a copy of the map to avoid exposing the internal data structure
        return new HashMap<>(employees);
    }


//    public synchronized void sendEmployeeDetails(String name, int portNumber, String ipAddress) throws RemoteException {
//        EmployeeInfo info = employees.get(name);
//        if (info != null) {
//            System.out.println("Client: " + name + " is active now !");
//        }
//        employeeIPs.put(name, ipAddress);
//    }


    public synchronized void notifyDisconnection(String name, int portNumber) throws RemoteException {
        employees.remove(name);
    }

    public synchronized void sendReport(String name, int employeePort, String report) throws RemoteException {
        // Store the employee's report in the map
        employeeReports.put(name, report);
    }

    public synchronized String getEmployeeReport(String name) throws RemoteException {
        // Return the report of the specified employee
        return employeeReports.get(name);
    }

    public byte[] captureScreen() throws RemoteException, AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(capture, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
    public byte[] captureWebcamImage() throws RemoteException, IOException {
        //saja
       // System.load("C:\\Users\\asus\\Downloads\\opencv\\build\\java\\x64\\opencv_java490.dll");
        //raghad
         System.load("C:\\Users\\HP\\Downloads\\opencv\\build\\java\\x64\\opencv_java490.dll");
        VideoCapture capture = new VideoCapture(0);
        Mat image = new Mat();
        byte[] imageData = null;

        if (capture.isOpened()) {
            // read image to matrix
            if (capture.read(image)) {
                // convert matrix to byte
                final MatOfByte buf = new MatOfByte();
                Imgcodecs.imencode(".jpg", image, buf);

                imageData = buf.toArray();
            }
            capture.release();
            System.out.println("done");

        } else {
            System.out.println("Failed to open the camera.");
        }

        return imageData;
    }
    public static synchronized String getReport(Scanner sc)  {
        System.out.println("Please enter your report (10 words maximum). Press Enter after each word:");
        String report = "";
        for (int i = 0; i < 10 && sc.hasNext(); i++) {
            report += sc.next() + " ";
            if (sc.hasNextLine()) {
                sc.nextLine();  // Consume the rest of the line
            }
        }
        return report.trim();  // Remove the trailing space
    }

}
