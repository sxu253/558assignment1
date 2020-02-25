package assignment1;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStoreRMI extends Remote {
    String sayHello() throws RemoteException;
    void runRmiProtocolServer(int port) throws RemoteException, IOException;
}