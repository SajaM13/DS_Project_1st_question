package org.example.Client;

import org.example.EmployeeInfo;
import org.example.Server.RMIServer;

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
            /*
        Exchanging remote object references in RMI can be achieved by having a method in remote interface
        that allows a client to register itself. When a client registers,
        it passes its own remote object reference to the server.
        The server can then use this reference to call back to the client.
            */
            String temporaryName = "Manager";
            Manager manager = new Manager();
            stub.registerManager(manager, clientPort);
            System.out.println("Manager is running");
            Scanner sc = new Scanner(System.in);

            RMIServer finalStub = stub;
            printMenu(); // Print the menu once at the start

            while (true) {
                String clientMessage = sc.nextLine().toUpperCase();
                switch (clientMessage) {
                    case "REPORT":
                        viewEmployeeReports(stub);
                        printMenu(); // Print the menu after viewing reports
                        break;
                    case "CAPTURE":
                        captureEmployeeScreen(sc, stub, registry,temporaryName);
                        printMenu(); // Print the menu after capturing screen
                        break;
                    case "WEBCAM":
                        captureWebcamImage(sc, stub, registry,temporaryName);
                        printMenu(); // Print the menu after capturing webcam image
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
        System.out.println("Enter 'exit' to exit");
    }
//    private static void viewActiveEmployees(RMIServer stub) throws RemoteException {
//        Map<String, Integer> activeEmployees = stub.getActiveEmployees();
//        if (activeEmployees.isEmpty()) {
//            System.out.println("non of employees is active");
//        } else {
//            int i = 1;
//            for (Map.Entry<String, Integer> entry : activeEmployees.entrySet()) {
//                System.out.println(i + ". Employee: " + entry.getKey() + "\tis active on port: " + entry.getValue());
//                i++;
//            }
//        }
//    }
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
    private static void captureWebcamImage(Scanner sc, RMIServer stub, Registry registry, String temporaryName) throws Exception {
        Map<String, EmployeeInfo> activeEmployees = stub.getActiveEmployees();
        if (activeEmployees.isEmpty()) {
            System.out.println("No active Employee");
        } else {
            int i = 1;
            List<String> employeeNames = new ArrayList<>();
            for (Map.Entry<String, EmployeeInfo> entry : activeEmployees.entrySet()) {
                System.out.println(". Employee: " + entry.getKey() + "\tis active now on port: " + entry.getValue().getPortNumber());
                employeeNames.add(entry.getKey());
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
        }}
    private static void captureEmployeeScreen(Scanner sc, RMIServer stub, Registry registry,String temporaryName) throws Exception {
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
            }
        }
    }

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