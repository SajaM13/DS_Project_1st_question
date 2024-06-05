package org.example.Client;

import org.example.Server.RMIServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Manager extends AbstractClientFunctionClass {
   private static  functions f = new functions();

    private static RMIServer stub;
    private static Registry registry;

    public Manager() throws RemoteException {
    }
    public static void main(String[] args) throws Exception {
        try {
            String serverAddress = args[0];
            int clientPort = Integer.parseInt(args[1]);
            registry = LocateRegistry.getRegistry(serverAddress, clientPort);
            stub = (RMIServer) registry.lookup("RMIServer");
            System.out.println("Manager is running");
            Scanner sc = new Scanner(System.in);
            f.printMenu(); // Print the menu once at the start
            loopManagerChoices(sc);

        } finally {

        }
    }
    private static void updateStub() {
//        System.out.println("Remote object has been unexported, updating reference...");
        try {
            stub = (RMIServer) registry.lookup("RMIServer");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 private static void loopManagerChoices(Scanner sc){
     while (true) {
         try {
             String clientMessage = sc.nextLine().toUpperCase();
             switch (clientMessage) {
                 case "CAPTURE":
                     updateStub();
                     try {
                         f.captureEmployeeScreen(sc, stub);
                     } catch (NoSuchObjectException | ConnectException e) {
                     }
                     f.printMenu(); // Print the menu after capturing screen
                     break;
                 case "WEBCAM":
                     updateStub();

                     try {
                         f.captureWebcamImage(sc, stub);
                     } catch (NoSuchObjectException | ConnectException e) {
                     }
                     f.printMenu(); // Print the menu after capturing webcam image
                     break;
                 case "MESSAGE":
                     updateStub();
                     try {
                         sendMessageToEmployee(sc, stub,5020);
                     } catch (NoSuchObjectException | ConnectException e) {
                         updateStub();
                     }
                     f.printMenu(); // Print the menu after sending message
                     break;
                 //                 case "REPORT":
//                     updateStub();
//                     try {
//                         f.viewEmployeeReports(stub);
//                     }  catch (NoSuchObjectException | ConnectException e) {
//                     }
//                     f.printMenu();
//                     break;
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
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 }
    public static void sendMessageToEmployee(Scanner sc, RMIServer stub,int clientPort) throws Exception {
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

