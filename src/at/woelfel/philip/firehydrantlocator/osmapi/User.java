package at.woelfel.philip.firehydrantlocator.osmapi;

public class User {
	private String username;
	private long uid;

	public User() {
		
	}
	
	public User(long id, String uname) {
		uid = id;
		username = uname;
	}
	
	
	@Override
	public String toString() {
		return username +" (" +uid +")";
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	
	
}
