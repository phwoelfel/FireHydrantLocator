package at.woelfel.philip.firehydrantlocator.osmapi;

public class Tag {
	private String key;
	private String value;
	
	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return key +": " +value;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
