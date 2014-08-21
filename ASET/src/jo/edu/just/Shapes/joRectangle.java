package jo.edu.just.Shapes;

import java.util.ArrayList;
import java.util.List;

import jo.edu.just.Flags;
import android.R.style;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Xfermode;
import android.graphics.PathDashPathEffect.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.format.Time;

public class joRectangle extends joShape {

	
	//public float Angle=0.0f;
	//public float scale = 1.0f;
	
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	
	static DashPathEffect effectDash = new DashPathEffect(new float[] {10, 5, 5, 5},3);
	
	
	
	
	
	public Context fatherContext ;
	//public joRelativebuttonWithIcon hClose;
	//public joRelativebuttonWithIcon hCam;
	public joRectangle(){
		this.SHAPE_TYPE = Flags.SHAPE_RECTANGLE_FOR_SELECT;
		Time x = new Time();
		x.setToNow();
		CreateTime  = x;
		
		
		
		
		
	}
	public List<joRelativebuttonWithIcon> RelativeButtons = new ArrayList<joRelativebuttonWithIcon>();
	
	public void CreateUpdateButtons(){
		
		
		
		joRelativebuttonWithIcon
		hClose = new joRelativebuttonWithIcon(fatherContext,0,"a",x2-60,y1 - 60,x2,y1-4 );
		hClose.ButtonID =0;
		hClose.TypeCloseOrCam = Flags.SHAPE_BUTTON_FOR_RECTANGLE_SELECT_CLOSE;
		hClose.ParentRectangle = this;
		hClose.Text ="w";
		
//		joRelativebuttonWithIcon
//		hCam = new joRelativebuttonWithIcon(fatherContext,0,"b",x2-60 -64,y1 - 60,x2-64,y1-4 );
//		hCam.ButtonID =1;
//		hCam.TypeCloseOrCam = Flags.SHAPE_BUTTON_FOR_RECTANGLE_SELECT_CAMERA;
//		hCam.ParentRectangle = this;
//		hCam.Text ="b";
		
		RelativeButtons.add(hClose);
//		RelativeButtons.add(hCam);
		
		
	}
	public joRectangle(int _LineColor){
		this();
		LineColor  = _LineColor;
	}
	
	
	
	
	@Override
	public void Draw(Canvas cav, Paint paint) {
		
		
		
		cav.save(Canvas.MATRIX_SAVE_FLAG);
		
		float [] ct = GetCenterPoint();
		
		cav.translate(translateX, translateY);
		cav.rotate(Angle,ct[0],ct[1]);
		cav.scale(scale, scale,ct[0],ct[1]);
		
		
		
		
		
		int tmpColor = paint.getColor();
		float tmpWidth = paint.getStrokeWidth();
		Paint.Style tmpS   = paint.getStyle();
		paint.setStyle(Paint.Style.STROKE);
 		paint.setColor(getColor());
 		paint.setStrokeWidth(3f);
 		
 		paint.setPathEffect(effectDash);
 		
 		//MaskFilter tempMask = paint.getMaskFilter();
 		//paint.setMaskFilter(new BlurMaskFilter(15, Blur.SOLID));
 		
		cav.drawRect(x1, y1, x2,y2, paint);
		paint.setColor(tmpColor);
		paint.setStyle(tmpS);
		paint.setStrokeWidth(tmpWidth);
		paint.setPathEffect(null);
		cav.restore();
		
		
		
		
		for(joRelativebuttonWithIcon x: this.RelativeButtons ){
			x.translateX = translateX;
			x.translateY = translateY;
			x.Angle 	 = Angle;
			x.scale 	 = scale;
			x.ct 		 = ct;
			x.Draw(cav, paint);
			
		}
		
		
		
	}
	
	@Override
	public float[] GetCenterPoint(){
		return new float[]{(x1+x2)/2  ,(y1+y2)/2 };
	}
	
	public boolean UseTheSecondLayer(){
		return true;
	}
	
	@Override
	public  float [] GetMinBoundBox(){
		return null;
		
	}
	
}
