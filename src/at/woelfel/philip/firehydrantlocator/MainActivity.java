package at.woelfel.philip.firehydrantlocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import at.woelfel.philip.filebrowser.FileBrowser;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHPosition;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHType;

public class MainActivity extends Activity implements OnClickListener, LocationListener {

	private Button button_addhydrant;
	private FireHydrantAdapter fh_adap;
	private LocationManager lmgr;

	private static final int DIALOG_ADD_HYDRANT = 1;
	public static final int DIALOG_DELETE_HYDRANT = 2;

	public static final int GPS_UPDATETIME = 1000; // wie oft GPS updaten (in ms)
	public static final int GPS_UPDATEDIST = 1; // ab welcher entfernungsaenderung
	private static final String SAVE_DIR = Environment.getExternalStorageDirectory() + "/fhlocator/";

	public static final String EXTRA_HYDRANT = "extra_hydrant";
	public static final String EXTRA_POSITION = "extra_position";
	public static final String EXTRA_ALL_HYDRANTS = "extra_all_hydrants";
	

	public static final int REQ_EDIT_HYDRANT = 1;
	public static final int REQ_SAVE_FILE = 2;
	public static final int REQ_LOAD_FILE = 3;
	public static final int REQ_SAVE_XML_FILE = 4;
	private static final String LOG_TAG = "Philip";

	private FHApp app;
	private Location cur_location;
	
	private int cur_delid=-1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (FHApp) getApplication();

		fh_adap = new FireHydrantAdapter(this);
		ListView lv = (ListView) findViewById(R.id.list_hydrants);
		lv.setAdapter(fh_adap);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ALL_HYDRANTS)){
			fh_adap.setFireHydrants((ArrayList<FireHydrant>) savedInstanceState.get(EXTRA_ALL_HYDRANTS));
			Log.d(LOG_TAG, "loading state");
		}

		button_addhydrant = (Button) findViewById(R.id.button_add_hydrant);
		button_addhydrant.setOnClickListener(this);

		lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		File f = new File(SAVE_DIR);
		if (!f.exists()) {
			f.mkdir();
		}

		// if (!lmgr.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
		// Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		// startActivity(myIntent);
		// }

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putSerializable(EXTRA_ALL_HYDRANTS, fh_adap.getFireHydrants());
		Log.d(LOG_TAG, "saveing state");
		
		super.onSaveInstanceState(outState);
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
			lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATETIME, GPS_UPDATEDIST, this);
		}
		if(provs.contains(LocationManager.NETWORK_PROVIDER)){
			lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATETIME, GPS_UPDATEDIST, this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
				case REQ_EDIT_HYDRANT:
					fh_adap.setFireHydrant(data.getExtras().getInt(MainActivity.EXTRA_POSITION), (FireHydrant) data.getExtras().get(MainActivity.EXTRA_HYDRANT));
					plsToast(R.string.hydrant_updated);
					break;

				case REQ_LOAD_FILE:
					if (data.hasExtra(FileBrowser.EXTRA_FILE)) {
						File f = (File) data.getExtras().get(FileBrowser.EXTRA_FILE);
						try {
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
							fh_adap.setFireHydrants((ArrayList<FireHydrant>) ois.readObject());
							ois.close();
							plsToast(getString(R.string.imported_from) + f.getAbsolutePath());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							plsToast(R.string.error_filenotfound);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					else {
						plsToast(R.string.error_nofile);
					}
					break;

				case REQ_SAVE_FILE:
					if (data.hasExtra(FileBrowser.EXTRA_FILE)) {
						File f = (File) data.getExtras().get(FileBrowser.EXTRA_FILE);
						try {
							ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
							oos.writeObject(fh_adap.getFireHydrants());
							oos.flush();
							oos.close();
							plsToast(getString(R.string.exported_to) + f.getAbsolutePath());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							plsToast(R.string.error_filenotfound);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						plsToast(R.string.error_nofile);
					}
					break;

				case REQ_SAVE_XML_FILE:
					if (data.hasExtra(FileBrowser.EXTRA_FILE)) {
						File f = (File) data.getExtras().get(FileBrowser.EXTRA_FILE);
						if (fh_adap.toOSM(f)) {
							plsToast(getString(R.string.exported_to) + f.getAbsolutePath());
						}
					}
					break;
				default:
					break;
			}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_export_xml:
				Intent iexpx = new Intent(this, FileBrowser.class);
				iexpx.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
				iexpx.putExtra(FileBrowser.EXTRA_STARTPATH, SAVE_DIR);
				startActivityForResult(iexpx, REQ_SAVE_XML_FILE);
				break;

			case R.id.menu_export_file:
				Intent iexpf = new Intent(this, FileBrowser.class);
				iexpf.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
				iexpf.putExtra(FileBrowser.EXTRA_STARTPATH, SAVE_DIR);
				startActivityForResult(iexpf, REQ_SAVE_FILE);

				break;

			case R.id.menu_import_file:
				Intent iimpf = new Intent(this, FileBrowser.class);
				iimpf.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
				iimpf.putExtra(FileBrowser.EXTRA_STARTPATH, SAVE_DIR);
				startActivityForResult(iimpf, REQ_LOAD_FILE);
				break;
				
			case R.id.menu_about:
				Intent iabout = new Intent(this, About.class);
				startActivity(iabout);
				break;
				
			case R.id.menu_settings:
				Intent iset = new Intent(this, Preferences.class);
				startActivity(iset);
				break;
				
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void plsToast(String txt) {
		Toast t = Toast.makeText(this, txt, Toast.LENGTH_LONG);
		t.show();

	}

	private void plsToast(int str_res) {
		Toast t = Toast.makeText(this, str_res, Toast.LENGTH_LONG);
		t.show();

	}

	@Override
	public void onClick(View v) {
		if (cur_location != null) {
			showDialog(DIALOG_ADD_HYDRANT);
		}
		else {
			plsToast(R.string.error_nolocation);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if(cur_location==null){
			plsToast(getString(R.string.location_found) + location.getLongitude() + " " + location.getLatitude());
		}
		cur_location = location;
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
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id==DIALOG_DELETE_HYDRANT && getCurrentDeleteID()>-1){
			Log.d("Philip", getClass().getSimpleName() + ": positive button");
			((AlertDialog)dialog).setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					fh_adap.deleteHydrant(getCurrentDeleteID());
					setCurrentDeleteID(-1);
				}
			});
		}
		else if(id==DIALOG_ADD_HYDRANT){
			if(cur_location!=null){
				AlertDialog dia = (AlertDialog)dialog;
				EditText ed_lat = (EditText)dia.findViewById(R.id.edittext_lat);
				ed_lat.setText(cur_location.getLatitude()+"");
				EditText ed_lon = (EditText)dia.findViewById(R.id.edittext_lon);
				ed_lon.setText(cur_location.getLongitude()+"");
			}
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ADD_HYDRANT) {
			if(cur_location!=null){
				LayoutInflater factory = LayoutInflater.from(this);
				final View view_hydrantdata = factory.inflate(R.layout.hydrant_edit, null);
	
				((EditText) view_hydrantdata.findViewById(R.id.edittext_lat)).setText(cur_location.getLatitude() + "");
				((EditText) view_hydrantdata.findViewById(R.id.edittext_lon)).setText(cur_location.getLongitude() + "");
				view_hydrantdata.findViewById(R.id.button_showlocation).setVisibility(View.GONE);
				view_hydrantdata.findViewById(R.id.button_updatelocation).setVisibility(View.GONE);
	
				final Spinner sp_types = (Spinner) view_hydrantdata.findViewById(R.id.spinner_hydrant_type);
				ArrayAdapter<CharSequence> ad_types = ArrayAdapter.createFromResource(this, R.array.hydrant_types, android.R.layout.simple_spinner_item);
				ad_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_types.setAdapter(ad_types);
	
				final Spinner sp_positions = (Spinner) view_hydrantdata.findViewById(R.id.spinner_hydrant_position);
				ArrayAdapter<CharSequence> ad_positions = ArrayAdapter.createFromResource(this, R.array.hydrant_positions, android.R.layout.simple_spinner_item);
				ad_positions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_positions.setAdapter(ad_positions);
	
				final AlertDialog.Builder dia = new AlertDialog.Builder(this);
				dia.setTitle(R.string.add_hydrant_title);
				dia.setView(view_hydrantdata);
				dia.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (cur_location != null) {
							String sel_type = (String) sp_types.getSelectedItem();
							FHType type = app.getTypeFromString(sel_type);
	
							FireHydrant fh = new FireHydrant(cur_location.getLongitude(), cur_location.getLatitude(), type);
	
							String sel_positions = (String) sp_positions.getSelectedItem();
							FHPosition pos = app.getPositionFromString(sel_positions);
							Log.d("Philip", getClass().getSimpleName() + ": " +pos);
							if (pos != null) {
								fh.setPosition(pos);
							}
	
							String name = ((EditText) view_hydrantdata.findViewById(R.id.edittext_name)).getText().toString();
							if (!name.equals("")) {
								fh.setName(name);
							}
							String diameter = ((EditText) view_hydrantdata.findViewById(R.id.edittext_diameter)).getText().toString();
							if (!diameter.equals("")) {
								fh.setDiameter(Integer.parseInt(diameter));
							}
							String count = ((EditText) view_hydrantdata.findViewById(R.id.edittext_count)).getText().toString();
							if (!count.equals("")) {
								fh.setCount(Integer.parseInt(count));
							}
							String pressure = ((EditText) view_hydrantdata.findViewById(R.id.edittext_pressure)).getText().toString();
							if (!pressure.equals("")) {
								fh.setPressure(Integer.parseInt(pressure));
							}
							String reservoir = ((EditText) view_hydrantdata.findViewById(R.id.edittext_reservoir)).getText().toString();
							if (!reservoir.equals("")) {
								fh.setReservoir(Integer.parseInt(reservoir));
							}
	
							fh_adap.addFirehydrant(fh);
						}
						else {
							plsToast("No GPS Location!");
						}
	
					}
				});
				dia.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
	
					}
				});
				return dia.create();
			}
		}
		else if (id == DIALOG_DELETE_HYDRANT) {
			AlertDialog.Builder dia = new AlertDialog.Builder(this);
			dia.setTitle(R.string.delete_hydrant_title);
			dia.setMessage(R.string.delete_hydrant_msg);
			dia.setNegativeButton(android.R.string.cancel, null);
			dia.setPositiveButton(android.R.string.ok, null);
			return dia.create();

		}
		return super.onCreateDialog(id);
	}

	public int getCurrentDeleteID() {
		return cur_delid;
	}

	public void setCurrentDeleteID(int cur_delid) {
		this.cur_delid = cur_delid;
	}
	

}