///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
			s = new ServerSocket(3000);
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
				String typeRequest = "";
				String body = "";
				String fileType = "";
				File fileCreated = null;
				boolean hasBody = false;
				boolean read = false;
				boolean newFileCreated = false;
				int contentLength = 0;
				int currentLength = 0;
				//while (i<70) {
				while ((str != null && !str.equals("")) || hasBody || read) {
					System.out.println("read : " + read);
					System.out.println("body : " + hasBody);
					if (!read) { 
						str = in.readLine();
						System.out.println("received : " + str);
						if (str != null && !str.equals("")) {
							String[] words = str.split(" ");
							if (words[0].toUpperCase().equals("GET")) {
								if (words[1].contains("text")) {
									String name = words[1].substring(words[1].lastIndexOf("/") + 1);
									displayText(out, name);
								} else if (words[1].contains("html")) {
									String name = words[1].substring(words[1].lastIndexOf("/") + 1);
									displayHtml(out, name);
								} else if (words[1].equals("/")) {
									displayIndex(out);
								} else {
									displayBadRequest(out);
								}
							} else if (words[0].toUpperCase().equals("DELETE")) {
								String name = words[1].substring(words[1].lastIndexOf("/") + 1);
								File file = null;
								if (words[1].contains("text")) {
									file = new File("../ressources/" + name + ".txt");
								} else if (words[1].contains("html")) {
									file = new File("../ressources/" + name + ".html");
								} else {
									displayBadRequest(out);
								}
								System.out.println("EXISTS" + file.exists());
								if (file != null && file.delete()) {
									System.out.println("success");
									displayDelete(out);
								} else {
									System.out.println("fail");
									displayNotFound(out);
								}
							} else if (words[0].toUpperCase().equals("PUT")) {
								typeRequest = "PUT";
								hasBody = true;
								currentLength=0;
								if (words[1].contains("text")) {
									String name = words[1].substring(words[1].lastIndexOf("/") + 1);
									fileCreated = new File("../ressources/" + name + ".txt");
									fileType = "txt";
									try {
										if (fileCreated.createNewFile()) {
											newFileCreated = true;
										} else {
											PrintWriter writer = new PrintWriter(fileCreated);
											writer.print("");
											writer.close();
										}
									} catch (Exception e) {
										displayErrorCreate(out);
									}
								} else if (words[1].contains("html")) {
									String name = words[1].substring(words[1].lastIndexOf("/") + 1);
									fileCreated = new File("../ressources/" + name + ".html");
									fileType = "html";
									try {
										if (fileCreated.createNewFile()) {
											newFileCreated = true;
										} else {
											PrintWriter writer = new PrintWriter(fileCreated);
											writer.print("");
											writer.close();
										}
									} catch (Exception e) {
										displayErrorCreate(out);
									}
								} else if (words[1].equals("/")) {
									displayIndex(out);
								} else {
									displayBadRequest(out);
								}
							} else if (words[0].toLowerCase().contains("content-length")){
								contentLength = Integer.valueOf(words[1]);
								System.out.println("CONTENTLENGTH"+contentLength);
							}
						} else if (hasBody){
							System.out.println("READ = TRUE");
							read = true;
							hasBody = false;
						}
					}else {
						/*
						currentLength++;
						char character = (char)in.read();
						body +=character;
						if (currentLength == contentLength) {
							addText(fileCreated,body);
							read = false;
						}*/
						char[] buffer = new char[contentLength];
						while (currentLength < contentLength) {
						  currentLength += in.read(buffer, currentLength, contentLength-currentLength);
						}
						body = new String(buffer);
						addText(fileCreated,body);
						if (newFileCreated) {
							if (fileType == "txt") {
								out.println("HTTP/1.0 201 OK");
								out.println("Content-Type: text");
								out.println("Server: Bot");
								out.println("");
								out.println("<H1>Text created</H1>");
								out.flush();
							}else {
								out.println("HTTP/1.0 201 OK");
								out.println("Content-Type: html");
								out.println("Server: Bot");
								out.println("");
								out.println("<H1>Html created</H1>");
								out.flush();
							}
						} else {
							if (fileType == "txt") {
								out.println("HTTP/1.0 200 OK");
								out.println("Content-Type: text");
								out.println("Server: Bot");
								out.println("");
								out.println("<H1>Text modified</H1>");
								out.flush();
							}else {
								out.println("HTTP/1.0 200 OK");
								out.println("Content-Type: html");
								out.println("Server: Bot");
								out.println("");
								out.println("<H1>Html modified</H1>");
								out.flush();
							}
						}
						read = false;
						newFileCreated=false;
					}
					System.out.println("String:" + str);
					System.out.println("CONTENTLENGTH"+contentLength);
				}
				remote.close();
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

	public void displayIndex(PrintWriter out) {
		out.println("HTTP/1.0 200 OK");
		out.println("Content-Type: html");
		out.println("Server: Bot");
		out.println("");
		out.println("<H1>Index</H1>");
		out.flush();
	}

	public void displayBadRequest(PrintWriter out) {
		out.println("HTTP/1.0 500");
		// this blank line signals the end of the headers
		out.println("");
		// Send the HTML page
		out.println("<H1>Bad request </H1>");
		out.flush();
	}

	public void displayNotFound(PrintWriter out) {
		out.println("HTTP/1.0 404");
		out.println("");
		out.println("<H1>404 Not found</H1>");
		out.flush();
	}

	public void displayErrorCreate(PrintWriter out) {
		out.println("HTTP/1.0 400");
		out.println("");
		out.println("<H1>400 Could not create file</H1>");
		out.flush();
	}

	public void displayDelete(PrintWriter out) {
		out.println("HTTP/1.0 200");
		out.println("");
		out.println("<H1>File deleted</H1>");
		out.flush();
	}

	public void displayText(PrintWriter out, String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("../ressources/" + name + ".txt"));
			String line;
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type: text");
			out.println("Server: Bot");
			out.println("");
			out.println("<H1>The text</H1>");
			while ((line = br.readLine()) != null && line.length() != 0) {
				out.println(line);
				out.println("<br/>");
			}
			out.flush();
		} catch (Exception e) {
			displayNotFound(out);
		}
	}

	public void displayHtml(PrintWriter out, String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("../ressources/" + name + ".html"));
			String line;
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type: html");
			out.println("Server: Bot");
			out.println("");
			out.println("<H1>The html</H1>");
			while ((line = br.readLine()) != null || line == "") {
				out.println(line);
			}
			out.flush();
		} catch (Exception e) {
			displayNotFound(out);
		}
	}

	
	public void addText(File file, String newString) {
		try {
		    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		    writer.append(newString);
		    writer.close();
		}catch(Exception e) {
		}
	}


	public void putHtml(PrintWriter out, String name) {
		File fileName = new File("../ressources/" + name + ".html");
		try {
			if (fileName.createNewFile()) {
				out.println("HTTP/1.0 201 OK");
				out.println("Content-Type: html");
				out.println("Server: Bot");
				out.println("");
				out.println("<H1>Text created</H1>");
				out.flush();
			} else {
				displayErrorCreate(out);
			}
		} catch (Exception e) {
			displayErrorCreate(out);
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
