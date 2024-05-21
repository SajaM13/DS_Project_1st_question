package org.example.Server;

//import org.example.Client.Client;

import org.example.Client.RMIClient;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;

/**
 * An RMI Server to implement the remote RMI interface and extend the abstract
 * server implementation class.
 */
public class Server extends AbstractServerFunctionClass {

    public static void main(String[] args) {
        if (args.length > 0) {
            try {

                    setupEmployee(args);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("provide a port number as an argument.");
        }
    }
    private static void setupEmployee(String[] args) throws Exception {
        int portNumber = Integer.parseInt(args[0]);
        Server server = new Server();
        RMIServer skeleton = (RMIServer) UnicastRemoteObject.exportObject(server, 0);
        Registry registry = LocateRegistry.createRegistry(portNumber);
        System.out.println("Employee is listening on port " + portNumber);
        registry.rebind("RMIServer", skeleton);
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        Scanner sc = new Scanner(System.in);
        String name = promptForName(sc);
        //shutdown hook to notify the manager when the server is about to terminate
        addShutdownHook(skeleton, name, portNumber);
        skeleton.sendEmployeeDetails(name, portNumber, ipAddress); // Send employee details to manager
        System.out.println("Hello " + name +"\tyou can start you job !");
       /*
              // new thread that updates the remote reference periodically
        new Thread(() -> {
            while (true) {
                try {
                        registry.rebind("RMIServer", skeleton);
                        Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        printMenu();
        handleServerInput(sc, skeleton, name, portNumber);
    }
    private static String promptForName(Scanner sc) {
        System.out.println("Please enter your name:");
        return sc.nextLine();
    }
    private static void printMenu() {
        System.out.println("Do Your Tasks");
        System.out.println("Enter 'report' to write a report");
        System.out.println("Enter 'exit' to exit");
    }
    private static void addShutdownHook(RMIServer skeleton, String name, int portNumber) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                skeleton.notifyDisconnection(name, portNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
    private static void handleServerInput(Scanner sc, RMIServer skeleton, String name, int portNumber) throws RemoteException {
        while (true) {
            String serverMessage = sc.next();
            switch (serverMessage.toUpperCase()) {
                case "REPORT":
                    String report = getReport(sc);
                    skeleton.sendReport(name, portNumber, report);
                    break;
                case "EXIT":
                    System.out.println("Employee " + name + " disconnected");
                    skeleton.notifyDisconnection(name, portNumber);
                    return;
                default:
                    break;
            }
        }
    }


}
