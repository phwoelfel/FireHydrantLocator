package at.woelfel.philip.firehydrantlocator.osmapi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class Node {

	protected long id;
	protected double latitude;
	protected double longitude;
	protected int version;
	protected long changeset;
	protected User user;
	protected boolean visible;
	protected String timestamp;
	protected ArrayList<Tag> tags;

	public Node() {

	}

	public Node(Node n) {
		if (n != null) {
			id = n.getId();
			latitude = n.getLatitude();
			longitude = n.getLongitude();
			version = n.getVersion();
			changeset = n.getChangeset();
			if (n.getUser() != null) {
				user = new User(n.getUser().getUid(), n.getUser().getUsername());
			}
			visible = n.isVisible();
			timestamp = n.getTimestamp();
			tags = (ArrayList<Tag>) n.getTags().clone();
		}
	}

	@Override
	public String toString() {
		String str = "Node: #" + id + "\n";
		str += "\tLatitude: " + latitude + "\n";
		str += "\tLongitude: " + longitude + "\n";
		str += "\tVersion: " + version + "\n";
		str += "\tChangeset: " + changeset + "\n";
		str += "\tVisible: " + visible + "\n";
		str += "\tTimestamp: " + timestamp + "\n";
		str += "\tUser: " + user + "\n";

		str += "\tTags:\n";
		if (tags != null) {
			for (Tag tag : tags) {
				str += "\t\t" + tag + "\n";
			}
		}

		return str;
	}

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
		if (tags.size() > 0) {
			for (Tag tag : tags) {
				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", tag.getKey());
				serializer.attribute(null, "v", tag.getValue());
				serializer.endTag(null, "tag");
			}
		}

		serializer.endTag(null, "node");
		serializer.endDocument();

		serializer.flush();

		return writer.toString();
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the changeset
	 */
	public long getChangeset() {
		return changeset;
	}

	/**
	 * @param changeset the changeset to set
	 */
	public void setChangeset(long changeset) {
		this.changeset = changeset;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the tags
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
