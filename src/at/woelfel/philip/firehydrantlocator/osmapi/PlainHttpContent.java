package at.woelfel.philip.firehydrantlocator.osmapi;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpMediaType;

public class PlainHttpContent extends AbstractHttpContent {

	String content;
	
	public PlainHttpContent(String content, String mediaType) {
		super(mediaType);
		this.content = content;
	}
	
	protected PlainHttpContent(String mediaType) {
		super(mediaType);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(content.getBytes());
		//out.flush();
	}

	
}
