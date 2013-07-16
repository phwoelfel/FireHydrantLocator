package at.woelfel.philip.firehydrantlocator.osmapi;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import at.woelfel.philip.firehydrantlocator.R;
import at.woelfel.philip.settings.Settings;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class OSMApi {

	private OAuthTokenmanager tokmgr;
	private Context cont;
	private Settings set;

	public String API_URL = "http://api06.dev.openstreetmap.org";

	public OSMApi(Context c) throws Exception {
		cont = c;
		set = new Settings(cont);
		tokmgr = new OAuthTokenmanager(cont);
		if (set.hasSetting(R.string.prefs_key_oauth_token) && set.hasSetting(R.string.prefs_key_oauth_token_secret)) {
			tokmgr.setToken((String) set.getSetting(R.string.prefs_key_oauth_token));
			tokmgr.setTokenSharedSecret((String) set.getSetting(R.string.prefs_key_oauth_token_secret));
		}
		else {
			throw new Exception(cont.getString(R.string.error_no_token));
		}
	}

	public HttpResponse callApi(String apimethod, String httpmethod) throws IOException {
		HttpTransport http_transport = new ApacheHttpTransport();
		OAuthParameters parameters = tokmgr.getOAuthParameters();
		HttpRequestFactory factory = http_transport.createRequestFactory(parameters);
		GenericUrl url = new GenericUrl(API_URL + apimethod);
		HttpRequest req = factory.buildGetRequest(url);
		req.setRequestMethod(httpmethod);
		HttpResponse resp = req.execute();
		return resp;
	}

	public HttpResponse callApi(String apimethod, String httpmethod, String content) throws IOException {
		HttpTransport http_transport = new ApacheHttpTransport();
		OAuthParameters parameters = tokmgr.getOAuthParameters();
		HttpRequestFactory factory = http_transport.createRequestFactory(parameters);
		GenericUrl url = new GenericUrl(API_URL + apimethod);

		HttpRequest req = factory.buildGetRequest(url);
		req.setRequestMethod(httpmethod);
		// UrlEncodedContent cont = new UrlEncodedContent(content);
		PlainHttpContent conte = new PlainHttpContent(content, "text/xml");
		req.setContent(conte);

		HttpResponse resp = req.execute();
		return resp;
	}

	public User getUser() {
		try {
			HttpResponse resp = callApi("/api/0.6/user/details", "GET");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(resp.getContent());
			NodeList nl = d.getElementsByTagName("user");
			if (nl.getLength() > 0) {
				org.w3c.dom.Node cur = nl.item(0);
				String id = cur.getAttributes().getNamedItem("id").getNodeValue();
				String uname = cur.getAttributes().getNamedItem("display_name").getNodeValue();
				User user = new User(Integer.parseInt(id), uname);
				Log.d("Philip", getClass().getSimpleName() + ": user: " + user);
				return user;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Changeset createChangeset(String comment) {
		try {

			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			serializer.setOutput(writer);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "osm");
			serializer.startTag(null, "changeset");

			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "created_by");
			serializer.attribute(null, "v", cont.getString(R.string.app_name));
			serializer.endTag(null, "tag");
			serializer.startTag(null, "tag");
			serializer.attribute(null, "k", "comment");
			serializer.attribute(null, "v", comment);
			serializer.endTag(null, "tag");

			serializer.endTag(null, "changeset");
			serializer.endTag(null, "osm");
			serializer.endDocument();

			serializer.flush();

			String body = writer.toString();
			Log.d("Philip", getClass().getSimpleName() + ": creating changeset: " + body);

			HttpResponse resp = callApi("/api/0.6/changeset/create", "PUT", body);
			String r = resp.parseAsString();
			Log.d("Philip", getClass().getSimpleName() + ": got response: " + r);
			return new Changeset(Long.parseLong(r));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Changeset getChangeset(long csid) {

		try {
			HttpResponse resp = callApi("/api/0.6/changeset/" + csid, "GET");
			String xml = resp.parseAsString();
			Log.d("Philip", getClass().getSimpleName() + ": getChangeset xml: " + xml);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document d = db.parse(is);
			d.normalize();
			Log.d("Philip", getClass().getSimpleName() + ": got changeset document: " + d);
			
			Element osmel = d.getDocumentElement();
			NodeList nl = osmel.getChildNodes();
			for (int i1 = 0; i1 < nl.getLength(); i1++) {
				if (nl.item(i1) instanceof Element) {
					Element cur = (Element) nl.item(i1);

					Log.d("Philip", getClass().getSimpleName() + ": cur: " + cur.getNodeName() + " " + cur.getNodeValue() + " " + cur.getNodeType());
					Changeset cs = new Changeset();
					long id = Long.parseLong(cur.getAttribute("id"));
					cs.setId(id);

					String uname = cur.getAttribute("user");
					long uid = Long.parseLong(cur.getAttribute("uid"));
					User u = new User(uid, uname);
					cs.setUser(u);

					String createdat = cur.getAttribute("created_at");
					cs.setCreatedat(createdat);

					boolean open = Boolean.parseBoolean(cur.getAttribute("open"));
					cs.setOpen(open);

					if (!cur.getAttribute("min_lon").equals("")) {
						Log.d("Philip", getClass().getSimpleName() + ": cur.getAttribute(\"min_lon\"): " + cur.getAttribute("min_lon"));
						double minLon = Double.parseDouble(cur.getAttribute("min_lon"));
						double minLat = Double.parseDouble(cur.getAttribute("min_lat"));
						double maxLon = Double.parseDouble(cur.getAttribute("max_lon"));
						double maxLat = Double.parseDouble(cur.getAttribute("max_lat"));
						Boundingbox bb = new Boundingbox(minLon, minLat, maxLon, maxLat);
						cs.setBoundingbox(bb);
					}

					NodeList tags = cur.getChildNodes();
					for (int i = 0; i < tags.getLength(); i++) {
						if (tags.item(i) instanceof Element) {
							Element nod = (Element) tags.item(i);
							Log.d("Philip", getClass().getSimpleName() + ": nod: " + nod.getNodeName() + " " + nod + " " + nod.getNodeType());
							String key = (nod.getAttribute("k") != null ? nod.getAttribute("k") : "");
							String value = (nod.getAttribute("v") != null ? nod.getAttribute("v") : "");
							if (key.equals("created_by")) {
								cs.setCreatedby(value);
							}
							else if (key.equals("comment")) {
								cs.setComment(value);
							}
						}
					}
					Log.d("Philip", getClass().getSimpleName() + ": got changeset: " + cs);
					return cs;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean closeChangeset(Changeset cs) {

		try {
			HttpResponse resp = callApi("/api/0.6/changeset/" + cs.getId() + "/close", "PUT");
			String r = resp.parseAsString();
			Log.d("Philip", getClass().getSimpleName() + ": got response: " + r +" status: " +resp.getStatusCode() );
			if (r.equals("") && resp.getStatusCode() == 200) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public long createNode(Node n, Changeset cs) {

		try {
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			serializer.setOutput(writer);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "osm");

			serializer.flush();
			writer.append(n.toXML());

			serializer.endTag(null, "osm");
			serializer.endDocument();

			serializer.flush();

			String body = writer.toString();
			Log.d("Philip", getClass().getSimpleName() + ": creating node: " + body);

			HttpResponse resp = callApi("/api/0.6/node/create", "PUT", body);

			String r = resp.parseAsString();
			Log.d("Philip", getClass().getSimpleName() + ": got response: " + r);

			return Long.parseLong(r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public Node getNode(long nodeid) {

		try {
			HttpResponse resp = callApi("/api/0.6/node/" + nodeid, "GET");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(resp.getContent());
			NodeList nl = d.getElementsByTagName("node");
			if (nl.getLength() > 0) {
				org.w3c.dom.Node cur = nl.item(0);
				Node n = new Node();

				long id = Long.parseLong(cur.getAttributes().getNamedItem("id").getNodeValue());
				n.setId(id);

				String uname = cur.getAttributes().getNamedItem("user").getNodeValue();
				int uid = Integer.parseInt(cur.getAttributes().getNamedItem("uid").getNodeValue());
				User u = new User(uid, uname);
				n.setUser(u);

				String timestamp = cur.getAttributes().getNamedItem("timestamp").getNodeValue();
				n.setTimestamp(timestamp);

				boolean visible = Boolean.parseBoolean(cur.getAttributes().getNamedItem("visible").getNodeValue());
				n.setVisible(visible);

				double lon = Double.parseDouble(cur.getAttributes().getNamedItem("lon").getNodeValue());
				double lat = Double.parseDouble(cur.getAttributes().getNamedItem("lat").getNodeValue());
				n.setLatitude(lat);
				n.setLongitude(lon);

				int version = Integer.parseInt(cur.getAttributes().getNamedItem("version").getNodeValue());
				n.setVersion(version);

				NodeList tags = cur.getChildNodes();
				for (int i = 0; i < tags.getLength(); i++) {
					org.w3c.dom.Node nod = tags.item(i);
					if ("tag".equals(nod.getNodeName())) {
						String key = nod.getAttributes().getNamedItem("k").getNodeValue();
						String value = nod.getAttributes().getNamedItem("v").getNodeValue();
						n.getTags().add(new Tag(key, value));
					}
				}
				Log.d("Philip", getClass().getSimpleName() + ": got Node: " + n);
				return n;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
}
