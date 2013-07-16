package at.woelfel.philip.firehydrantlocator.osmapi;

import java.io.IOException;

import android.content.Context;
import android.util.Log;
import at.woelfel.philip.firehydrantlocator.R;
import at.woelfel.philip.firehydrantlocator.R.string;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class OAuthTokenmanager {

	private static final HttpTransport http_transport = new ApacheHttpTransport();

//	private static final String CONSUMER_KEY = "l9ZWR0RsZAIUlnJCYC3R0aOaz4CivYik7TXvAh1H";
//	private static final String CONSUMER_SECRET = "qA9BviYNmE5mqwCnRbE20ZUWPm2CMqfsSs2M1roi";

	// private static final String OAUTH_URL = "http://www.openstreetmap.org";
	// private static final String API_URL = "http://www.openstreetmap.org";
	
	private static final String CONSUMER_KEY = "XvTpxOIBvuT6a5HaqlI7rYL41vYNggdI9KdSGBm9";
	private static final String CONSUMER_SECRET = "3nEBG1s4gjkLxuuPR0yC5eX4ekXgHABS2krxGWcG";

	private static final String OAUTH_URL = "http://api06.dev.openstreetmap.org";
	private static final String API_URL = "http://api06.dev.openstreetmap.org";

	private static final String REQUEST_TOKEN_URL = OAUTH_URL + "/oauth/request_token";
	private static final String AUTHORIZE_URL = OAUTH_URL + "/oauth/authorize";
	private static final String ACCESS_TOKEN_URL = OAUTH_URL + "/oauth/access_token";

	private Context cont;

	private OAuthHmacSigner signer;
	private String token;
	private String tokenSecret;

	public OAuthTokenmanager(Context c){
		cont = c;

		// this signer will be used to sign all the requests in the "oauth dance"
		signer = new OAuthHmacSigner();
		signer.clientSharedSecret = CONSUMER_SECRET;

		//
		// OAuthParameters parameters = new OAuthParameters();
		// parameters.consumerKey = CONSUMER_KEY;
		// parameters.token = "xwE1arKNIsn3hycHmoej2yq9zfI6dUf9LnpRKMqO";
		// parameters.signer = signer;
		// xwE1arKNIsn3hycHmoej2yq9zfI6dUf9LnpRKMqO token
		// rjhTmVFcOU65nWRfQDLouW88xvapzKEa3WOoaHqv tokenSecret
		// utilize accessToken to access protected resources
		// HttpRequestFactory factory = http_transport.createRequestFactory(parameters);
		// GenericUrl url = new GenericUrl("http://api.openstreetmap.org/api/0.6/permissions");
		// HttpRequest req = factory.buildGetRequest(url);
		// req.setRequestMethod("GET");
		// HttpResponse resp = req.execute();
		// System.out.println("Response Status Code: " + resp.getStatusCode());
		// System.out.println("Response body:" + resp.parseAsString());
	}

	public void setTokenSharedSecret(String secret) {
		signer.tokenSharedSecret = secret;
		tokenSecret = secret;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public OAuthParameters getOAuthParameters() {
		if (token != null && tokenSecret != null) {
			OAuthParameters parameters = new OAuthParameters();
			parameters.consumerKey = CONSUMER_KEY;
			parameters.token = token;
			parameters.signer = signer;
			return parameters;
		}
		else {
			return null;
		}
	}

	public String getTempToken() throws IOException {

		// Step 1: Get a request token. This is a temporary token that is used for
		// having the user authorize an access token and to sign the request to obtain
		// said access token.
		OAuthGetTemporaryToken requestTempToken = new OAuthGetTemporaryToken(REQUEST_TOKEN_URL);
		requestTempToken.consumerKey = CONSUMER_KEY;
		requestTempToken.transport = http_transport;
		requestTempToken.signer = signer;

		OAuthCredentialsResponse requestTokenResponse = requestTempToken.execute();
		Log.d("Philip", "Request Temp Token:");
		Log.d("Philip", "    - oauth_token        = " + requestTokenResponse.token);
		Log.d("Philip", "    - oauth_token_secret = " + requestTokenResponse.tokenSecret);

		// updates signer's token shared secret
		signer.tokenSharedSecret = requestTokenResponse.tokenSecret;

		return requestTokenResponse.token;
	}

	public String getAuthURL(String tempToken) throws Exception {
		if (tempToken != null) {

			OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(AUTHORIZE_URL);
			authorizeUrl.temporaryToken = tempToken;

			// After the user has granted access to you, the consumer, the provider will
			// redirect you to whatever URL you have told them to redirect to. You can
			// usually define this in the oauth_callback argument as well.
			//
			// String currentLine = "n";
			// System.out.println("Go to the following link in your browser:\n" + authorizeUrl.build());
			// InputStreamReader converter = new InputStreamReader(System.in);
			// BufferedReader in = new BufferedReader(converter);
			// while (currentLine.equalsIgnoreCase("n")) {
			// System.out.println("Have you authorized me? (y/n)");
			// currentLine = in.readLine();
			// }

			return authorizeUrl.build();
		}
		else {
			throw new Exception(cont.getString(R.string.error_no_temp_token));
		}
	}

	public String[] getOAuthToken(String tempToken) throws Exception {
		if (tempToken != null) {
			// Step 3: Once the consumer has redirected the user back to the oauth_callback
			// URL you can request the access token the user has approved. You use the
			// request token to sign this request. After this is done you throw away the
			// request token and use the access token returned. You should store this
			// access token somewhere safe, like a database, for future use.
			OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
			accessToken.consumerKey = CONSUMER_KEY;
			accessToken.signer = signer;
			accessToken.transport = http_transport;
			accessToken.temporaryToken = tempToken;

			OAuthCredentialsResponse accessTokenResponse = accessToken.execute();
			Log.d("Philip", "Access Token:");
			Log.d("Philip", "    - oauth_token        = " + accessTokenResponse.token);
			Log.d("Philip", "    - oauth_token_secret = " + accessTokenResponse.tokenSecret);

			// updates signer's token shared secret
			signer.tokenSharedSecret = accessTokenResponse.tokenSecret;
			String[] ar = { accessTokenResponse.token, accessTokenResponse.tokenSecret };

			return ar;
		}
		else {
			throw new Exception(cont.getString(R.string.error_no_temp_token));
		}
	}
}
