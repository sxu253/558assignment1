/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements KeyValueStoreRMI {

	// Variables
	HashMap<String, String> storeMap = new HashMap<>();
	HashMap<String, String> storeMapRmi = new HashMap<>();
	String value;
	String key;
	Registry registry;

	// Implement server side socket for TCP
	public void runTcpProtocolServer(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server is listening on port " + port);

			while (true) {
				System.out.println("Starting a new thread for this client");
				ClientThreadHandlerTCP th = new ClientThreadHandlerTCP(serverSocket.accept(), storeMap);
				Thread t = new Thread(th);
				t.start();
			}

		} catch (IOException e) {
			System.out.println("Accept failed");
		}

	}

	// Implement server side socket for UDP
	public void runUdpProtocolServer(int port) throws IOException {
		// Created socket to listen at specified port
		try (DatagramSocket ds = new DatagramSocket(port)) {
			System.out.println("Server is listening on port " + port);
			byte[] receive = new byte[65535];
			DatagramPacket DpReceive = null;
			while (true) {
				DpReceive = new DatagramPacket(receive, receive.length);
				ds.receive(DpReceive);
				System.out.println("Starting a new thread for this client");
				ClientThreadHandlerUDP thu = new ClientThreadHandlerUDP(ds, storeMap, DpReceive, receive);
				Thread t = new Thread(thu);
				t.start();
			}
		}
	}

	// Implement server side for RMI
	public void runRmiProtocolServer() throws RemoteException, IOException {
		try {
			Server obj = new Server();
			KeyValueStoreRMI stub = (KeyValueStoreRMI) UnicastRemoteObject.exportObject(obj, 0);
			// System.setProperty("java.rmi.server.hostname","192.168.1.2");
			// Bind the remote object's stub in the registry
			LocateRegistry.createRegistry(1099);
			registry = LocateRegistry.getRegistry("localhost", 1099);
			registry.bind("KeyValueStoreRMI", stub);
			System.err.println("Server ready");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}

	public void implementRmiOperations(String[] taskKeyValue)
			throws RemoteException, UnknownHostException, IOException {
		StoreImplementation store = new StoreImplementation(storeMap);
		// Put implementation
		if (taskKeyValue[0].equalsIgnoreCase("put") && taskKeyValue[0] != null) {
			storeMap = store.putValuesInStrore(taskKeyValue);
			System.out.println(storeMap);
		}
		// Get implementation
		else if (taskKeyValue[0].equalsIgnoreCase("get")) {
			value = store.getValuesFromStore(taskKeyValue);
			if (value != null) {
				System.out.println(value);
			} else {
				System.out.println("No such key exist in the store");
			}
		}
		// Delete implementation
		else if (taskKeyValue[0].equalsIgnoreCase("del")) {
			storeMap = store.deleteValuesFromStore(taskKeyValue);
			System.out.println(storeMap);
		}
		// Print store information
		else if (taskKeyValue[0].equalsIgnoreCase("store")) {
			System.out.println(storeMap);
		}
		// Close the socket
		else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
            System.exit(0);
			System.out.println("exit");
		}

	}
}
