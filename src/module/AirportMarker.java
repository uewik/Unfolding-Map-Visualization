package module;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PGraphics;
import processing.core.PConstants;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Hao Zhang
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(11);
		pg.ellipse(x, y, 5, 5);
			
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		String name = getName();
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(name) +6, 18, 5);
	
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(name, x + 3 , y +18);
		
	
		pg.popStyle();
	}
	
	public String getName() {
		String text = getStringProperty("name");
		return text.substring(1, text.length() - 2);
	}
	
}
