package assignment1;
import java.io.IOException;
import java.net.UnknownHostException;

public class GenericNode {

	public static void main(String[] args) throws UnknownHostException, IOException {

		// Declare and initialize variables
		int port;
		Server server = new Server();
		Client client = new Client();
		String protocol = null;
		String hostName = null;
		String task = null;
		String key = null;
		String value = null;
		
		
		if (args.length < 2)
			return;

		// If argument length is equal to 2 then call the function for server
		if (args.length == 2) {
			protocol = args[0];
			port = Integer.parseInt(args[1]);

			if (protocol.equals("tc")) {
				server.runTcpProtocolServer(port);
			} else if (protocol.equals("uc")) {
				server.runUdpProtocolServer(port);
			} else if (protocol.equals("rmis")) {
				server.runRmiProtocolServer(port);
			}
		}
		


		// If argument length is greater than 2 then call the function for client
		else if (args.length > 2) {
			protocol = args[0];
			hostName = args[1];
			port = Integer.parseInt(args[2]);

			if (args.length > 3) {
				task = args[3];
			}
			if (args.length > 4) {
				key = args[4];
			}
			if (args.length > 5) {
				value = args[5];
			}
			if (protocol.equals("tc")) {
				client.runTcpProtocolClient(hostName, port, task, key, value);
			} else if (protocol.equals("uc")) {
				client.runUdpProtocolClient(hostName, port, task, key, value);
			} else if (protocol.equals("rmic")) {
				client.runRmiProtocolClient(hostName, port, task, key, value);
			}
		}
	}
}
