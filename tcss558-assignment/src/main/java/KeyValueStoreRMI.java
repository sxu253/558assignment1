import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStoreRMI extends Remote {
	void runRmiProtocolServer() throws RemoteException, IOException;

	void implementRmiOperations(String[] taskKeyValue) throws RemoteException, UnknownHostException, IOException;
}