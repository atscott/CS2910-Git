package client;

public class HTTPHeader {

	/**
	 * the route that was requested
	 */
	String location;

	/**
	 * the content type that the requested route returned
	 */
	Constants.MimeType contentType;

	/**
	 * boolean determining if the webpage is chunked or not
	 */
	boolean chunkedEncoding;

	/**
	 * if the chunkedEncoding is false, this is the length of the content to be
	 * read if the chunkedEncoding is true, this will be negative
	 */
	int contentLength;

	public HTTPHeader() {
		this.contentLength = -5;
	}

	/**
	 * Getters
	 */
	public String getLocation() {
		return this.location;
	}

	public Constants.MimeType getContentType() {
		return this.contentType;
	}

	public boolean getChunckedEncoding() {
		return this.chunkedEncoding;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	/**
	 * Setters
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public boolean setContentType(String contentType) {
		boolean validType = false;
		for (int i = 0; i < Constants.MimeType.values().length; i++) {
			if (Constants.MimeType.values()[i].contentType.equals(contentType)) {
				validType = true;
				this.contentType = Constants.MimeType.values()[i];
				break;
			}
		}
		return validType;
	}

	public void setChunkedEncoding(boolean chunkedEncoding) {
		this.chunkedEncoding = chunkedEncoding;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	@Override
	public String toString() {
		String header = "";

		header += "Content-Type: " + contentType.contentType;
		header += "\nTransfer-Encoding: " + chunkedEncoding;
		header += "\nLocation: " + location;
		header += "\nContent-Length: " + contentLength;

		return header;
	}
}