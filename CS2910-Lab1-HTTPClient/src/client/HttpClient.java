package client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import client.Constants.MimeType;

public class HttpClient {

	public static final String CRLF = "\r\n";

	private static BufferedReader bufferedReader;
	private static DataOutputStream outputStream;
	private static DataInputStream inputStream;
	private static FileOutputStream fos;
	private static Socket socket;
	private static File textFile;
	private static HTTPHeader header;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UI ui = new UI();
		ui.setVisible(true);
	}

	public static void startRequest(String hostName, int port, String resource) {
		try {
			socket = new Socket(hostName, port);
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			textFile = new File("text.txt");
			fos = new FileOutputStream(textFile);
			//socket.setSoTimeout(1000);
			header = new HTTPHeader();

			outputStream.writeBytes("GET " + resource + " HTTP/1.1" + CRLF
					+ "Host: " + hostName + CRLF + CRLF);

			String s = bufferedReader.readLine();
			header.setLocation(hostName);
			while (!s.equals("")) {
				System.out.println(s);
				s = bufferedReader.readLine();
				if (s.contains(Constants.CONTENT_TYPE)) {
					//Looks for the semi-colon that indicates a charset is given, this will
					//successfully take out the Content-Type
					int index = s.indexOf(";");
					boolean added;
					if (index == -1) {
						//System.out.println(s.substring(s.indexOf(":") + 1).trim());
						added = header.setContentType(s.substring(s.indexOf(":") + 1).trim());
					} else {
						//System.out.println(s.substring(s.indexOf(":")+1,s.indexOf(";")).trim());
						added = header.setContentType(s.substring(s.indexOf(":")+1,s.indexOf(";")).trim());
					}
					if (!added) {
						System.out.println("Content-Type was not valid.");
					}
				}
				if (s.contains(Constants.CONTENT_LENGTH)) {

					header.setContentLength(Integer.parseInt(s.substring(
							s.indexOf(":") + 1).trim()));
				}
				if (s.contains(Constants.TRANSFER_ENCODING)) {
					if (s.contains("chunked")) {
						header.setChunkedEncoding(true);
					}
				}
			}
			
			if (header.contentType == MimeType.png) {
				saveRawBytes(inputStream, header, resource);
			}
			//Prints out the human readable HTTPHeader class
			System.out.println("\n\n"+header.toString());
			// TODO call you method here

		} catch (SocketTimeoutException ste) {
			System.out.println("connection timed out");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				bufferedReader.close();
				outputStream.close();
				inputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


	private static void saveRawBytes(DataInputStream inputStream, HTTPHeader header, String resource) {
		
		String extension = "";
		int index = resource.lastIndexOf('.');
		if(index >= 0){
			extension = resource.substring(index);
		}
		
		FileOutputStream pngStream = null;
		try {
			File pngFile = new File("output" + extension);
			pngStream = new FileOutputStream(pngFile);
			
			int bytesRead = 0;
			while (bytesRead < header.getContentLength()) {
				int byteToWrite = inputStream.read();
				//System.out.println(""+byteToWrite);
				
					pngStream.write(byteToWrite);
					bytesRead++;
				
			}


		} catch (IOException e) {
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
