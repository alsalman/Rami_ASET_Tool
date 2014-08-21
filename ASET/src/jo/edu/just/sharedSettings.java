package jo.edu.just;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.util.Log;

public class sharedSettings {
	public static int QHTC_SERVER_REQUEST_TRANSITION_MODE = 0; // 1 = QHTC_hash // 0 = normal QHTC
	public static Activity WaitDialogHandle =  null;
	public static Context FatherContext = null;
	public static MapView Mosmv = null;
	
	
	
	public static  float[] FromMapPixelToPixel(float x , float y ,org.osmdroid.views.MapView mOsmMap,Rect rctPtr){
    	float [] tmp = {0,0};
    	
    	tmp[0] = ((x - rctPtr.left  ) *((float)(mOsmMap.getRight() - mOsmMap.getLeft()) ))/ ((float)(rctPtr.right - rctPtr.left));
    	tmp[1] = ((y - rctPtr.top   ) * ((float)(mOsmMap.getBottom() - mOsmMap.getTop()) ))/ ((float)(rctPtr.bottom - rctPtr.top));
    	
    	
    	return tmp;
    }
	public  static GeoPoint GetGeoPointFromScreenPixel(MapView mp,float x ,float y){
		Projection prj = mp.getProjection();
		Rect r = mp.getScreenRect(null);
		
		Log.d("screen","left: " + r.left);
		Log.d("screen","bottom" + mp.getHeight());
		
		GeoPoint topLeft = (GeoPoint) 	 prj.fromPixels(0,0);
		GeoPoint bottomRight = (GeoPoint)prj.fromPixels(mp.getWidth()   ,mp.getHeight());

		
		GeoPoint res = new GeoPoint(
				(int)(topLeft.getLatitudeE6() + (float)((bottomRight.getLatitudeE6() - topLeft.getLatitudeE6()))* (  x/(float)(mp.getWidth()))),
				(int)(topLeft.getLongitudeE6() + (float)((bottomRight.getLongitudeE6() - topLeft.getLongitudeE6()))* (  x/(float)(mp.getHeight()))));
		
		Log.d("geo screen" , ""+ res.toDoubleString());
		return topLeft;
	}
}
