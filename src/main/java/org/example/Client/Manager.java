package org.example.Client;

import org.example.EmployeeInfo;
import org.example.Server.RMIServer;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Manager extends AbstractClientFunctionClass {
    public Manager() throws RemoteException {
    }
    public static void main(String[] args) {
        try {
            String serverAddress = args[0];
            int clientPort = Integer.parseInt(args[1]);
            Registry registry = LocateRegistry.getRegistry(serverAddress, clientPort);
            RMIServer stub = (RMIServer) registry.lookup("RMIServer");
            System.out.println(stub.passingRemoteObject());
            System.out.println("Manager is running");
            Scanner sc = new Scanner(System.in);
            printMenu(); // Print the menu once at the start

            while (true) {
                String clientMessage = sc.nextLine().toUpperCase();
                switch (clientMessage) {
                    case "REPORT":
                        viewEmployeeReports(stub);
                        printMenu(); // Print the menu after viewing reports
                        break;
                    case "CAPTURE":
                        captureEmployeeScreen(sc, stub);
                        printMenu(); // Print the menu after capturing screen
                        break;
                    case "WEBCAM":
                        captureWebcamImage(sc, stub);
                        printMenu(); // Print the menu after capturing webcam image
                        break;
                    case "MESSAGE":
                        sendMessageToEmployee(sc, stub,5001);
                        printMenu(); // Print the menu after sending message
                        break;
                    case "EXIT":
                        System.out.println("Manager disconnected");
                        return;
                    default:
                        // If command is not recognized, try to update the remote reference
                        try {
                            stub = (RMIServer) registry.lookup("RMIServer");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void printMenu() {
        System.out.println("Hello Manager !");
//        System.out.println("Enter 'view' to see active employees on system");
        System.out.println("Enter 'report' if you want to see your employees reports");
        System.out.println("Enter 'capture' to capture a client's screen");
        System.out.println("Enter 'webcam' to take live photo for a client");
        System.out.println("Enter 'message' to send a message to a client");
        System.out.println("Enter 'exit' to exit");
    }
    private static void viewEmployeeReports(RMIServer stub) throws RemoteException {
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
    private static void captureWebcamImage(Scanner sc, RMIServer stub) throws Exception {
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
            System.out.println("kkkkkk");

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
    private static void captureEmployeeScreen(Scanner sc, RMIServer stub) throws Exception {
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
    private static void sendMessageToEmployee(Scanner sc, RMIServer stub,int clientPort) throws Exception {
        Map<String, String> employeeIPs = stub.getEmployeeIPs();
        if (employeeIPs.isEmpty()) {
            System.out.println("No active Employee");
        } else {
            for (Map.Entry<String, String> entry : employeeIPs.entrySet()) {
                System.out.println(". Employee: " + entry.getKey() + " is active now on IP: " + entry.getValue());
            }

            System.out.println("Enter the name of the employee to chat with:");
            String name = sc.next();
            String ipAddress = employeeIPs.get(name);

            if (ipAddress != null) {
                try (Socket socket = new Socket(ipAddress, clientPort);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    sc.nextLine();

                    while (true) {
                        System.out.println("Enter your message to " + name + " (write 'exit' to stop):");
                        String message = sc.nextLine();

                        if ("exit".equalsIgnoreCase(message)) {
                            System.out.println("Ending chat with " + name);
                            break;
                        }

                        out.println(message);
                        System.out.println("Message sent to " + name + " at " + ipAddress);

                        String reply = in.readLine();
                        if (reply != null) {
                            System.out.println("Reply from " + name + ": " + reply);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to chat with " + name + ": " + e.getMessage());
                }
            } else {
                System.out.println("Employee not found.");
            }
        }
    }
}

//مسار لحفظ الصورة
//C:\Users\asus\Desktop\cam.png //camera
//C:\Users\asus\Desktop\sc.png //screen






//------------------------------------------------------
  //  }

//            new Thread(() -> {
//                while (true) {
//                    try {
//                        finalStub.exchangeReferences();
//                        Thread.sleep(5000); // Exchange references every 5 seconds
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//---------------------------
//
//            Registry registry = LocateRegistry.getRegistry(serverAddress, clientPort);
//            RMIServer stub = (RMIServer) registry.lookup("RMIServer");
            /*
        Exchanging remote object references in RMI can be achieved by having a method in remote interface
        that allows a client to register itself. When a client registers,
        it passes its own remote object reference to the server.
        The server can then use this reference to call back to the client.
            */
//String temporaryName = "Manager";
//    Manager manager = new Manager();
////            stub.registerManager(manager, clientPort);