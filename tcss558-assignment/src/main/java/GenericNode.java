/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 *This program takes in CLI arguments, and establishes TCP, UDP, or RMI connections.
 */
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
		
		// Determines which server type is to be run 
		if (args.length == 1) {
			protocol = args[0];
			if (protocol.equals("rmis")) {
				server.runRmiProtocolServer();
			}
		} else if (args.length == 2) {
			protocol = args[0];
			port = Integer.parseInt(args[1]);

			if (protocol.equals("ts")) {
				server.runTcpProtocolServer(port);
			} else if (protocol.equals("us")) {
				server.runUdpProtocolServer(port);
			}
		}
		// Determines which client type is to be run
		else if (args.length > 1) {
			protocol = args[0];
			hostName = args[1];
			if (protocol.equals("rmic")) {
				// rmic localhost store = 3
				if (args.length > 2) {
					task = args[2];
				}
				// rmic localhost get a = 4
				if (args.length > 3) {
					key = args[3];
				}
				// rmic localhost put a 123 = 5
				if (args.length > 4) {
					value = args[4];
				}
				client.runRmiProtocolClient(hostName, task, key, value);

			} else {
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
				}
			}
		}
	}
}