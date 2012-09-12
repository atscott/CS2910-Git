package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * Course: CS2910
 * Term: Sept. 2012
 * Assignment: Lab 1
 * Author: Andrew Scott
 * Date: 9/10/12
 */

/**
 * a simple Http client that sends a request to a web server and interprets the
 * response.
 */
public class HttpClient {

	/**
	 * The address of the host
	 */
	private String host;

	/**
	 * The port that the client will use
	 */
	private int port;

	/**
	 * socket for the client
	 */
	private Socket socket;

	/**
	 * 
	 */
	private DataOutputStream transmitStream;

	private String resourceID;

	/**
	 * The end-of-line string that will work for any system
	 */
	final static String CRLF = "\r\n";

	/**
	 * @param host
	 * @param port
	 */
	HttpClient(String host, int port, String resource) {
		this.host = host;
		this.port = port;
		this.resourceID = resource;
	}

	/**
	 * 
	 */
	public void run() {
		boolean ok = openSocket();

		if (ok) {
			ok = initTransmit();
		}

		if (ok) {
			ok = sendRequest();
		}

		if (ok) {
			System.out.println(getResponse());
		}

		if (ok) {
			System.out.println("run: successful exit");
		} else {
			System.out.println("run: error exit");
		}

	}

	private boolean openSocket() {
		boolean ok = false;

		try {
			socket = new Socket(this.host, port);
			ok = true;
			System.out.println("openSocket: socket created");
		} catch (UnknownHostException e) {
			System.out.println("openSocket: UnknownHostException");
		} catch (IOException e) {
			System.out.println("openSocket: IOException");
		}

		return ok;
	}

	private boolean initTransmit() {
		boolean ok = false;

		try {
			this.transmitStream = new DataOutputStream(socket.getOutputStream());
			ok = true;
			System.out.println("transmitStream: stream created");
		} catch (IOException e) {
			System.out.println("transmitStream: IOException");
		}

		return ok;
	}

	private boolean sendRequest() {
		boolean ok = false;

		String request = "GET " + resourceID + " HTTP/1.1" + HttpClient.CRLF
				+ "Host: " + host + HttpClient.CRLF + HttpClient.CRLF;

		try {
			transmitStream.writeBytes(request);
			ok = true;
			System.out.println("sendRequest: request sent");
		} catch (IOException e) {
			System.out.println("sendRequest: IOException");
		}

		return ok;
	}

	private String getResponse() {

		StringBuilder sb = new StringBuilder();

		try {

			FileWriter fStream = new FileWriter("out");
			BufferedWriter out = new BufferedWriter(fStream);

			try {
				socket.setSoTimeout(1000);

				BufferedReader responseFromServer = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				InputStream is = socket.getInputStream();

				int result;
				boolean isHeader = true;
				while ((result = responseFromServer.read()) != -1) {
					char[] chars = Character.toChars(result);
					for (char c : chars) {

						if (isHeader) {
							sb.append(c);
						} else {
							out.append(c);
						}
						if (isHeader && sb.toString().endsWith("\r\n\r\n")) {
							isHeader = false;
						}

					}
				}

			} catch (SocketTimeoutException e) {
				System.out.println("getResponse: SocketTimeoutException");
			} catch (IOException e) {
				System.out.println("getResponse: IOException");
			}

			out.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return sb.toString();
	}

}
