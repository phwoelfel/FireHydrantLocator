package at.woelfel.philip.firehydrantlocator;

import android.app.Application;
import android.content.res.Resources;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHPosition;
import at.woelfel.philip.firehydrantlocator.FireHydrant.FHType;

public class FHApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	public FHType getTypeFromString(String stype){
		Resources res = getResources();

		FHType type = null;
//		String sel_type = (String) ((Spinner) findViewById(R.id.spinner_hydrant_type)).getSelectedItem();
		if (stype.equals(res.getString(R.string.hydrant_type_pillar))) {
			type = FHType.PILLAR;
		}
		if (stype.equals(res.getString(R.string.hydrant_type_pond))) {
			type = FHType.POND;
		}
		if (stype.equals(res.getString(R.string.hydrant_type_underground))) {
			type = FHType.UNDERGROUND;
		}
		if (stype.equals(res.getString(R.string.hydrant_type_wall))) {
			type = FHType.WALL;
		}

		return type;
	}
	
	public String getStringFromType(FHType type){
		Resources res = getResources();
		switch (type) {
			case PILLAR:
				return res.getString(R.string.hydrant_type_pillar);
			case POND:
				return res.getString(R.string.hydrant_type_pond);
			case UNDERGROUND:
				return res.getString(R.string.hydrant_type_underground);
			case WALL:
				return res.getString(R.string.hydrant_type_wall);

			default:
				return null;
		}
	}
	
	
	public FHPosition getPositionFromString(String spositions){
		Resources res = getResources();
		
		FHPosition pos = null;
//		String sel_positions = (String) ((Spinner) findViewById(R.id.spinner_hydrant_position)).getSelectedItem();
		if (spositions.equals(res.getString(R.string.hydrant_position_green))) {
			pos = FHPosition.GREEN;
		}
		if (spositions.equals(res.getString(R.string.hydrant_position_lane))) {
			pos = FHPosition.LANE;
		}
		if (spositions.equals(res.getString(R.string.hydrant_position_parkinglot))) {
			pos = FHPosition.PARKINGLOT;
		}
		if (spositions.equals(res.getString(R.string.hydrant_position_sidewalk))) {
			pos = FHPosition.SIDEWALK;
		}
		return pos;

	}
	
	public String getStringFromPosition(FHPosition pos){
		Resources res = getResources();
		switch (pos) {
			case GREEN:
				return res.getString(R.string.hydrant_position_green);
			case LANE:
				return res.getString(R.string.hydrant_position_lane);
			case SIDEWALK:
				return res.getString(R.string.hydrant_position_sidewalk);
			case PARKINGLOT:
				return res.getString(R.string.hydrant_position_parkinglot);

			default:
				return "";
		}

	}
	
	
}
