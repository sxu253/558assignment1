
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;

public class Server implements KeyValueStoreRMI {

	// Variables
	HashMap<String, String> storeMap = new HashMap<>();
	HashMap<String, String> storeMapRmi = new HashMap<>();
	String value;
	String key;
	Registry registry;
	// Implement server side socket for TCP
	public void runTcpProtocolServer(int port) {

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);

				// Read the task key and value in a string
				String task = reader.readLine();

				// call function to implement the operations
				implementTcpOperations(socket, writer, task);
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	// implements conditions for different operations
		private void implementTcpOperations(Socket socket, PrintWriter writer, String task) throws IOException {

			System.out.println(task);
			if (task != null) {
				String[] taskKeyValue = task.split(" ");
				System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));

				// PUT implementation
				if (taskKeyValue[0] != null && taskKeyValue[0].equalsIgnoreCase("put")) {
					storeMap = putValuesInStore(taskKeyValue);
					System.out.println(storeMap);
				}

				// GET implementation
				else if (taskKeyValue[0].equalsIgnoreCase("get")) {
					value = getValuesFromStore(taskKeyValue);
					if (value != null) {
						writer.println(value);
					} else {
						writer.println("No such key exist in the store");
					}
				}

				// Delete implementation
				else if (taskKeyValue[0].equalsIgnoreCase("del")) {
					storeMap = deleteValuesFromStore(taskKeyValue);
					System.out.println(storeMap);
				}

				// Print store information
				else if (taskKeyValue[0].equalsIgnoreCase("store")) {
					writer.println(storeMap);
				}

				// Close the socket
				else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
					System.out.println("Exit connection");
					socket.close();
				}
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
				// Created DatagramPacket to receive the data
				DpReceive = new DatagramPacket(receive, receive.length); 
				
				// DatagramSocket receives the DatagramPacket
				ds.receive(DpReceive); 
				
				// Print out data sent by client, received by server
				System.out.println("Client: " + data(receive)); 
				StringBuilder dataInput = data(receive); 
				// Create string array that contains: task, key, value
				String[] taskKeyValue = dataInput.toString().split(" ");  
				if (taskKeyValue[0] != null) {
					implementUdpOperations(taskKeyValue, ds);
				} else {
					System.out.println("uc/tc <address> <port> put <key> <msg> UDP/TCP CLIENT: Put an object into store\n"
							+ "uc/tc <address> <port> get <key> UDP/TCP CLIENT: Get an object from store by key\n"
							+ "uc/tc <address> <port> del <key> UDP/TCP CLIENT: Delete an object from store by key\n"
							+ "uc/tc <address> <port> store UDP/TCP CLIENT: Display object store\n"
							+ "uc/tc <address> <port> exit UDP/TCP CLIENT: Shutdown server ");
				}
				receive = new byte[65535]; 
			} 
		}
	} 

	private void implementUdpOperations(String[] taskKeyValue, DatagramSocket ds) throws IOException {
		
		// Put implementation
		if (taskKeyValue[0].equalsIgnoreCase("put") && taskKeyValue[0] != null) {
			storeMap = putValuesInStore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Get implementation
		else if (taskKeyValue[0].equalsIgnoreCase("get")) {
			value = getValuesFromStore(taskKeyValue);
			if (value != null) {
				System.out.println(value);
			} else {
				System.out.println("No such key exist in the store");
			}
		}

		// Delete implementation
		else if (taskKeyValue[0].equalsIgnoreCase("del")) {
			storeMap = deleteValuesFromStore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Print store information
		else if (taskKeyValue[0].equalsIgnoreCase("store")) {
			System.out.println(storeMap);
		}

		// Close the socket
		else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
			System.out.println("Exit connection");
			ds.close();
		}

	}

	// A utility method to convert the sent byte array data into a string representation 
	public static StringBuilder data(byte[] a) 
	{ 
		if (a == null) 
			return null; 
		StringBuilder ret = new StringBuilder(); 
		int i = 0; 
		while (a[i] != 0) 
		{ 
			ret.append((char) a[i]); 
			i++; 
		} 
		return ret; 
	} 
	


	// Put key-values in a hash-map
	private HashMap<String, String> putValuesInStore(String[] taskKeyValue) {

		key = taskKeyValue[1];
		value = taskKeyValue[2];

		storeMap.put(key, value);
		return storeMap;
	}

	// Get values for the given key
	private String getValuesFromStore(String[] taskKeyValue) {

		if (storeMap.get(taskKeyValue[1]) != null) {
			String value = storeMap.get(taskKeyValue[1]);
			return value;
		}
		return null;
	}

	// Delete key-value from hash-map
	private HashMap<String, String> deleteValuesFromStore(String[] taskKeyValue) {

		if (storeMap.get(taskKeyValue[1]) != null) {
			storeMap.remove(taskKeyValue[1]);
		}

		return storeMap;
	}


	@Override
	public void runRmiProtocolServer() throws RemoteException, IOException {
		try {

            Server obj = new Server();
            KeyValueStoreRMI stub = (KeyValueStoreRMI) UnicastRemoteObject.exportObject(obj, 0);
            //System.setProperty("java.rmi.server.hostname","192.168.1.2");
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
	

	@Override
	public void implementRmiOperations(String[] taskKeyValue)
			throws RemoteException, UnknownHostException, IOException {
		// Put implementation
		if (taskKeyValue[0].equalsIgnoreCase("put") && taskKeyValue[0] != null) {
			storeMap = putValuesInStore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Get implementation
		else if (taskKeyValue[0].equalsIgnoreCase("get")) {
			value = getValuesFromStore(taskKeyValue);
			if (value != null) {
				System.out.println(value);
			} else {
				System.out.println("No such key exist in the store");
			}
		}

		// Delete implementation
		else if (taskKeyValue[0].equalsIgnoreCase("del")) {
			storeMap = deleteValuesFromStore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Print store information
		else if (taskKeyValue[0].equalsIgnoreCase("store")) {
			System.out.println(storeMap);
		}

		// Close the socket
		else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
//			try{
//                //Naming.unbind(mServerName);
//                registry.unbind("KeyValueStoreRMI");
//                System.out.println("CalculatorServer exiting.");
//            }
//            catch(Exception e){}

//            System.exit(1);
			System.out.println("exit");

		}

	}





}
