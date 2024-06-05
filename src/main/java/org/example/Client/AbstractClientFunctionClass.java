package org.example.Client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

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
}