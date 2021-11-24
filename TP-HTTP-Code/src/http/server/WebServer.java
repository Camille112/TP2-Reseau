  package http.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * Modification by Camille Migozzi and Karina Du :
 * Handling of GET, HEAD, POST, PUT, DELETE requests.
 * 
 * @author Jeff Heaton, Camille Migozzi, Karina Du
 * @version 1.1
 */

public class WebServer {

	/**
	 * WebServer constructor.
	 * Start a server HTTP which read the requests and send a convenient response. 
	 */

	protected void start() {
		ServerSocket s;

		System.out.println("Webserver starting up on port 3000");
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
				String body = "";
				String nameFile = "";
				File fileCreated = null;
				boolean hasBody = false;
				boolean read = false;
				boolean newFileCreated = false;
				int contentLength = 0;
				int currentLength = 0;
				while ((str != null && !str.equals("")) || hasBody || read) {
					if (!read) {
						str = in.readLine();
						if (str != null && !str.equals("")) {
							String[] words = str.split(" ");
							String name = words[1];
							if (words[0].toUpperCase().equals("GET")) {
								if (name.contains("txt")) {
									getText(out, name);
								} else if (name.contains("html")) {
									getHtml(out, name);
								} else if (name.contains("jpg") || name.contains("png")) {
									String baliseImage = "<img src=\"data:image/png;base64,";

									byte[] bytes = Files.readAllBytes(Paths.get("../ressources/" + name));
									String encodedString = Base64.getMimeEncoder().encodeToString(bytes);

									baliseImage += encodedString;
									baliseImage += "\" />";

									out.println("HTTP/1.0 200 OK");
									out.println("Content-Type: html");
									out.println("Server: Bot");
									out.println("");
									out.println(baliseImage);
									out.flush();

								} else if (name.contains("webm") || name.contains("mp4") || name.contains("mp3")) {
									String baliseVideo = "<video controls>\r\n<source type=\"video/webm\" src=\"data:video/webm;base64,";

									byte[] bytes = Files.readAllBytes(Paths.get("../ressources/" + name));
									String encodedString = Base64.getMimeEncoder().encodeToString(bytes);

									baliseVideo += encodedString;
									baliseVideo += "\">\r\n</video>";

									out.println("HTTP/1.0 201 OK");
									out.println("Content-Type: text/html");
									out.println("Server: Bot");
									out.println("");
									out.println(baliseVideo);
									out.flush();

								} else if (name.equals("/")) {
									getIndex(out);
								} else {
									displayBadRequest(out);
								}
							} else if (words[0].toUpperCase().equals("HEAD")) {
								if (name.contains("txt") || name.contains("html") || name.contains("png")
										|| words[1].contains("jpg") || name.contains("webm") || name.contains("mp4") || name.contains("mp3")) {
									head(out, name);
								} else if (words[1].equals("/")) {
									getIndex(out);
								} else {
									out.println("HTTP/1.0 500");
									out.println("");
									out.flush();
								}
							} else if (words[0].toUpperCase().equals("DELETE")) {
								File file = null;
								if (name.contains("txt") || name.contains("html") || name.contains("png")
										|| words[1].contains("jpg")) {
									file = new File("../ressources/" + name);
									if (file.exists() && file.delete()) {
										displayDelete(out);
									} else {
										displayNotFound(out);
									}
								} else {
									displayBadRequest(out);
								}
							} else if (words[0].toUpperCase().equals("PUT")) {
								currentLength = 0;
								if (name.contains("txt") || name.contains("html")) {
									hasBody = true;
									fileCreated = new File("../ressources/" + name);
									nameFile = name;
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
									getIndex(out);
								} else {
									displayBadRequest(out);
								}
							} else if (words[0].toLowerCase().contains("content-length")) {
								contentLength = Integer.valueOf(words[1]);
							} else if (words[0].toUpperCase().equals("POST")) {
								currentLength = 0;
								if (name.contains("txt") || name.contains("html")) {
									hasBody = true;
									fileCreated = new File("../ressources/" + name);
									nameFile = name;

									try {
										if (fileCreated.createNewFile()) {
											newFileCreated = true;
										}
									} catch (Exception e) {
										displayErrorCreate(out);
									}
								} else if (name.equals("/")) {
									getIndex(out);
								} else {
									displayBadRequest(out);
								}
							}
						} else if (hasBody) {
							read = true;
							hasBody = false;
						}
					} else {
						char[] buffer = new char[contentLength];
						while (currentLength < contentLength) {
							currentLength += in.read(buffer, currentLength, contentLength - currentLength);
						}
						body = new String(buffer);
						addText(fileCreated, body);
						displayPutFile(out, newFileCreated, nameFile);
						read = false;
						newFileCreated = false;
					}
				}
				remote.close();
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

	/**
	 * This method creates the header and the information displayed on the page when the request is GET /. It shows the page index.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * 
	 */
	public void getIndex(PrintWriter out) {
		out.println("HTTP/1.0 200 OK");
		out.println("Content-Type: html");
		out.println("Server: Bot");
		out.println("");
		out.println("<H1>Index</H1>");
		out.flush();
	}

	/**
	 * This method creates the header and the information displayed on the page when there is a bad request.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * 
	 */
	public void displayBadRequest(PrintWriter out) {
		out.println("HTTP/1.0 500");
		// this blank line signals the end of the headers
		out.println("");
		// Send the HTML page
		out.println("<H1>Bad request </H1>");
		out.flush();
	}

	/**
	 * This method creates the header and the information displayed on the page when the request concerns a file not found.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * 
	 */
	public void displayNotFound(PrintWriter out) {
		out.println("HTTP/1.0 404");
		out.println("");
		out.println("<H1>404 Not found</H1>");
		out.flush();
	}

	/**
	 * This method creates the header and the information displayed on the page when the request couldn't create a file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * 
	 */
	public void displayErrorCreate(PrintWriter out) {
		out.println("HTTP/1.0 400");
		out.println("");
		out.println("<H1>400 Could not create file</H1>");
		out.flush();
	}

	/**
	 * This method creates the header and the information displayed on the page when the request delete a file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * 
	 */
	public void displayDelete(PrintWriter out) {
		out.println("HTTP/1.0 200");
		out.println("");
		out.println("<H1>File deleted</H1>");
		out.flush();
	}

	/**
	 * This method creates the header and the information displayed on the page when the request is to get a text file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * @param name The name of the text file.
	 * 
	 */
	public void getText(PrintWriter out, String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("../ressources/" + name));
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
	
	/**
	 * This method creates the header and the information displayed on the page when the request is to get a html file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * @param name The name of the html file.
	 * 
	 */
	public void getHtml(PrintWriter out, String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("../ressources/" + name));
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

	/**
	 * This method creates the header and the information displayed on the page when the request is to get the head of a file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * @param name The name of the file.
	 * 
	 */
	public void head(PrintWriter out, String name) {
		try {
			File text = new File("../ressources/" + name);
			if (text.exists()) {
				String length = String.valueOf(text.length());
				Path path = text.toPath();
				String mimeType = Files.probeContentType(path);

				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: " + mimeType);
				out.println("Content-Length: " + length);
				out.println("Server: Bot");
				out.println("");
				out.flush();
			} else {
				out.println("HTTP/1.0 404");
				out.println("");
				out.flush();
			}
		} catch (Exception e) {
			displayNotFound(out);
		}
	}
	
	/**
	 * This method adds a text to a file.
	 * 
	 * @param file The file in which you want to add a text.
	 * @param newString The string you want to add in the file.
	 * 
	 */
	public void addText(File file, String newString) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.append(newString + "\n");
			writer.close();
		} catch (Exception e) {
		}
	}

	/**
	 * This method creates the header and the information displayed on the page when the request is to put a file.
	 * 
	 * @param out The PrintWriter out prints on the socket remote.
	 * @param newFileCreated The file created.
	 * @param nameFile The name of the new file.
	 * 
	 */
	public void displayPutFile(PrintWriter out, boolean newFileCreated, String nameFile) {
		File file = new File("../ressources/" + nameFile);
		Path path = file.toPath();
		String mimeType = "";
		try {
			mimeType = Files.probeContentType(path);
		} catch (Exception e) {
		}
		String string = "";
		if (newFileCreated) {
			string = "created";
			out.println("HTTP/1.0 201 OK");
		} else {
			string = "modified";
			out.println("HTTP/1.0 200 OK");
		}
		out.println("Content-Type: " + mimeType);
		out.println("Server: Bot");
		out.println("");
		out.println("<H1>Content " + string + "</H1>");
		out.flush();
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
