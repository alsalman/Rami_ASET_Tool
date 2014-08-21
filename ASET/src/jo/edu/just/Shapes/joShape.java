package jo.edu.just.Shapes;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.format.Time;

public abstract class joShape {
	
	public int SHAPE_TYPE = 0;
	
	
	public String TextTAG = "";
	public Time CreateTime=null;
	public Time FinalizeTime=null;
	public boolean inEditMode = false;
	
	
	//this variable used if the rectangle created to select object for edit
	public joShape  ParentHandle = null;
	
	public int LineColor = Color.RED;
	public float Angle = 0.0f;
	public float scale = 1.0f;
	public float StrokeWidth = 30.0f;
	public Path path =null; //used when needed :P 
	
	public float translateX=0.0f;
	public float translateY=0.0f;
	
	
	public String GetAnnotationString(){
		try{
			String [] temp = TextTAG.split("[|]+"); 
			return temp[0];
		}catch (Exception e){
			return  null;
		}
	}
	public String GetAnnotationStringType(){
		try{
			String [] temp = TextTAG.split("[|]+"); 
			return temp[1];
		}catch (Exception e) {
			return null;
		}
	}
	
	public int getColor(){
		if (inEditMode)
			return Color.argb(128, 128,128,128);
		else
			return LineColor;
	}
	
	public joShape(){
		Time x = new Time();
		x.setToNow();
		CreateTime  = x;
	}
	
	public void EndShape(){
		Time x = new Time();
		x.setToNow();
		FinalizeTime  =x;
	}
	public abstract void Draw(Canvas cav,Paint paint);
	public abstract float [] GetMinBoundBox(); 
	public abstract float [] GetCenterPoint();
	
	public boolean UseTheSecondLayer(){
		return false;
	}
	
	
	
}
