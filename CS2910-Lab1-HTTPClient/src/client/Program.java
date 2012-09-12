package client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String hostName = getInfo("Enter Hostname: ");
		Integer port = null;
		while (port == null) {
			try {
				port = Integer.parseInt(getInfo("Enter port: "));
			} catch (NumberFormatException e) {
				System.out.println("Enter a valid number");
			}
		}

		HttpClient client = new HttpClient(hostName, port);
		client.run();
	}

	private static String getInfo(String prompt) {
		Scanner scan = new Scanner(System.in);
		System.out.print(prompt);
		return scan.nextLine();
	}

}
