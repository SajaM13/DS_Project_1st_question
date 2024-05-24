package org.example.Server;

//import org.example.Client.Client;

import org.example.Client.RMIClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
        if (args.length < 1) {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }


        int portNumber = Integer.parseInt(args[0]);
        Server server = new Server();
//        //
//        RMIServer skeleton = (RMIServer) UnicastRemoteObject.exportObject(server, 0);
//        Registry registry = LocateRegistry.createRegistry(portNumber);
//        System.out.println("Employee is listening on port " + portNumber);
//        registry.rebind("RMIServer", skeleton);
//        //
//        System.setProperty("java.rmi.server.hostname", "employee.example.com");
        System.setProperty("java.rmi.server.hostname", "localhost");
        RMIServer service = new AbstractServerFunctionClass();
        RMIServer skeleton = (RMIServer) UnicastRemoteObject.exportObject(service, 0);
        Registry registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind("RMIServer", skeleton);
        System.out.println("Employee server ready.");
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        Scanner sc = new Scanner(System.in);
        String name = promptForName(sc);
        //shutdown hook to notify the manager when the server is about to terminate
        addShutdownHook(skeleton, name, portNumber);
        skeleton.sendEmployeeDetails(name, portNumber, ipAddress); // Send employee details to manager
        System.out.println("Hello " + name +"\tyou can start you job !");
        printMenu();
        handleSocketConnections(5001);
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
    public static void handleManagerCommunication(Socket clientSocket) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {

            // Process the received message testtttttttttttttttt

            Scanner sc = new Scanner(System.in);
            String message ;

            while  ((message = in.readLine()) != null){
                if (message == null || "exit".equalsIgnoreCase(message)) {
                    System.out.println("Chat ended by manager.");
                    break;
                }

                System.out.println("Received message from manager: " + message);

                System.out.println("Enter reply to manager (write 'exit' to stop):");
                String reply = sc.nextLine();
                if ("exit".equalsIgnoreCase(reply)) {
                    out.println("Chat ended by employee.");
                    System.out.println("Exiting.");
                    break;
                }

                out.println(reply);
            }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Handle incoming socket connections
    public static void handleSocketConnections(int socketPort) {
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            System.out.println("Employee server listening on port " + socketPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("manager is connected and wanna chat with you: " + clientSocket.getInetAddress());

                // Create a thread to handle communication with this manager
                Thread managerThread = new Thread(() -> handleManagerCommunication(clientSocket));
                managerThread.start();
            }
        } catch (BindException e) {
            System.err.println("Port " + socketPort + " is already in use. Please choose a different port.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
