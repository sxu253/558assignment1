import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	// Variables
	HashMap<String, String> storeMap = new HashMap<>();
	String value;
	String key;

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
				implementOperations(socket, writer, task);
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	// Implement server side socket for UDP
	public void runUdpProtocolServer(int port) {

		try (DatagramSocket serverSocket = new DatagramSocket(port)) {

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();

		}

	}

	// implements conditions for different operations
	private void implementOperations(Socket socket, PrintWriter writer, String task) throws IOException {

		System.out.println(task);
		if (task != null) {
			String[] taskKeyValue = task.split(" ");
			System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));

			// PUT implementation
			if (taskKeyValue[0] != null && taskKeyValue[0].equalsIgnoreCase("put")) {
				storeMap = putValuesInStrore(taskKeyValue);
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
				socket.close();
			}
		}
	}

	// Put key-values in a hash-map
	private HashMap<String, String> putValuesInStrore(String[] taskKeyValue) {

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
