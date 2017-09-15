import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

public class SlackMessenger {
	final static Logger slackMessengerLogger = Logger.getLogger(SlackMessenger.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("SlackMessenger");
	private final String url = bundle.getString("url");

	public String sendMessenge(String sender, String message, String channel) {
		String responseToClient = null;
		try {
			validateClientInput(message, channel);
			responseToClient = sendMessengeToSlack(sender, message, channel);
		} catch (Exception e) {
			slackMessengerLogger.error("Validation failed");
			responseToClient = "Wrong input: " + e.getMessage().toLowerCase();
		}
		return responseToClient;
	}

	private String sendMessengeToSlack(String sender, String message, String channel) {
		HttpResponse response = null;
		HttpClient client = HttpClientBuilder.create().build();
		try {
			HttpPost post = new HttpPost(url);
			post.setEntity(createBodyWrapper(sender, message, channel));
			response = client.execute(post);
			slackMessengerLogger.info("Response from server: " + response.getStatusLine().getReasonPhrase() + ", code: "
					+ response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			slackMessengerLogger.error("IOException");
		}
		return createClientResponse(response);
	}

	private String createClientResponse(HttpResponse response) {
		int code = response.getStatusLine().getStatusCode();
		String responseToClient = null;
		try {
			if (code == 200) {
				responseToClient = "Message delivered";
			} else if (code == 404) {
				responseToClient = "Recipient not found";
				throw new Exception("Recipient not found");
			} else if (code == 500) {
				responseToClient = "Service Unavailable";
				throw new Exception("Service Unavailable");
			} else {
				responseToClient = "Something weird is happening";
				throw new Exception("Something weird is happening");
			}

		} catch (Exception e) {
			slackMessengerLogger.error(e.getMessage());
		}
		return responseToClient;
	}

	private StringEntity createBodyWrapper(String sender, String message, String channel) {
		StringEntity param = null;
		try {
			param = new StringEntity(createPostBody(sender, message, channel));
		} catch (UnsupportedEncodingException e) {
			slackMessengerLogger.error("Param has unsupported encoding");
		}
		param.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
		return param;
	}

	private String createPostBody(String sender, String message, String channel) {
		JSONObject json = new JSONObject();
		json.put("text", message);
		if (channel != null) {
			json.put("channel", channel);
		}
		if (sender != null) {
			json.put("username", sender);
		}
		return json.toString();
	}

	private void validateClientInput(String message, String channel) throws Exception {
		if (message == null || message.isEmpty()) {
			slackMessengerLogger.error("No message's found");
			throw new Exception("No message's found");
		}
		if (channel != null && channel.length() > 0) {
			Pattern p = Pattern.compile("^(#|@)[\\w_-]+$");
			Matcher m = p.matcher(channel);
			if (!m.matches()) {
				slackMessengerLogger.error("Wrong channel format");
				throw new Exception("Wrong channel format");
			}
		}
	}
}