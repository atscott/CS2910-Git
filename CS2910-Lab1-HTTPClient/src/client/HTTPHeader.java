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

	/**
	 * Sets the content type only if it exists in the MimeType enum, contentType will be unchanged if the passed
	 * in string is not valid
	 * @param contentType
	 * @return true if the content type was valid, otherwise false
	 */
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
	
	public Constants.GenericContentType getGenericContentType() {
	    if (this.contentType.contentType.contains("text/")) {
	        return Constants.GenericContentType.TEXT;
	    } else {
	        return Constants.GenericContentType.RAW;
	    }
	}

	public void setChunkedEncoding(boolean chunkedEncoding) {
		this.chunkedEncoding = chunkedEncoding;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * Overrides the Object's toString method to print out all the information about this HTTPHeader class
	 * in human readable form
	 */
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