package org.example.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EmployeeChat {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening for messages on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    Scanner sc = new Scanner(System.in);

                    while (true) {
                        String message = in.readLine();
                        if (message == null || "exit".equalsIgnoreCase(message)) {
                            System.out.println("Chat ended by manager.");
                            break;
                        }

                        System.out.println("Received message: " + message);

                        System.out.println("Enter reply to manager (write 'exit' to stop):");
                        String reply = sc.nextLine();
                        if ("exit".equalsIgnoreCase(reply)) {
                            out.println("Chat ended by employee.");
                            System.out.println("Exiting.");
                            break;
                        }

                        out.println(reply);
                    }

                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
