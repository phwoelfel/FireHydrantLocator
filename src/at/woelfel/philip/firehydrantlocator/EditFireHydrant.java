package at.woelfel.philip.firehydrantlocator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHPosition;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHType;

public class EditFireHydrant extends Activity implements LocationListener {

	private FireHydrant fh;
	private int position;
	private FHApp app;

	private LocationManager lmgr;
	private Location cur_location;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hydrant_edit);

		app = (FHApp) getApplication();
		
		Intent in = getIntent();
		if (in.hasExtra(MainActivity.EXTRA_HYDRANT) && in.getExtras().get(MainActivity.EXTRA_HYDRANT) != null) {
			fh = (FireHydrant) in.getExtras().get(MainActivity.EXTRA_HYDRANT);

			((EditText)findViewById(R.id.edittext_lat)).setText(fh.getLatitude()+"");
			((EditText)findViewById(R.id.edittext_lon)).setText(fh.getLongitude()+"");
			
			Spinner sp_types = (Spinner) findViewById(R.id.spinner_hydrant_type);
			ArrayAdapter<CharSequence> ad_types = ArrayAdapter.createFromResource(this, R.array.hydrant_types, android.R.layout.simple_spinner_item);
			ad_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_types.setAdapter(ad_types);

			Spinner sp_positions = (Spinner) findViewById(R.id.spinner_hydrant_position);
			ArrayAdapter<CharSequence> ad_positions = ArrayAdapter.createFromResource(this, R.array.hydrant_positions, android.R.layout.simple_spinner_item);
			ad_positions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_positions.setAdapter(ad_positions);

			lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			Button but_updateloc = (Button)findViewById(R.id.button_updatelocation);
			but_updateloc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(cur_location!=null){
						fh.setLatitude(cur_location.getLatitude());
						fh.setLongitude(cur_location.getLongitude());
						((EditText)findViewById(R.id.edittext_lat)).setText(fh.getLatitude()+"");
						((EditText)findViewById(R.id.edittext_lon)).setText(fh.getLongitude()+"");
						Toast.makeText(EditFireHydrant.this, R.string.location_updated, Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(EditFireHydrant.this, R.string.error_nolocation, Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			Button but_showloc = (Button)findViewById(R.id.button_showlocation);
			but_showloc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String uri = "geo:"+ fh.getLatitude() + "," + fh.getLongitude() + "?q=" +fh.getLatitude() +"," +fh.getLongitude() +"(" +(fh.getName()!=null?fh.getName():app.getResources().getString(R.string.hydrant)) +")";
					//String uri = "http://openfiremap.org/?zoom=15&lat="+ fh.getLatitude() + "&lon=" + fh.getLongitude();
					startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
				}
			});
			
//			Log.d("Philip", "type pos: " +ad_types.getPosition(fh.getType().name) +", type: " +fh.getType().name);
			sp_types.setSelection(ad_types.getPosition(app.getStringFromType(fh.getType())));

			if (fh.getPosition() != null) {
				sp_positions.setSelection(ad_positions.getPosition(app.getStringFromPosition(fh.getPosition())));
//				Log.d("Philip", "pos pos: " +ad_types.getPosition(fh.getPosition().name) +", pos: " +fh.getPosition().name);
			}

			if (fh.getName() != null && !fh.getName().equals("")) {
				((EditText) findViewById(R.id.edittext_name)).setText(fh.getName());
			}

			if (fh.getDiameter() != 0) {
				((EditText) findViewById(R.id.edittext_diameter)).setText(fh.getDiameter()+"");
			}

			if (fh.getCount() != 0) {
				((EditText) findViewById(R.id.edittext_count)).setText(fh.getCount()+"");
			}

			if (fh.getPressure() != 0) {
				((EditText) findViewById(R.id.edittext_pressure)).setText(fh.getPressure()+"");
			}

			if (fh.getReservoir() != 0) {
				((EditText) findViewById(R.id.edittext_reservoir)).setText(fh.getReservoir()+"");
			}
		}
		if (in.hasExtra(MainActivity.EXTRA_POSITION)) {
			position = in.getExtras().getInt(MainActivity.EXTRA_POSITION);
		}
		Toast.makeText(this, R.string.savewithmenu, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		lmgr.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ArrayList<String> provs = new ArrayList<String>(lmgr.getAllProviders());
		if(provs.contains(LocationManager.GPS_PROVIDER)){
			lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MainActivity.GPS_UPDATETIME, MainActivity.GPS_UPDATEDIST, this);
		}
		if(provs.contains(LocationManager.NETWORK_PROVIDER)){
			lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MainActivity.GPS_UPDATETIME, MainActivity.GPS_UPDATEDIST, this);
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		cur_location = location;
		Toast.makeText(this, getString(R.string.location_found) +cur_location.getLatitude() +", " +cur_location.getLongitude(),Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hydrant_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_save) {
			String sel_type = (String) ((Spinner) findViewById(R.id.spinner_hydrant_type)).getSelectedItem();
			FHType type = app.getTypeFromString(sel_type);
			fh.setType(type);

			String sel_positions = (String) ((Spinner) findViewById(R.id.spinner_hydrant_position)).getSelectedItem();
			FHPosition pos = app.getPositionFromString(sel_positions);
			if (pos != null) {
				fh.setPosition(pos);
			}

			String name = ((EditText) findViewById(R.id.edittext_name)).getText().toString();
			if (!name.equals("")) {
				fh.setName(name);
			}
			else{
				fh.setName(null);
			}
			String diameter = ((EditText) findViewById(R.id.edittext_diameter)).getText().toString();
			if (!diameter.equals("")) {
				fh.setDiameter(Integer.parseInt(diameter));
			}
			else{
				fh.setDiameter(0);
			}
			String count = ((EditText) findViewById(R.id.edittext_count)).getText().toString();
			if (!count.equals("")) {
				fh.setCount(Integer.parseInt(count));
			}
			else{
				fh.setCount(0);
			}
			String pressure = ((EditText) findViewById(R.id.edittext_pressure)).getText().toString();
			if (!pressure.equals("")) {
				fh.setPressure(Integer.parseInt(pressure));
			}
			else{
				fh.setPressure(0);
			}
			String reservoir = ((EditText) findViewById(R.id.edittext_reservoir)).getText().toString();
			if (!reservoir.equals("")) {
				fh.setReservoir(Integer.parseInt(reservoir));
			}
			else{
				fh.setReservoir(0);
			}

			Intent data = new Intent();
			data.putExtra(MainActivity.EXTRA_HYDRANT, fh);
			data.putExtra(MainActivity.EXTRA_POSITION, position);
			setResult(RESULT_OK, data);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}


}
