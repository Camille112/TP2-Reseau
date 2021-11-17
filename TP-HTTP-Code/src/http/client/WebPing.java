package http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("Usage java WebPing <server host name> <server port number>");
			return;
		}

		String httpServerHost = args[0];
		int httpServerPort = Integer.parseInt(args[1]);
		httpServerHost = args[0];
		httpServerPort = Integer.parseInt(args[1]);
		
		PrintStream socOut = null;
		BufferedReader stdIn = null;
		BufferedReader socIn = null;

		try {
			InetAddress addr;
			Socket sock = new Socket("", httpServerPort);
			addr = sock.getInetAddress();
			socIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			socOut = new PrintStream(sock.getOutputStream());
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Connected to " + addr);        
			socOut.println("test");
			sock.close();
		} catch (java.io.IOException e) {
			System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
			System.out.println(e);
		}
	}
}