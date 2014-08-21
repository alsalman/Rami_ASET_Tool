package jo.edu.just.Shapes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jo.edu.just.ButtonClickEventClassListener;
import jo.edu.just.FinishDrawEventClassListener;
import jo.edu.just.DrawOverMap.FinishDrawEventClass;
import jo.edu.just.Flags;

import android.R.color;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.format.Time;
import android.util.Log;

public class joButton extends joShape{
	
	
	
	
	public class buttonClickEventClass extends java.util.EventObject {
        //here's the constructor
        public buttonClickEventClass(Object source) {
            super(source);
        }
}
   
	
	public boolean checked  = false;
   
	public void SetChecked( boolean check){
		checked = check;
		needsHoveringUpdate = true;
		
	}
	
	
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(ButtonClickEventClassListener listener)  {
    	_listeners.add(listener);
    }
    public synchronized void removeEventListener(ButtonClickEventClassListener listener)   {
    	    _listeners.remove(listener);
    	  }
    
    public synchronized void fireEventHover() {
		buttonClickEventClass  event = new buttonClickEventClass(this);
	    Iterator i = _listeners.iterator();
	    while(i.hasNext())  {
	      ((ButtonClickEventClassListener) i.next()).handleButtonClickClassEvent(true,false,false,event);
	    }
	}
    public synchronized void fireEventClick() {
    		buttonClickEventClass  event = new buttonClickEventClass(this);
    	    Iterator i = _listeners.iterator();
    	    while(i.hasNext())  {
    	      ((ButtonClickEventClassListener) i.next()).handleButtonClickClassEvent(false,false,true,event);
    	    }
    } 
    
    
    
    
	
   
	
	
	
	
	
	
	
	
	
	//private List<Float>  pts = new ArrayList<Float>();
	
	//screen width and height 
	private float  width=0;
	private float  height =0;
	private float  posX;
	private float  posY;
	
	
	
	
	
	public int ButtonID = -1;
	
	public joButton(){
		SHAPE_TYPE = Flags.SHAPE_BUTTON;
		Time x = new Time();
		x.setToNow();
		CreateTime  = x;
	}
	
	
	
	public String Text ; 
	
	public joShape LinkedObjectForName = null;
	
	
	
	private String  GetString(){
		if (LinkedObjectForName != null) return " CurrentTag: " +LinkedObjectForName.GetAnnotationString();
		else return   Text ;
	}
	
	
	
	public boolean visible = true ;
	public int color   =  Color.argb(200, 255, 0, 0);
	
	
	boolean IsHover = false;
	boolean needsHoveringUpdate = true;
	public void SetHover(boolean IsHovering){
		//if (IsHover !=  IsHovering) needsHoveringUpdate = true;
		IsHover = IsHovering;
		//if (needsHoveringUpdate == false) return ;
		
		
		//log.("Hovering Call", "" + IsHover);
		
		
		if (IsHover || checked){
			lnrGradient= new LinearGradient(
	 				screenRect.left + screenRect.width()* posX ,
	 				screenRect.top  + screenRect.height() * posY,  
	 				screenRect.left + screenRect.width()* posX ,
	 				screenRect.top  +  screenRect.height() * height,
	 				0xe6f3ae1b,0xe6bb6008,Shader.TileMode.MIRROR);
		}else /*if (!IsHover) */
		{
			lnrGradient= new LinearGradient(
	 				screenRect.left + screenRect.width()* posX ,
	 				screenRect.top  + screenRect.height() * posY,  
	 				screenRect.left + screenRect.width()* posX ,
	 				screenRect.top  +  screenRect.height() * height,
	 				0x00ef4444,0xe6992f2f,Shader.TileMode.MIRROR);
		}
		
		//...
	}
	
	public boolean GetHoverState(){
		return IsHover; 
	}
	
	public joButton(int ID,String _text,float RelativeX,float RelativeY,  float _RelativeWidth,float _RelativeHieght ){
		this();
		SHAPE_TYPE = Flags.SHAPE_BUTTON;
		
		ButtonID = ID;
		Text = _text;
		width = _RelativeWidth;
		height = _RelativeHieght;
		
		posX = RelativeX;
		posY = RelativeY;
		
	}
	
	public Rect screenRect = null;
	LinearGradient lnrGradient ;
	@Override
	public void Draw(Canvas cav,Paint paint){
		if (!visible) return ;
		cav.save(Canvas.MATRIX_SAVE_FLAG);
		
		cav.translate(translateX, translateY);
		cav.rotate(Angle,0,0);
		cav.scale(scale, scale,0,0);
		int tmpColor = paint.getColor();
		Shader tmpShader  = paint.getShader();
		
		
		
		float tmpWidth = paint.getStrokeWidth();
		Paint.Style tmpS   = paint.getStyle();
		Shader tempShader  = paint.getShader();
		
		
		paint.setStyle(Paint.Style.FILL);
 		paint.setColor(color);
 		paint.setStrokeWidth(1.0f);	
 		paint.setTextSize(34.0f);
 		//if (needsHoveringUpdate){
 			SetHover(IsHover);
 		//	needsHoveringUpdate = false;
 		//}
 		paint.setShader(lnrGradient);
 	  
 		
 	//	paint.setShader(new LinearGradient(0f, 0f,  screenRect.width() * width, 0f,0xffffff64,0x00ffff64, Shader.TileMode.MIRROR));
 //		paint.setShader(new SweepGradient(0, -100, new int[]{0xffffff64,0x00ffff64,0x00ffff64,0x00ffff64,0x00ffff64},null));
 //-		
 		cav.drawRoundRect( new RectF( 
 						 screenRect.left + screenRect.width()* posX,
 						 screenRect.top  + screenRect.height() * posY,
 						 screenRect.left + screenRect.width() * width,
 						 screenRect.top  +  screenRect.height() * height),
 			7.0f,5.5f
 				, paint);
 		
 		paint.setStyle(Paint.Style.STROKE);
 		
 		
 		paint.setColor(Color.RED);
 		//paint.setShader(tempShader);
 		paint.setShader(tmpShader);
 		
 		cav.drawRoundRect( new RectF( 
				 screenRect.left + screenRect.width()* posX,
				 screenRect.top  + screenRect.height() * posY,
				 screenRect.left + screenRect.width() * width,
				 screenRect.top  +  screenRect.height() * height),
	11.0f,7.5f
		, paint);
 		
 		paint.setStyle(Paint.Style.FILL_AND_STROKE);
 		paint.setColor(Color.WHITE);
 		//MaskFilter tempMask = paint.getMaskFilter();
 		//paint.setMaskFilter(new BlurMaskFilter(1, Blur.NORMAL));
 		cav.drawText(GetString(),
 				
 				screenRect.left +  screenRect.width() * posX + (screenRect.width()* posX )/48
 				
 				
 				
 				,(int)(screenRect.top + screenRect.height() * (0+ height )     - screenRect.height()* height /4 + screenRect.height()*posY  /12   )    , paint);
 //-
		paint.setColor(tmpColor);
		paint.setStyle(tmpS);
		paint.setStrokeWidth(tmpWidth);
		//paint.setMaskFilter(tempMask);
		cav.restore();
	}

	@Override
	public  float [] GetMinBoundBox(){
		//no need for implementation
		float [] tmp   =  {0,0,0,0};
		return tmp;
		
	}
	@Override
	public  float[] GetCenterPoint(){
		//no need for implementation
		return new float[]{0 ,0};
	}
}
