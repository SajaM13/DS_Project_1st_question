package org.example.Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Abstract class containing the implementation of client methods.
 */
public abstract class AbstractClientFunctionClass extends RemoteObject implements RMIClient, Serializable {

    public AbstractClientFunctionClass() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void receiveMessage(String message) throws RemoteException {
        System.out.println("Received message: " + message);
    }
    //    protected synchronized static String clientRead(String line) {
//
//    }
    // To return the current system time of the client.
//    protected synchronized static String getCurrentTime(){
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        return currentDateTime.format(formatter);
//    }

}