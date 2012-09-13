package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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

	public static void startRequest(String hostName, int port, String resource) {
		try {
			socket = new Socket(hostName, port);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			textFile = new File("abc.txt");
			fos = new FileOutputStream(textFile);
			socket.setSoTimeout(1000);

			outputStream.writeBytes("GET " + resource + " HTTP/1.1" + CRLF
					+ "Host: " + hostName + CRLF + CRLF);

			
			// This will read the entire header. sb.toString() is the header as a string
			// I will leave it up to you to parse it. Or rewrite the entire header reading.
			StringBuilder sb = new StringBuilder();
			while (!sb.toString().endsWith("\r\n\r\n")) {
				sb.append(inputStream.readChar());
			}
			
			
			savePNG(inputStream);
			
			System.out.println("done");

			// parseHtml(true,transferEncoding);
			// parseHtml(inputStream, HTTPHeader);

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

	private static void savePNG(DataInputStream is) {
		FileOutputStream pngStream = null;
		try {
			File pngFile = new File("abc.png");
			pngStream = new FileOutputStream(pngFile);
			
			int n = 0;
			byte[] bytes = new byte[1024];
			while ((n = inputStream.read(bytes)) > 0) {
				pngStream.write(bytes, 0, n);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pngStream != null) {
					pngStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
