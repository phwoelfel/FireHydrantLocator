package at.woelfel.philip.firehydrantlocator.osmapi;

public class Changeset {

	private long id;
	private User user;
	private String createdat;
	private boolean open;
	private String createdby;
	private String comment;
	private Boundingbox boundingbox;
	
	public Changeset(long id) {
		this.id = id;
	}
	
	public Changeset() {
		
	}
	
	@Override
	public String toString() {
		String str = "Changeset: #" +id +"\n";
		str += "\tUser:" +user +"\n";
		str += "\tCreated at: " +createdat +"\n";
		str += "\tOpen: " +open +"\n";
		str += "\tBoundingbox: " +boundingbox +"\n";
		str += "\tCreated by: " +createdby +"\n";
		str += "\tComment: " +comment +"\n";
		return str;
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
	 * @return the createdat
	 */
	public String getCreatedat() {
		return createdat;
	}

	/**
	 * @param createdat the createdat to set
	 */
	public void setCreatedat(String createdat) {
		this.createdat = createdat;
	}

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * @return the createdby
	 */
	public String getCreatedby() {
		return createdby;
	}

	/**
	 * @param createdby the createdby to set
	 */
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the boundingbox
	 */
	public Boundingbox getBoundingbox() {
		return boundingbox;
	}

	/**
	 * @param boundingbox the boundingbox to set
	 */
	public void setBoundingbox(Boundingbox boundingbox) {
		this.boundingbox = boundingbox;
	}
	
}
