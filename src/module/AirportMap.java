package module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Hao Zhang
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private CommonMarker lastSelected;   //add
	private CommonMarker lastClicked;	//add
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
			m.setRadius(5);
			airportList.add(m);
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			sl.setHidden(true);
			routeList.add(sl);
		}
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
	}

	private void selectMarkerIfHover(List<Marker> markers)
	{
		for (Marker marker : markers) 
		{
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = (CommonMarker) marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else
			checkAirportsForClick();
	}

	private void checkAirportsForClick()
	{
		for (Marker airport : airportList) {
			if (!airport.isHidden() && airport.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) airport;
				for (Marker anAirport : airportList)
					if (anAirport != lastClicked) {
						anAirport.setHidden(true);
					}
				for (Marker route : routeList) {
					for (Location lc : ((SimpleLinesMarker) route).getLocations())
					{
						if (airport.getDistanceTo(lc) == 0)
						{
							route.setHidden(false);
							setAirport(airportList, (SimpleLinesMarker) route);
							break;
						}
					}
				}
				return;
			}
		}
	}
	
	private void unhideMarkers() {
		for(Marker marker : airportList)
			marker.setHidden(false);
		for(Marker route : routeList)
			route.setHidden(true);
	
	}
	
	private void setAirport(List<Marker> airportList, SimpleLinesMarker route)
	{
		for (Marker airport : airportList)
		{
			for (Location lc : route.getLocations())
			{
				if (airport.getDistanceTo(lc) == 0)
				{
					airport.setHidden(false);
					break;
				}
			}
		}
	}
}
