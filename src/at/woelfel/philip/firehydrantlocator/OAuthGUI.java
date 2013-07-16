package at.woelfel.philip.firehydrantlocator;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.woelfel.philip.firehydrantlocator.osmapi.Changeset;
import at.woelfel.philip.firehydrantlocator.osmapi.Node;
import at.woelfel.philip.firehydrantlocator.osmapi.OAuthTokenmanager;
import at.woelfel.philip.firehydrantlocator.osmapi.OSMApi;
import at.woelfel.philip.firehydrantlocator.osmapi.User;
import at.woelfel.philip.settings.Settings;

import com.google.api.client.http.HttpResponse;

public class OAuthGUI extends Activity {

	private OAuthTokenmanager tokmgr;
	private Settings set;

	private String tempToken;
	private TextView text;

	private OSMApi api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauthgui);

		tokmgr = new OAuthTokenmanager(this);
		set = new Settings(this);
		text = ((TextView) findViewById(R.id.text_oauthgui));
		try {
			api = new OSMApi(this);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Button but = (Button) findViewById(R.id.button_authorize_app);
		but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					tempToken = tokmgr.getTempToken();
					String uri = tokmgr.getAuthURL(tempToken);

					Uri ur = Uri.parse(uri);
					Log.d("Philip", "auth uri " + ur);
					Intent in = new Intent(android.content.Intent.ACTION_VIEW, ur);
					Log.d("Philip", "intent: " + in);
					startActivity(in);
				} catch (IOException e) {
					Toast.makeText(OAuthGUI.this, "io error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					text.setText("io error:" + e.getLocalizedMessage());
					e.printStackTrace();
				} catch (Exception e) {
					Toast.makeText(OAuthGUI.this, "error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					text.setText("error:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		});

		Button but2 = (Button) findViewById(R.id.button_get_oauth_token);
		but2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String[] toks = tokmgr.getOAuthToken(tempToken);
					set.setSetting(getString(R.string.prefs_key_oauth_token), toks[0]);
					set.setSetting(getString(R.string.prefs_key_oauth_token_secret), toks[1]);
					text.setText(getString(R.string.oauth_token_saved) + "\nToken: " + toks[0] + "\nToken Secret: " + toks[1]);
				} catch (Exception e) {
					Toast.makeText(OAuthGUI.this, "error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					text.setText("error:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		});

		Button but3 = (Button) findViewById(R.id.button_test_oauth);
		but3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String t = ((EditText) findViewById(R.id.edittext_oauth_method)).getText() + "";
					HttpResponse resp = api.callApi(t, "GET");
					String rstr = resp.parseAsString();
					text.setText(getString(R.string.oauth_test_ok) + resp.getStatusCode() + "\n" + rstr);
					System.out.println("Response Status Code: " + resp.getStatusCode());
					System.out.println("Response body:" + rstr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Button but4 = (Button) findViewById(R.id.button_get_user);
		but4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				User u = api.getUser();
				if (u != null) {
					text.setText("got user: " + u.getUsername() + " (" + u.getUid() + ")");
				}
				else {
					text.setText(R.string.error_api);
				}
			}
		});

		Button but5 = (Button) findViewById(R.id.button_create_changeset);
		but5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Changeset cs = api.createChangeset("test cs");
				if (cs != null) {
					text.setText("created changeset: " + cs.getId());
				}
				else {
					text.setText(R.string.error_api);
				}
			}
		});

		Button but6 = (Button) findViewById(R.id.button_get_changeset);
		but6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String t = ((EditText) findViewById(R.id.edittext_oauth_method)).getText() + "";
				Changeset cs = api.getChangeset(Long.parseLong(t));
				text.setText("got changeset:\n" + cs);
			}
		});

		Button but7 = (Button) findViewById(R.id.button_close_changeset);
		but7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String t = ((EditText) findViewById(R.id.edittext_oauth_method)).getText() + "";
				Changeset cs = new Changeset(Long.parseLong(t));
				if (api.closeChangeset(cs)) {
					text.setText("closed changeset #" +cs.getId() +"\n");
				}
				else {
					text.setText(R.string.error_api);
				}
			}
		});

		
		Button but8 = (Button) findViewById(R.id.button_get_node);
		but8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String t = ((EditText) findViewById(R.id.edittext_oauth_method)).getText() + "";
				Node n = api.getNode(Long.parseLong(t));
				text.setText("got node:\n" + n);
			}
		});
	}

}
