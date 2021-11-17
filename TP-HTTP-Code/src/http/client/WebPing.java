package http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage java WebPing <server host name> <server port number>");
			return;
		}

		String httpServerHost = null;
		int httpServerPort = Integer.parseInt(args[0]);

		try {
			InetAddress addr;
			Socket sock = new Socket(httpServerHost, httpServerPort);
			addr = sock.getInetAddress();
			System.out.println("Connected to " + addr);
			sendGet(sock);

			sock.close();
		} catch (java.io.IOException e) {
			System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
			System.out.println(e);
		}
	}

	private static void sendGet(Socket sock) throws Exception {
		
		BufferedReader socIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		PrintStream socOut = new PrintStream(sock.getOutputStream());
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("send : "+"GET /index.html");
		socOut.println("GET /index.html");
		socOut.println("");
		
		String str = ".";
		while (str != null && !str.equals("")) {
			str = socIn.readLine();
			System.out.println("received : "+str);
		}
//		HttpGet request = new HttpGet("https://www.google.com/search?q=mkyong");
//
//		// add request headers
//		request.addHeader("custom-key", "mkyong");
//		request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
//
//		try (CloseableHttpResponse response = httpClient.execute(request)) {
//
//			// Get HttpResponse Status
//			System.out.println(response.getStatusLine().toString());
//
//			HttpEntity entity = response.getEntity();
//			Header headers = entity.getContentType();
//			System.out.println(headers);
//
//			if (entity != null) {
//				// return it as a String
//				String result = EntityUtils.toString(entity);
//				System.out.println(result);
//			}
//
//		}

	}
}