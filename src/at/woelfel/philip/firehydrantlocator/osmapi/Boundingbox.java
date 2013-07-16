package at.woelfel.philip.firehydrantlocator.osmapi;

public class Boundingbox {
	
	private double minLon;
	private double minLat;
	private double maxLon;
	private double maxLat;
	
	
	public Boundingbox(double minLon, double minLat, double maxLon, double maxLat) {
		this.minLon = minLon;
		this.minLat = minLat;
		this.maxLon = maxLon;
		this.maxLat = maxLat;
	}
	
	@Override
	public String toString() {
		return "[minLon=" + minLon + ", minLat=" + minLat + ", maxLon=" + maxLon + ", maxLat=" + maxLat + "]";
	}
	
	
	/**
	 * @return the minLon
	 */
	public double getMinLon() {
		return minLon;
	}
	/**
	 * @param minLon the minLon to set
	 */
	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}
	/**
	 * @return the minLat
	 */
	public double getMinLat() {
		return minLat;
	}
	/**
	 * @param minLat the minLat to set
	 */
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}
	/**
	 * @return the maxLon
	 */
	public double getMaxLon() {
		return maxLon;
	}
	/**
	 * @param maxLon the maxLon to set
	 */
	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
	/**
	 * @return the maxLat
	 */
	public double getMaxLat() {
		return maxLat;
	}
	/**
	 * @param maxLat the maxLat to set
	 */
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	
	
	
}
