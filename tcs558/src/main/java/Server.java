import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	// Variables
	HashMap<String, String> storeMap = new HashMap<>();
	String value;
	String key;
	private byte[] buf = new byte[256];
	private DatagramSocket socket;

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
					System.out.println("Null task");
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


}
