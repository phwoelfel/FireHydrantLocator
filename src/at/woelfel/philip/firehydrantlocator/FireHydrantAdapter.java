package at.woelfel.philip.firehydrantlocator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class FireHydrantAdapter extends BaseAdapter {

	private ArrayList<FireHydrant> hydrants;
	private Context cont;

	public FireHydrantAdapter(Context context) {
		cont = context;
		hydrants = new ArrayList<FireHydrant>();
	}

	@Override
	public int getCount() {
		return hydrants != null ? hydrants.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return hydrants != null ? hydrants.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.firehydrant_entry, null);
		}
		final FireHydrant fh = hydrants.get(position);
		TextView label = (TextView) v.findViewById(R.id.label_hydrant);
		if (fh.getName() != null && !fh.getName().equals("")) {
			label.setText(fh.getName());
		}
		else {
			label.setText(cont.getString(R.string.firehydrant_listitem) + " " + position + " (" + fh.getLongitude() + " " + fh.getLatitude() + ")");
		}
		ImageButton button_delete = (ImageButton) v.findViewById(R.id.button_delete);
		button_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) cont).setCurrentDeleteID(position);
				((Activity) cont).showDialog(MainActivity.DIALOG_DELETE_HYDRANT);
			}
		});

		ImageButton button_edit = (ImageButton) v.findViewById(R.id.button_edit);
		button_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(cont, EditFireHydrant.class);
				in.putExtra(MainActivity.EXTRA_HYDRANT, fh);
				in.putExtra(MainActivity.EXTRA_POSITION, position);
				((Activity) cont).startActivityForResult(in, MainActivity.REQ_EDIT_HYDRANT);
			}
		});
		
		ImageButton button_show = (ImageButton) v.findViewById(R.id.button_show);
		button_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = "geo:"+ fh.getLatitude() + "," + fh.getLongitude() + "?q=" +fh.getLatitude() +"," +fh.getLongitude() +"(" +(fh.getName()!=null?fh.getName():cont.getResources().getString(R.string.hydrant)) +")";
				((Activity) cont).startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		});

		return v;
	}

	public void addFirehydrant(FireHydrant fh) {
		hydrants.add(fh);
		notifyDataSetChanged();
	}

	public ArrayList<FireHydrant> getFireHydrants() {
		return hydrants;
	}

	public void setFireHydrants(ArrayList<FireHydrant> fh) {
		this.hydrants = fh;
		notifyDataSetChanged();
	}

	public void deleteHydrant(int position) {
		hydrants.remove(position);
		notifyDataSetChanged();
	}

	public void setFireHydrant(int position, FireHydrant fh) {
		hydrants.set(position, fh);
		notifyDataSetChanged();
	}
	

	public boolean toXML(File xmlfile) {

		try {
			if (!xmlfile.exists()) {
				xmlfile.createNewFile();
			}
			else if(!xmlfile.canRead()){
				return false;
			}
			FileOutputStream fileos = new FileOutputStream(xmlfile);
			XmlSerializer serializer = Xml.newSerializer();
			// we set the FileOutputStream as output for the serializer, using UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			// Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			// set indentation option
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			// start a tag called "root"
			serializer.startTag(null, "osmChange");
			serializer.attribute(null, "version", "0.6");
			serializer.attribute(null, "generator", "hydrantlocator");

			serializer.startTag(null, "create");
			for (int i = 0; i < hydrants.size(); i++) {
				FireHydrant fh = hydrants.get(i);

				serializer.startTag(null, "node");
				serializer.attribute(null, "id", "-" + (i + 1));
				serializer.attribute(null, "lat", "" + fh.getLatitude());
				serializer.attribute(null, "lon", "" + fh.getLongitude());
				// timestamp
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM'T'hh:mm:ss.SZ");
				serializer.attribute(null, "timestamp", sdf.format(date));
				serializer.attribute(null, "version", "1");
				// TODO
				serializer.attribute(null, "changeset", "");

				// serializer.startTag(null, "tag");
				// serializer.attribute(null, "k", "");
				// serializer.attribute(null, "v", "");
				// serializer.endTag(null, "tag");

				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", "emergency");
				serializer.attribute(null, "v", "fire_hydrant");
				serializer.endTag(null, "tag");

				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", "fire_hydrant:type");
				serializer.attribute(null, "v", fh.getType().name);
				serializer.endTag(null, "tag");

				if (fh.getPosition() != null) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:position");
					serializer.attribute(null, "v", fh.getPosition().name);
					serializer.endTag(null, "tag");
				}
				
				if (fh.getName() != null && !fh.getName().equals("")) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:name");
					serializer.attribute(null, "v", fh.getName());
					serializer.endTag(null, "tag");
				}

				if (fh.getCount() != 0) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:count");
					serializer.attribute(null, "v", ""+fh.getCount());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getDiameter()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:diameter");
					serializer.attribute(null, "v", ""+fh.getDiameter());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getPressure()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:pressure");
					serializer.attribute(null, "v", ""+fh.getPressure());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getReservoir()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:reservoir");
					serializer.attribute(null, "v", ""+fh.getReservoir());
					serializer.endTag(null, "tag");
				}
				
				serializer.endTag(null, "node");
			}

			serializer.endTag(null, "create");

			serializer.endTag(null, "osmChange");
			serializer.endDocument();
			// write xml data into the FileOutputStream
			serializer.flush();
			// finally we close the file stream
			fileos.close();
			return true;
		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
			e.printStackTrace();
			return false;
		}
		
	}

	
	/*
	 * <?xml version='1.0' encoding='UTF-8'?>
<osm version='0.6' upload='true' generator='JOSM'>
  <node id='-15467' action='modify' visible='true' lat='48.21436546276882' lon='15.636203102921538'>
    <tag k='emergency' v='fire_hydrant' />
    <tag k='fire_hydrant:diameter' v='80' />
    <tag k='fire_hydrant:position' v='sidewalk' />
    <tag k='fire_hydrant:type' v='pillar' />
  </node>
  </osm>
	 */
	
	public boolean toOSM(File xmlfile){
		try {
			if (!xmlfile.exists()) {
				xmlfile.createNewFile();
			}
			else if(!xmlfile.canRead()){
				return false;
			}
			FileOutputStream fileos = new FileOutputStream(xmlfile);
			XmlSerializer serializer = Xml.newSerializer();
			// we set the FileOutputStream as output for the serializer, using UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			// Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			// set indentation option
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			// start a tag called "root"
			serializer.startTag(null, "osm");
			serializer.attribute(null, "version", "0.6");
			serializer.attribute(null, "generator", "hydrantlocator");

			for (int i = 0; i < hydrants.size(); i++) {
				FireHydrant fh = hydrants.get(i);

				serializer.startTag(null, "node");
				serializer.attribute(null, "id", "-" + (i + 1));
				serializer.attribute(null, "action", "create");
				serializer.attribute(null, "visible", "true");
				serializer.attribute(null, "lat", "" + fh.getLatitude());
				serializer.attribute(null, "lon", "" + fh.getLongitude());
				// timestamp
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM'T'hh:mm:ss.SZ");
				serializer.attribute(null, "timestamp", sdf.format(date));
				serializer.attribute(null, "version", "1");
				// TODO
				serializer.attribute(null, "changeset", "");

				// serializer.startTag(null, "tag");
				// serializer.attribute(null, "k", "");
				// serializer.attribute(null, "v", "");
				// serializer.endTag(null, "tag");

				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", "emergency");
				serializer.attribute(null, "v", "fire_hydrant");
				serializer.endTag(null, "tag");

				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", "fire_hydrant:type");
				serializer.attribute(null, "v", fh.getType().name);
				serializer.endTag(null, "tag");

				if (fh.getPosition() != null) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:position");
					serializer.attribute(null, "v", fh.getPosition().name);
					serializer.endTag(null, "tag");
				}
				
				if (fh.getName() != null && !fh.getName().equals("")) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:name");
					serializer.attribute(null, "v", fh.getName());
					serializer.endTag(null, "tag");
				}

				if (fh.getCount() != 0) {
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:count");
					serializer.attribute(null, "v", ""+fh.getCount());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getDiameter()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:diameter");
					serializer.attribute(null, "v", ""+fh.getDiameter());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getPressure()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:pressure");
					serializer.attribute(null, "v", ""+fh.getPressure());
					serializer.endTag(null, "tag");
				}
				
				if(fh.getReservoir()!=0){
					serializer.startTag(null, "tag");
					serializer.attribute(null, "k", "fire_hydrant:reservoir");
					serializer.attribute(null, "v", ""+fh.getReservoir());
					serializer.endTag(null, "tag");
				}
				
				serializer.endTag(null, "node");
			}


			serializer.endTag(null, "osm");
			serializer.endDocument();
			// write xml data into the FileOutputStream
			serializer.flush();
			// finally we close the file stream
			fileos.close();
			return true;
		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
			e.printStackTrace();
			return false;
		}
	}
	
	public String toXMLString(){
		try {
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			serializer.setOutput(writer);
			
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
			serializer.startTag(null, "osm");
			serializer.attribute(null, "version", "0.6");
			serializer.attribute(null, "generator", "hydrantlocator");
			serializer.flush();
			for (int i = 0; i < hydrants.size(); i++) {
				FireHydrant fh = hydrants.get(i);
				String st = fh.toXML();
				writer.append(st);
			}


			serializer.endTag(null, "osm");
			serializer.endDocument();

			serializer.flush();
			
			return writer.toString();
		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
			e.printStackTrace();
			return null;
		}
	}
}
