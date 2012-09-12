package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class HttpClient {
	
	public static final String CRLF = "\r\n";

	private static DataInputStream inputStream;
	private static DataOutputStream outputStream;
	private static FileOutputStream fos;
	private static Socket socket;
	private static File textFile;
	private static File outputFile;
	private static HTTPHeader header;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UI ui = new UI();
	}

	public static void startRequest(String hostName, int port) {
		try {
			socket = new Socket(hostName, port);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			textFile = new File("/Users/dev/Documents/text.txt");
			fos = new FileOutputStream(textFile);
			socket.setSoTimeout(1000);
			
			outputStream.writeBytes("GET / HTTP/1.1"+CRLF+"Host: "+hostName+ CRLF+ CRLF);
			
			int n = 0;
			byte[] bytes = new byte[1024];
			while ((n = inputStream.read(bytes)) > 0) {
				fos.write(bytes, 0, n);
				System.out.println("writing "+n);
			}
			System.out.println("done");

			//parseHtml(true,transferEncoding);
			//parseHtml(inputStream, HTTPHeader);

		} catch (SocketTimeoutException ste) {
			System.out.println("connection timed out");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				inputStream.close();
				outputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
