package jo.edu.just.Shapes;

import jo.edu.just.sharedSettings;

import org.osmdroid.google.wrapper.Projection;
import org.osmdroid.util.GeoPoint;

public class JoPoint {
	public JoPoint(float _x,float _y){
		x = _x;
		y = _y;
	}
	
	public JoPoint (float _x , float _y , float _projX ,float _projy){
		x = _x;
		y = _y;
		ProjX = _projX;
		ProjY = _projy;
	}
	public float x,y,ProjX,ProjY;
	
	public void UpdateMyGeoFromScreencoords(org.osmdroid.views.MapView.Projection prj,float _x ,float _y){
		GeoPTS = (GeoPoint)prj.fromPixels(_x, _y);
		//GeoPTS = sharedSettings.GetGeoPointFromScreenPixel(sharedSettings.Mosmv, _x, _y);
	}
	
	public GeoPoint GeoPTS;
	//public float dx,dy;
	
	public JoPoint clone(){
		JoPoint tmp = new JoPoint(x,y);
				tmp.ProjX = this.ProjX;
				tmp.ProjY = this.ProjY;
		return tmp;
	}
}
