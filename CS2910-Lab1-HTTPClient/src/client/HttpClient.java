package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HttpClient {
	
	public static final String CRLF = "\r\n";

	private static BufferedReader bufferedReader;
	private static DataOutputStream outputStream;
	private static FileOutputStream fos;
	private static Socket socket;
	private static File textFile;
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
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new DataOutputStream(socket.getOutputStream());
			textFile = new File("/Users/dev/Documents/text.txt");
			fos = new FileOutputStream(textFile);
			socket.setSoTimeout(1000);
			header = new HTTPHeader();
			
			outputStream.writeBytes("GET / HTTP/1.1"+CRLF+"Host: "+hostName+ CRLF+ CRLF);
			
			String s = bufferedReader.readLine();
			header.setLocation(hostName);
			while (!s.equals("")) {
				System.out.println(s);
				s = bufferedReader.readLine();
				if (s.contains(Constants.CONTENT_TYPE)) {
					System.out.println(""+s+"----\n"+s.substring(s.indexOf(":")+1,s.indexOf(";")).trim());
					boolean added = header.setContentType(s.substring(s.indexOf(":")+1,s.indexOf(";")).trim());
					if (!added) {
						System.out.println("Content-Type was not valid.\nExiting");
					}
				}
				if (s.contains(Constants.CONTENT_LENGTH)) {
					
					header.setContentLength(Integer.parseInt(s.substring(s.indexOf(":")+1).trim()));
				}
				if (s.contains(Constants.TRANSFER_ENCODING)) {
					if (s.contains("chunked")) {
						header.setChunkedEncoding(true);
					}
				}
			}
			System.out.println("done");

			System.out.println("\n\n"+header.toString());
			//TODO call you method here
			

		} catch (SocketTimeoutException ste) {
			System.out.println("connection timed out");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				//inputStream.close();
				bufferedReader.close();
				outputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
