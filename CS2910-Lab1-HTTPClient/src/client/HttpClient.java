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

//	private static BufferedReader bufferedReader;
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
//			bufferedReader = new BufferedReader(new InputStreamReader(
//					socket.getInputStream()));
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			textFile = new File("text.txt");
			fos = new FileOutputStream(textFile);
			socket.setSoTimeout(3000);
			header = new HTTPHeader();

			outputStream.writeBytes("GET " + resource + " HTTP/1.1" + CRLF
					+ "Host: " + hostName + CRLF + CRLF);
			
			header.setLocation(hostName);
			
			String s = "";
			while (!s.equals("\r\n")) {
				StringBuilder sb = new StringBuilder();
				while(!sb.toString().endsWith("\r\n")){
					sb.append((char) socket.getInputStream().read());
				}
				s = sb.toString();
				System.out.print(s);
				if (s.contains(Constants.CONTENT_TYPE)) {
					// Looks for the semi-colon that indicates a charset is
					// given, this will
					// successfully take out the Content-Type
					int index = s.indexOf(";");
					boolean added;
					if (index == -1) {
						// System.out.println(s.substring(s.indexOf(":") +
						// 1).trim());
						added = header.setContentType(s.substring(
								s.indexOf(":") + 1).trim());
					} else {
						// System.out.println(s.substring(s.indexOf(":")+1,s.indexOf(";")).trim());
						added = header.setContentType(s.substring(
								s.indexOf(":") + 1, s.indexOf(";")).trim());
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


				saveRawBytes(inputStream, header, resource);
			
			// Prints out the human readable HTTPHeader class
			System.out.println("\n\n" + header.toString());
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
//				bufferedReader.close();
				outputStream.close();
				inputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static void saveRawBytes(DataInputStream inputStream,
			HTTPHeader header, String resource) {

		String name = "GenericOutput";
		int index = resource.lastIndexOf('/');
		if (index >= 0) {
			name = resource.substring(index+1);
		}

		FileOutputStream pngStream = null;
		try {
			File pngFile = new File(name);
			pngStream = new FileOutputStream(pngFile);
			int totalBytesRead = 0;
			byte[] data = new byte[1024];
			while (totalBytesRead < header.getContentLength()) {
				int toRead = Math.min(1024, header.getContentLength()
						- totalBytesRead);
				int readBytes = inputStream.read(data, 0, toRead);
				// System.out.println(""+byteToWrite);
				if (readBytes > 0) {
					pngStream.write(data);
					totalBytesRead += readBytes;
				} else {
					System.out
							.println("Could note read to EOF. Total bytes read: "
									+ totalBytesRead
									+ ". Supposed to have read: "
									+ header.getContentLength());
					totalBytesRead = header.getContentLength();
				}
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
