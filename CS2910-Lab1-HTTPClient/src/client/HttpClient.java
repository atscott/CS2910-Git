
package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HttpClient {

    public static final String CRLF = "\r\n";

    private static DataInputStream inputStream;
    private static Socket socket;
    private static HTTPHeader header;

    /**
     * @param args
     */
    public static void main(String[] args) {
        UI ui = new UI();
        ui.setVisible(true);

        // Include a shutdown hook to close connections and streams. This is not guaranteed to run
        // if the JVM exits ungracefully.
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("unable to close the socket.");
                        e.printStackTrace();
                    }
                }
                
                
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void startRequest(String hostName, int port, String resource) {
        boolean allGood = true;

        allGood = connectToHost(hostName, port);

        if (allGood) {
            allGood = sendRequest(hostName, resource);
        }
        
        if (allGood) {
            allGood = getData();
        }

        if (allGood) {
            allGood = parseHeader(hostName);
        }

        if (allGood) {
            allGood = parseBody(resource);
        }
    }

    private static boolean connectToHost(String hostName, int port) {
        boolean allGood = true;

        // Is closed in shutdown hook
        try {
            socket = new Socket(hostName, port);
            socket.setSoTimeout(3000);
        } catch (SocketTimeoutException ste) {
            System.out.println("connection timed out");
            allGood = false;
        } catch (UnknownHostException e) {

            // Might happen if server goes offline between check in UI and socket call in here
            System.out.println("Is the server offline?");
            allGood = false;
            e.printStackTrace();
        } catch (IOException e) {
            allGood = false;
            e.printStackTrace();
        }
        
        return allGood;
    }

    private static boolean sendRequest(String hostName, String resource) {
        boolean allGood = true;

        // Is closed along with socket in shutdown hook automatically. no explicit instructions need to be given.
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("GET " + resource + " HTTP/1.1" + CRLF
                    + "Host: " + hostName + CRLF + CRLF);
        } catch (IOException e) {
            System.out
                    .println("Unable to send the HTTP request. Check your inputs and try again.");
            allGood = false;
        }

        return allGood;
    }

    private static boolean parseHeader(String hostName) {
        boolean allGood = true;

        header = new HTTPHeader();
        header.setLocation(hostName);

        String s = "";
        while (!s.equals(CRLF)) {
            StringBuilder sb = new StringBuilder();
            while (!sb.toString().endsWith(CRLF)) {
                try {
                    sb.append((char) inputStream.read());
                } catch (IOException e) {
                    System.out.println("Unable to read header information from the host.");
                    e.printStackTrace();
                }
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

        // Prints out the human readable HTTPHeader class
        System.out.println("\n\n" + header.toString());

        return allGood;
    }

    private static boolean parseBody(String resource) {
        boolean allGood = true;
        
        // Break mime types into general categories & handle the HTTP body
        switch (header.getGenericContentType()) {
            case TEXT:
                parseText();
                break;
            case RAW:
                saveRawBytes(resource);
                break;
            default:
                System.out.println("Invalid content type");
                allGood = false;
        }
        
        return allGood;
    }
    
    private static boolean getData() {
        boolean allGood = true;
        
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Unable to receive data from the host.\n");
            e.printStackTrace();
            allGood = false;
        }
        
        return allGood;
    }

    private static void saveRawBytes(String resource) {

        String name = "GenericOutput";
        int index = resource.lastIndexOf('/');
        if (index >= 0) {
            name = resource.substring(index + 1);
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
                            .println("Could not read to EOF. Total bytes read: "
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

    /**
     * Creates a new file using the location as a filename and mime type as an extension.
     */
    private static void parseText() {

        // Create a unique filename for the data being retrieved
        String filename = header.getLocation();
        filename = filename.replace('.', '-');

        // Add the proper extension based on mime type
        filename = filename.concat("." + header.getContentType().contentType.substring(5));

        // Create the new file. Ensure it is available and clean
        File output = new File(filename);
        output.delete();
        try {
            output.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create the new file");
        }

        if (header.chunkedEncoding) {
            parseChunkedText(output);
        } else {
            parseUnchunckedText(output);
        }

    }

    /**
     * Reads and writes chunked text content to a file
     */
    private static void parseChunkedText(File output) {

        PrintWriter mWriter = null;
        BufferedReader bufferedReader = null;

        try {
            mWriter = new PrintWriter(output);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = bufferedReader.readLine()) != null;) {

                try {
                    if (Integer.parseInt(line) == 0) {
                        break;
                    }

                } catch (NumberFormatException nfe) {
                    // Ignore this exception.
                }

                mWriter.append(line);
            }
        } catch (IOException e) {
            System.out.println("Unable to write chunked text to file");
        } finally {
            if (mWriter != null) {
                mWriter.close();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Reads and writes unchunked text content to a file
     */
    private static void parseUnchunckedText(File output) {

        PrintWriter mWriter = null;
        BufferedReader bufferedReader = null;

        int expectedCount = header.contentLength;

        try {
            mWriter = new PrintWriter(output);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            for (int i = 0; i < expectedCount; i++) {
                char toPrint = (char) bufferedReader.read();
                mWriter.append(toPrint);
            }

        } catch (IOException e) {
            System.out.println("Unable to write unchunked text to file");
        } finally {
            if (mWriter != null) {
                mWriter.close();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
