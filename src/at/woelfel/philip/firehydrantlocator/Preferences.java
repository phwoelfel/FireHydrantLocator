package at.woelfel.philip.firehydrantlocator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference p = findPreference(getString(R.string.prefs_key_oauth_gettoken));
		p.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent in = new Intent(Preferences.this, OAuthGUI.class);
				startActivity(in);
				return true;
			}
		});
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Philip", getClass().getSimpleName() + ": resetting");
		onCreate(null);
	}
}
