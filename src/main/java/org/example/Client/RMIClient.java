package org.example.Client;

import java.awt.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;

public interface RMIClient extends Remote {
    void receiveMessage(String message) throws RemoteException;

}