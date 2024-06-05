package org.example.Client;

import org.example.EmployeeInfo;
import org.example.Server.RMIServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Scanner;

public class functions {

    public static void printMenu() {
        System.out.println("Hello Manager !");
//        System.out.println("Enter 'view' to see active employees on system");
        System.out.println("Enter 'report' if you want to see your employees reports");
        System.out.println("Enter 'capture' to capture a client's screen");
        System.out.println("Enter 'webcam' to take live photo for a client");
        System.out.println("Enter 'message' to send a message to a client");
        System.out.println("Enter 'exit' to exit");
    }
    static void viewEmployeeReports(RMIServer stub) throws RemoteException {
        Map<String, EmployeeInfo> activeEmployees = stub.getActiveEmployees();
        if (activeEmployees.isEmpty()) {
            System.out.println("No active Employee");
        } else {
            for (String name : activeEmployees.keySet()) {
                String report = stub.getEmployeeReport(name);
                System.out.println("Employee: " + name + "\nReport: " + report);
            }
        }
    }
    public static void captureWebcamImage(Scanner sc, RMIServer stub) throws Exception {
        Map<String, EmployeeInfo> activeEmployees = stub.getActiveEmployees();
        if (activeEmployees.isEmpty()) {
            System.out.println("No active Employee");
        } else {
            int i = 1;
//            List<String> employeeNames = new ArrayList<>();
            for (Map.Entry<String, EmployeeInfo> entry : activeEmployees.entrySet()) {
                System.out.println(". Employee: " + entry.getKey() + "\tis active now on port: " + entry.getValue());
//                employeeNames.add(entry.getKey());
                i++;
            }


            byte[] imageBytes = stub.captureWebcamImage();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            System.out.println("Enter the path to save the webcam image:");
            String path = sc.next();

            boolean result = ImageIO.write(image, "png", new File(path));

            if (!result) {
                System.out.printf("Failed to write image to file: %s%n", path);
            } else {
                System.out.printf("Successfully wrote image to file: %s%n", path);
            }
        }
    }
    public static void captureEmployeeScreen(Scanner sc, RMIServer stub) throws Exception {
        Map<String, EmployeeInfo> activeEmployees = stub.getActiveEmployees();
        if (activeEmployees.isEmpty()) {
            System.out.println("No active Employee");
        } else {
            int i = 1;
//            List<String> employeeNames = new ArrayList<>();
            for (Map.Entry<String, EmployeeInfo> entry : activeEmployees.entrySet()) {
                System.out.println(". Employee: " + entry.getKey() + "\tis active now on port: " + entry.getValue());
//                employeeNames.add(entry.getKey());
                i++;
            }
            byte[] captureBytes = stub.captureScreen();
            BufferedImage capture = ImageIO.read(new ByteArrayInputStream(captureBytes));

            System.out.println("Enter the path to save the screen capture:");
            String path = sc.next();

            boolean result = ImageIO.write(capture, "png", new File(path));

            if (!result) {
                System.out.printf("Failed to write image to file: %s%n", path);
            } else {
                System.out.printf("Successfully wrote image to file: %s%n", path);
            }
        }}
}
