package org.example.Server;


/**
 * An RMI Server to implement the remote RMI interface and extend the abstract
 * server implementation class.
 */
public class Server extends AbstractServerFunctionClass {
    static server_functions f = new server_functions();
    public static void main(String[] args) {
        if (args.length > 0) {
            try {

                    f.setupEmployee(args);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("provide a port number as an argument.");
        }
    }

}
