///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

	/**
	 * WebServer constructor.
	 */
	protected void start() {
		ServerSocket s;

		System.out.println("Webserver starting up on port 80");
		System.out.println("(press ctrl-c to exit)");
		try {
			// create the main server socket
			s = new ServerSocket(3001);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return;
		}

		System.out.println("Waiting for connection");
		for (;;) {
			try {
				// wait for a connection
				Socket remote = s.accept();
				// remote is now the connected socket
				System.out.println("Connection, sending data.");
				BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());

				// read the data sent. We basically ignore it,
				// stop reading once a blank line is hit. This
				// blank line signals the end of the client HTTP
				// headers.
				String str = ".";
				while (str != null && !str.equals("")) {
					str = in.readLine();
					System.out.println("received : " + str);
					if (str != null && !str.equals("")) {
						String[] words = str.split(" ");
						if (words[0].toUpperCase().equals("GET")) {
							System.out.println("1111");
							if (words[1].contains("banane")) {
//								
//								BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//		                        ImageIO.write(screenshot, "jpg", out);
//								
//								
//								
//								
								//renvoi de l'image
								File imageFile = new File("./ressources/banana.jpg");
								FileInputStream fis = new FileInputStream(imageFile);
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								byte[] buf = new byte[1024];
								for (int readNum; (readNum = fis.read(buf)) != -1;) {
									bos.write(buf, 0, readNum); //no doubt here is 0
									//Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
									
								}
								byte[] bytes = bos.toByteArray();
								BufferedOutputStream output = new BufferedOutputStream(remote.getOutputStream());
								output.write(bytes);
								output.flush();
								fis.close();
								
								
								
								
//								
//								BufferedImage image = ImageIO.read(new File("./ressources/banana.jpg"));
//
//						        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//						        ImageIO.write(image, "jpg", byteArrayOutputStream);
//								
//						        byte[] size = ByteBuffer.allocate(10000).putInt(byteArrayOutputStream.size()).array();
//						        OutputStream outputStream = remote.getOutputStream();
//						        //outputStream.write(size);
//						        outputStream.write(byteArrayOutputStream.toByteArray());
//						        outputStream.flush();
//
//								byte[] bytes = Files.readAllBytes(Paths.get("./ressources/banana.jpg"));
//								for (byte b : bytes) {
//									System.out.println(b);
//								}

//								// Create OutputStream to write a file.
//								OutputStream os = new FileOutputStream(new File("./ressources/banana.jpg"));
//
//								// Create a BufferedOutputStream with buffer array size of 16384 (16384 bytes =
//								// 16 KB).
//								BufferedOutputStream br = new BufferedOutputStream(os, 16384000);
//								br.write(bytes);
								
//								
//								OutputStream outputStream = remote.getOutputStream();
//						        outputStream.write(size);
//						        outputStream.write(byteArrayOutputStream.toByteArray());
//						        outputStream.flush();

							}
						} else if (words[0].toUpperCase().equals("POST")) {

						}

					}

//					
//					
//					
//					StringTokenizer parse = new StringTokenizer(str);
//					String method = parse.nextToken().toUpperCase();
//					System.out.println("--method : " + method);
//					if (method == "GET") {
//						String fileRequested = parse.nextToken().toLowerCase();
//						System.out.println("fileRequested : " + fileRequested);
//					}

//					if(str.contains("GET")) {
//						if() {
//							
//						}else{
//							
//						}
//					}else if(str.contains("POST")) {
//						
//					}
				}
				// readAllBite
				// Send the response
				// Send the headers
//				out.println("HTTP/1.0 200 OK");
//				out.println("Content-Type: text/html");
//				out.println("Server: Bot");
//				// this blank line signals the end of the headers
//				out.println("");
//				// Send the HTML page
//				out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
//				out.flush();
				remote.close();
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

	/**
	 * Start the application.
	 * 
	 * @param args Command line parameters are not used.
	 */
	public static void main(String args[]) {
		WebServer ws = new WebServer();
		ws.start();
	}
}
