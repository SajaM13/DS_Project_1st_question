package org.example;

import org.example.Client.RMIClient;

import java.io.Serializable;

public class EmployeeInfo implements Serializable {
    private RMIClient client;
    private int portNumber;

    public EmployeeInfo(int portNumber) {
        this.client = client;
        this.portNumber = portNumber;
    }

    public RMIClient getClient() {
        return client;
    }

    public int getPortNumber() {
        return portNumber;
    }
    @Override
    public String toString() {
        return
                "" + portNumber
               ;
    }

}

