package at.woelfel.philip.firehydrantlocator;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import at.woelfel.philip.firehydrantlocator.osmapi.Node;
import at.woelfel.philip.firehydrantlocator.osmapi.Tag;

public class FireHydrant extends Node implements Serializable {

	// notwendig:
	private FHType type; // Art des Hydranten: Unterflur, Ueberflur, Wand oder Teich (offenes Gewaesser)

	// optional
	private String name;
	private FHPosition position; // Standort des Hydranten: Strasse, Parkplatz, Wiese oder Gehsteig
	private int diameter; // Durchmesser in mm
	private int count; // Anzahl wenn mehrere an einer Stelle
	private int reservoir; // Fassungsvermoegen eines Behaelters im m^3
	private int pressure; // Druck in Bar

	public enum FHType {
		PILLAR("pillar"), UNDERGROUND("underground"), WALL("wall"), POND("pond");

		public final String name;

		FHType(String name) {
			this.name = name;
		}
	}

	public enum FHPosition {
		LANE("lane"), PARKINGLOT("parking_lot"), GREEN("green"), SIDEWALK("sidewalk");

		public final String name;

		FHPosition(String name) {
			this.name = name;
		}
	}

	public FireHydrant(double longitude, double latitude, FHType type) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.type = type;
	}

	public FireHydrant(Node n) {
		super(n);
		ArrayList<Tag> tags = n.getTags();
		if (tags != null) {
			for (Tag tag : tags) {
				if ("fire_hydrant:type".equals(tag.getKey())) {
					type = FireHydrant.getTypeFromName(tag.getValue());
				}
				else if ("fire_hydrant:diameter".equals(tag.getKey())) {
					diameter = Integer.parseInt(tag.getValue());
				}
				else if ("fire_hydrant:pressure".equals(tag.getKey())) {
					pressure = Integer.parseInt(tag.getValue());
				}
				else if ("fire_hydrant:position".equals(tag.getKey())) {
					position = FireHydrant.getPositionFromName(tag.getValue());
				}
				else if ("fire_hydrant:count".equals(tag.getKey())) {
					count = Integer.parseInt(tag.getValue());
				}
				else if ("fire_hydrant:reservoir".equals(tag.getKey())) {
					reservoir = Integer.parseInt(tag.getValue());
				}
				else if ("name".equals(tag.getKey())) {
					name = tag.getValue();
				}
			}
		}
	}

	/**
	 * @return the type of the hydrant (see {@link FHType})
	 */
	public FHType getType() {
		return type;
	}

	/**
	 * @param type the type to set (see {@link FHType})
	 */
	public void setType(FHType type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the relative position (see {@link FHPosition})
	 */
	public FHPosition getPosition() {
		return position;
	}

	/**
	 * @param pos the relative position to set (see {@link FHPosition})
	 */
	public void setPosition(FHPosition pos) {
		this.position = pos;
	}

	/**
	 * @return the diameter
	 */
	public int getDiameter() {
		return diameter;
	}

	/**
	 * @param diameter the diameter to set
	 */
	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the reservoir
	 */
	public int getReservoir() {
		return reservoir;
	}

	/**
	 * @param reservoir the reservoir to set
	 */
	public void setReservoir(int reservoir) {
		this.reservoir = reservoir;
	}

	/**
	 * @return the pressure
	 */
	public int getPressure() {
		return pressure;
	}

	/**
	 * @param pressure the pressure to set
	 */
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	@Override
	public String toXML() throws IllegalArgumentException, IllegalStateException, IOException {

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

		serializer.startDocument("UTF-8", Boolean.valueOf(true));
		serializer.startTag(null, "node");
		if (getId() != 0) {
			serializer.attribute(null, "id", "" + getId());
		}
		serializer.attribute(null, "visible", "" + isVisible());
		if (getLatitude() != 0) {
			serializer.attribute(null, "lat", "" + getLatitude());
		}
		if (getLongitude() != 0) {
			serializer.attribute(null, "lon", "" + getLongitude());
		}
		if (getTimestamp() != null && !getTimestamp().equals("")) {
			serializer.attribute(null, "timestamp", getTimestamp());
		}
		if (getVersion() != 0) {
			serializer.attribute(null, "version", "" + getVersion());
		}
		if (getChangeset() != 0) {
			serializer.attribute(null, "changeset", "" + getChangeset());
		}
		serializer.startTag(null, "tag");
		serializer.attribute(null, "k", "emergency");
		serializer.attribute(null, "v", "fire_hydrant");
		serializer.endTag(null, "tag");

		serializer.startTag(null, "tag");
		serializer.attribute(null, "k", "fire_hydrant:type");
		serializer.attribute(null, "v", getType().name);
		serializer.endTag(null, "tag");

		if (getPosition() != null) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "fire_hydrant:position");
			serializer.attribute(null, "v", getPosition().name);
			serializer.endTag(null, "tag");
		}

		if (getName() != null && !getName().equals("")) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "name");
			serializer.attribute(null, "v", getName());
			serializer.endTag(null, "tag");
		}

		if (getCount() != 0) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "fire_hydrant:count");
			serializer.attribute(null, "v", "" + getCount());
			serializer.endTag(null, "tag");
		}

		if (getDiameter() != 0) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "fire_hydrant:diameter");
			serializer.attribute(null, "v", "" + getDiameter());
			serializer.endTag(null, "tag");
		}

		if (getPressure() != 0) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "fire_hydrant:pressure");
			serializer.attribute(null, "v", "" + getPressure());
			serializer.endTag(null, "tag");
		}

		if (getReservoir() != 0) {
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "fire_hydrant:reservoir");
			serializer.attribute(null, "v", "" + getReservoir());
			serializer.endTag(null, "tag");
		}

		serializer.endTag(null, "node");
		serializer.endDocument();

		serializer.flush();

		return writer.toString();

	}
	
	public static FHType getTypeFromName(String name){
		if(name!=null){
			if(name.equals(FHType.PILLAR.name())){
				return FHType.PILLAR;
			}
			else if(name.equals(FHType.POND.name())){
				return FHType.POND;
			}
			else if(name.equals(FHType.WALL.name())){
				return FHType.WALL;
			}
			else if(name.equals(FHType.UNDERGROUND.name())){
				return FHType.UNDERGROUND;
			}
		}
		
			return null;
	}
	
	
	public static FHPosition getPositionFromName(String name){
		if(name!=null){
			if(name.equals(FHPosition.GREEN.name())){
				return FHPosition.GREEN;
			}
			else if(name.equals(FHPosition.LANE.name())){
				return FHPosition.LANE;
			}
			else if(name.equals(FHPosition.PARKINGLOT.name())){
				return FHPosition.PARKINGLOT;
			}
			else if(name.equals(FHPosition.SIDEWALK.name())){
				return FHPosition.SIDEWALK;
			}
		}
		
			return null;
	}

}
