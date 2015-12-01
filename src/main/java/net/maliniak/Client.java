package net.maliniak;


import net.maliniak.model.Window;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private void doTest() {
        try {
            // fire to localhost port 1099
            Registry myRegistry = LocateRegistry.getRegistry("127.0.0.1", 1099);

            // search for myMessage service
            RemoteApi impl = (RemoteApi) myRegistry.lookup("remoteApi");

            // call server's method
            Window uniqueWindow = impl.getUniqueWindow("Untitled.*Notepad.*");
            System.out.println(uniqueWindow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client main = new Client();
        main.doTest();
    }
}
