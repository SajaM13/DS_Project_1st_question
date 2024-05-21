package org.example.Server;

import org.example.Client.RMIClient;
import org.example.EmployeeInfo;

import java.awt.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * An RMI interface containing the methods to be implemented by the server and to be exposed to the
 * client through a remote object.
 */
public interface RMIServer extends Remote{
    void registerManager(RMIClient manager, int portNumber) throws RemoteException;;
    void sendEmployeeDetails(String name, int portNumber, String ipAddress) throws RemoteException;
    Map<String, EmployeeInfo> getActiveEmployees() throws RemoteException ;
    void notifyDisconnection(String name, int portNumber) throws RemoteException;
    String getEmployeeReport(String name) throws RemoteException;
    void sendReport(String name, int clientPort, String report) throws RemoteException;
    Map<String, String> getEmployeeIPs() throws RemoteException;
    byte[] captureScreen() throws RemoteException, AWTException;
    byte[] captureWebcamImage()  throws RemoteException, AWTException, IOException;


}