package jo.edu.just;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.contributor.OSMUploader;
import org.osmdroid.google.wrapper.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView.Projection;

import jo.edu.just.Shapes.JoPoint;
import jo.edu.just.Shapes.joBitmap;
import jo.edu.just.Shapes.joButton;
import jo.edu.just.Shapes.joPath;
import jo.edu.just.Shapes.joRectangle;
import jo.edu.just.Shapes.joRelativebuttonWithIcon;
import jo.edu.just.Shapes.joShape;
import jo.edu.just.multiTouch.MoveGestureDetector;
import jo.edu.just.multiTouch.RotateGestureDetector;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.Touch;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

public class DrawOverMap {
	Context father;
	Vibrator vibe; 
	Paint paint = new Paint();
	
	//touch listeners 
	static  GestureDetector gestureDetector ;
	static  MoveGestureDetector mMoveDetector;
	static  RotateGestureDetector mRotateDetector;
	static  ScaleGestureDetector mScaleDetector; 
	static Map<joShape, JoPoint> tempRefMap;
	//to switch between them as a 2 buffers 
	Bitmap AllBitmap1; 
	static Bitmap AllBitmap2 = null; 
	Canvas AllCanvas;  
	static  Canvas AllCanvas2 = null; 
	boolean UseTheSecondLayer = false;  
	boolean CreatedObject  = false;  
	
	int LastPointerCount = -1;
	
	//--------
	float oldX;  
	float oldY;  
	
	float oldX_abs;  
	float oldY_abs;  
	
	
	boolean IsDown=false;
	boolean InScaleMode = false;
	org.osmdroid.views.MapView    mOsmMap;
	org.osmdroid.views.MapView.Projection  ProjUnit;
	
	//private int mVpl;// viewport left, top, right, bottom
    //private int mVpt;
    //private int mVpr;
    //private int mVpb;
   Rect rctPtr;
	
	joBitmap dynamicBitmap = new joBitmap(); 
		
	boolean IsDirtyTouch = false;
	public void PrepareForDirtyTouch(){
		IsDirtyTouch = false;
	}
	public boolean IsThereDirtyTouch(){
		return IsDirtyTouch;
	}
	
	public void PostDraw(){
		mOsmMap.invalidate();
	}	
	
	// Two routines to transform and scale between viewport and mapview
    public  float transformX(float in) {
        float out;
        out = ((rctPtr.right - rctPtr.left) * in) / (mOsmMap.getRight() - mOsmMap.getLeft())
                + rctPtr.left;
        return out;
    }
    

    public float transformY(float in) {
        float out;
        out = ((rctPtr.bottom- rctPtr.top) * in) / (mOsmMap.getBottom() - mOsmMap.getTop())
                + rctPtr.top;
        return out;
    }
    
    public void AssignrectView(Rect rct){
    	rctPtr = rct;
    	dynamicBitmap.screenRect = rct;
    }
    
    
   // public DrawOverMap(Object source){
   // 	super(source);
   // }
    
    public class FinishDrawEventClass extends java.util.EventObject {
        //here's the constructor
        public FinishDrawEventClass(Object source) {
            super(source);
        }
}
   
   
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(FinishDrawEventClassListener listener)  {
    	_listeners.add(listener);
    }
    public synchronized void removeEventListener(FinishDrawEventClassListener listener)   {
    	    _listeners.remove(listener);
    	  }
    private synchronized void fireEventFinishDraw() {
    		  FinishDrawEventClass  event = new FinishDrawEventClass (this);
    	    Iterator i = _listeners.iterator();
    	    while(i.hasNext())  {
    	      ((FinishDrawEventClassListener) i.next()).handleFinishDrawClassEvent(event);
    	    }
    	} 
    
    
    
public joButton AddButton (int ID,String text_ ,float posx_,float posy_, float width_, float height_){
	joButton x = new joButton(ID,text_,posx_,posy_,width_,height_);
	dynamicBitmap.Addbutton(x);
	return x;
}
    
public DrawOverMap(Context context){
		
		father =context;
		dynamicBitmap.FatherContext  = father;
		//setFocusable(true);
		//setFocusableInTouchMode(true);
		//this.setOnTouchListener(this);
		
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		//see http://www.croczilla.com/bits_and_pieces/svg/samples/linestroke/linestroke.xml
		paint.setStrokeWidth(20.0f);
		//paint.setStrokeCap(Cap.ROUND);   
		//paint.setStrokeJoin(Join.ROUND);   
		//paint.setStrokeMiter(4.0f);        
		
		vibe = (Vibrator) father.getSystemService(Context.VIBRATOR_SERVICE) ;
		int width =  ((WindowManager)(father.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getWidth();
		int height = ((WindowManager)(father.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getHeight();
	
		//AllBitmap1  = Bitmap.createBitmap(width,height,Config.ARGB_8888 );
		if (AllBitmap2 == null)
			AllBitmap2 	= Bitmap.createBitmap(width,height,Config.ARGB_8888 );
		AllBitmap2.eraseColor(Color.argb(0,0,0,0)  );
		//AllCanvas	= new Canvas(AllBitmap1);
		if (AllCanvas2 == null)
			AllCanvas2	= new Canvas(AllBitmap2);
		
		dynamicBitmap.SetBackColor(Color.WHITE );
		dynamicBitmap.SetOptions(width,height,AllBitmap1,AllBitmap2,AllCanvas,AllCanvas2, paint);
		
		gestureDetector= new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		  		
		
			
			
			@Override
			public boolean onDown(MotionEvent e){
				if (!dynamicBitmap.CanSendTouchToButtons) return true;
				joRelativebuttonWithIcon 
				x= dynamicBitmap.
				GetbuttontoRectIsClickedwithScale(	
						e.getX(),
						e.getY(),
						rctPtr,
						mOsmMap.getRight(),
						mOsmMap.getLeft(),
						mOsmMap.getTop(),
						mOsmMap.getBottom());
				if (x != null){
					//log.d("on down event fired for button related to rect", "onDown");
					x.fireEventHover();
					x.SetHover(true);
					mOsmMap.invalidate();
					return true;
				}
				
				
				
				joButton y = dynamicBitmap.GetbuttonIsClickedwithScale(e.getX(), e.getY(), rctPtr, mOsmMap.getRight(), mOsmMap.getLeft(),mOsmMap.getTop(),mOsmMap.getBottom());
				
				if (y != null){
					//log.d("on down event fired", "onDown");
					y.fireEventHover();
					y.SetHover(true);
					mOsmMap.invalidate();
				}
				
				return true;
			}
			
			public boolean onSingleTapUp(MotionEvent e){
				
				if (!dynamicBitmap.CanSelectObjects && !dynamicBitmap.CanSendTouchToButtons) return true;
				
				joShape x;
				joButton y = null;
				joRelativebuttonWithIcon z;
				x = dynamicBitmap.GetObjectOnPosWithScale(e.getX(), e.getY(), rctPtr, mOsmMap.getRight(), mOsmMap.getLeft(),mOsmMap.getTop(),mOsmMap.getBottom());
				y = dynamicBitmap.GetbuttonIsClickedwithScale(e.getX(), e.getY(), rctPtr, mOsmMap.getRight(), mOsmMap.getLeft(),mOsmMap.getTop(),mOsmMap.getBottom());
				
				if(dynamicBitmap.InEditMode){
					z = dynamicBitmap.GetbuttontoRectIsClickedwithScale(e.getX(), e.getY(), rctPtr, mOsmMap.getRight(), mOsmMap.getLeft(),mOsmMap.getTop(),mOsmMap.getBottom());
					if(x == null && y == null && z == null ) 
						do{
							if (dynamicBitmap.CanSelectObjects)dynamicBitmap.ReleaseObjectFromEditMode(null);
						}while(dynamicBitmap.InEditMode);
					//dynamicBitmap.DrawLayer1();
					//dynamicBitmap.DrawLayer2();
					PostDraw();//invalidate();
					
					if (y != null && x == null && z ==null){
						if(dynamicBitmap.CanSendTouchToButtons)y.fireEventClick();
					}
					if (z != null){
						if (dynamicBitmap.CanSendTouchToButtons)z.fireEventClick();
					}
				}
				if (!dynamicBitmap.InEditMode && y != null){
					if (dynamicBitmap.CanSendTouchToButtons)y.fireEventClick();
				}
					
				
				return true;
			}
			
			public void onLongPress(MotionEvent e) {
		    	//log.d("hhr","long begin");
				if (!dynamicBitmap.CanSelectObjects) return ;		    	
		    	vibe.vibrate(100);
		    	if (IsDown){
		    		mMoveDetector.InvokeMoveEnd();
		    	}
		    	IsDown = true;
		    	
		    	joShape x = 
		    	dynamicBitmap.GetObjectOnPosWithScale(e.getX(), e.getY(), rctPtr, mOsmMap.getRight(), mOsmMap.getLeft(),mOsmMap.getTop(),mOsmMap.getBottom());
		    	
		        //joShape x = dynamicBitmap.GetObjectOnPos(transformX(e.getX()) ,transformY( e.getY()));
		       //if (x!=null) ((joPath)x).LineColor = Color.RED;
		         if (x != null){
		           if(x.inEditMode){
		        	   dynamicBitmap.ReleaseObjectFromEditMode(x);
		           }else{
		        	   dynamicBitmap.MoveObjectToEditMode(x);
		           }
		    	   //dynamicBitmap.DrawLayer2();
		    	   //dynamicBitmap.DrawLayer1();		    	   
		       }

		       IsDown = false;
		       //invalidate();
		       PostDraw();
		    }
		    
		    
		    
		    
		});	

		mMoveDetector = new MoveGestureDetector(father.getApplicationContext(),new MoveGestureDetector.OnMoveGestureListener() {
			
			@Override
			public void onMoveEnd(MoveGestureDetector detector) {
				
				
				
				
				if (IsDown && !dynamicBitmap.InEditMode){
					if (dynamicBitmap.content.size() >0 && dynamicBitmap.LastItem() != null){
					//temp block here { just to make polygons
						JoPoint tmpPoint = ((joPath)dynamicBitmap.LastItem()).getPoint(0);
						((joPath)dynamicBitmap.LastItem()).AddPoint(tmpPoint.x, tmpPoint.y);
						((joPath)dynamicBitmap.LastItem()).GetLastPoint().GeoPTS = new GeoPoint(tmpPoint.GeoPTS);
						
					//end of temp block	
						dynamicBitmap.LastItem().EndShape();
						dynamicBitmap.MoveObjectToEditMode(dynamicBitmap.LastItem());
						PostDraw();
						dynamicBitmap.tempLast = null;
						fireEventFinishDraw();
						
						//- test block to show the annotation after finish drawing
						
						
						
						
					//	Intent i2 = new Intent("jo.edu.just.TextNotation");
					//	Bundle extras2 = new Bundle();
					//	extras2.putString("annotation",dv.GetObjectAnnotation());
					//	i2.putExtras(extras2);
						
						
						//
						
						
					}
					//AllBitmap1.eraseColor(Color.RED);
					//dynamicBitmap.LastItem().Draw(AllCanvas, paint);
				
					IsDown = false;
				}else if (IsDown && dynamicBitmap.InEditMode && !InScaleMode){
					for(joShape x : dynamicBitmap.tempLayer ){
						x.ParentHandle.translateX = x.translateX;
						x.ParentHandle.translateY = x.translateY;
						((joPath)x.ParentHandle).RefreshGeoFromPixelsIncludingTranslation(mOsmMap);
					}
					IsDown = false;
					tempRefMap = null;
					//dynamicBitmap.DrawLayer1();
					//dynamicBitmap.DrawLayer2();
					//invalidate();
					PostDraw();
				}
				// TODO Auto-generated method stub
				//log.d("hhr","move end");
				//InScaleMode= false;
			}
			
			@Override
			public boolean onMoveBegin(MoveGestureDetector detector) {
				//log.d("hhr","move begin");
				
				InScaleMode = false;
				
				if (!dynamicBitmap.InEditMode && !IsDown && dynamicBitmap.CanBuildNewObjects){
					//Log.d("hhr serial","1x");
					
					//IGeoPoint tGeo = 	 ProjUnit.fromPixels(detector.getFocusX(), detector.getFocusY());
					oldX = transformX( detector.getFocusX());//tGeo.getLatitudeE6();
					oldY = transformY(detector.getFocusY());//tGeo.getLongitudeE6();
					oldX_abs = detector.getFocusX();
					oldY_abs = detector.getFocusY();
					
					CreatedObject = false;
					//Log.d("hhr serial","2x");
					//dynamicBitmap.AddItem(new joPath(paint.getColor()));
					//UseTheSecondLayer = dynamicBitmap.LastItem().UseTheSecondLayer();
					IsDown = true;
					// TODO Auto-generated method stub
					
					return true;
				}else if (dynamicBitmap.InEditMode && !IsDown && !InScaleMode){//DragMode For selected Object
					IsDown = true;
					//Log.d("hhr serial","3x");
					tempRefMap = new HashMap<joShape, JoPoint>();
					for(joShape x: dynamicBitmap.tempLayer){
						tempRefMap.put(x,new JoPoint( 
								detector.getFocusX() - x.translateX,  
								detector.getFocusY() - x.translateY
								));
					
					}
					//Log.d("hhr serial","4x");
					return true;
				}
				else 
					return false;
			}

			@Override
			public boolean onMove(MoveGestureDetector detector) {
				//log.d("hhr","move con");
				if (detector.getNumberOfPoints()>1) return false;
				
				
				Log.d("touch in ","x: " + detector.getFocusDelta().x + "," + detector.getFocusDelta().y);
				Log.d("touch in ","x: " + transformX(detector.getFocusDelta().x) + "," + transformX(detector.getFocusDelta().y));
				Log.d("touch in ","-");
				if (IsDown  && !dynamicBitmap.InEditMode   ){
					
					if (!CreatedObject){
						if (dynamicBitmap.Distance(oldX, oldY,transformX( detector.getFocusDelta().x),transformY( detector.getFocusDelta().y)) < joBitmap.DRAG_THRESHOLD ) return true;
						//log.d("hhr","distance");
						CreatedObject = true;
						dynamicBitmap.AddItem(new joPath(paint.getColor(),paint.getStrokeWidth()));
						((joPath)(dynamicBitmap.LastItem())).AddPoint(oldX,oldY);
						((joPath)(dynamicBitmap.LastItem())).GetLastPoint().UpdateMyGeoFromScreencoords(ProjUnit,oldX_abs , oldY_abs);
						//
						UseTheSecondLayer = dynamicBitmap.LastItem().UseTheSecondLayer();
					}
					if (dynamicBitmap.LastItem() !=  null){
						//AllCanvas.drawLine(oldX, oldY, detector.getFocusDelta().x,detector.getFocusDelta().y,paint);
						//((joPath)(dynamicBitmap.LastItem())).AddLine(oldX, oldY, detector.getFocusDelta().x, detector.getFocusDelta().y);
						
						//IGeoPoint tGeo = 	 ProjUnit.fromPixels(detector.getFocusDelta().x, detector.getFocusDelta().y);
						
						((joPath)(dynamicBitmap.LastItem())).AddPoint(transformX( detector.getFocusDelta().x),transformY(detector.getFocusDelta().y));
						((joPath)(dynamicBitmap.LastItem())).GetLastPoint().UpdateMyGeoFromScreencoords(ProjUnit, detector.getFocusDelta().x, detector.getFocusDelta().y);
						oldX = transformX(detector.getFocusDelta().x);
						oldY = transformY(detector.getFocusDelta().y);
						oldX_abs = detector.getFocusDelta().x;
						oldY_abs = detector.getFocusDelta().y;
						//postInvalidate();
						PostDraw();
					}
				} else if(!InScaleMode &&  IsDown && dynamicBitmap.InEditMode && dynamicBitmap.tempLayer.size()> 0){
					for(joShape x: dynamicBitmap.tempLayer){
						x.translateX = detector.getFocusX() - tempRefMap.get(x).x;
						x.translateY = detector.getFocusY() - tempRefMap.get(x).y;
		
							x.ParentHandle.translateX = x.translateX;
							x.ParentHandle.translateY = x.translateY;
						
					}
					//dynamicBitmap.DrawLayer2();
					//postInvalidate();
					PostDraw();
				}
				
				// TODO Auto-generated method stub
				
				return true;
			}
		} );
		
		mRotateDetector = new RotateGestureDetector(father.getApplicationContext(),new RotateGestureDetector.OnRotateGestureListener() {
			
			@Override
			public void onRotateEnd(RotateGestureDetector detector) {
				// TODO Auto-generated method stub
				if (dynamicBitmap.InEditMode){
					if (dynamicBitmap.tempLayer.size()!=0){
						for(joShape x : dynamicBitmap.tempLayer){
							x.ParentHandle.Angle = x.Angle;
						}
						//dynamicBitmap.DrawLayer1();
						//invalidate();
						PostDraw();
					}
				}
			}
			
			@Override
			public boolean onRotateBegin(RotateGestureDetector detector) {
				//log.d("hhr","rotate begin");
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean onRotate(RotateGestureDetector detector) {
				// TODO Auto-generated method stub
				//log.d("hhr","rotate con" + detector.getRotationDegreesDelta());
				if (dynamicBitmap.InEditMode){
					InScaleMode = true;
					if (dynamicBitmap.tempLayer.size()!=0){
						for(joShape x: dynamicBitmap.tempLayer){
							x.Angle -= detector.getRotationDegreesDelta();
							x.ParentHandle.Angle = x.Angle;
						}
						
					//((joRectangle)dynamicBitmap.tempLayer.get(dynamicBitmap.tempLayer.size()-1) ).Angle -= detector.getRotationDegreesDelta();
						//dynamicBitmap.DrawLayer2();
						//postInvalidate();
						PostDraw();
					}
					
					
					
				}
				
				return true;
			}
		} );
		
		mScaleDetector = new ScaleGestureDetector(father.getApplicationContext(), new ScaleGestureDetector.OnScaleGestureListener() {
			
			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				// TODO Auto-generated method stub
				if (dynamicBitmap.InEditMode){
					//InScaleMode = false;
					if (dynamicBitmap.tempLayer.size()!=0){
						for(joShape x : dynamicBitmap.tempLayer){
							x.ParentHandle.scale = x.scale;
						}
					//dynamicBitmap.DrawLayer1();
					//invalidate();
						PostDraw();
					}
					
				}
				
			}
			
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				// TODO Auto-generated method stub
				//log.d("hhr","scale begin"); 
				return true;
			}
			
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				//log.d("hhr","scale con" + detector.getScaleFactor());
				if (dynamicBitmap.InEditMode){
					InScaleMode = true;
					if (dynamicBitmap.tempLayer.size()!=0){
						for(joShape x: dynamicBitmap.tempLayer){
							x.scale *= detector.getScaleFactor();
							x.ParentHandle.scale = x.scale;
						}
						//dynamicBitmap.DrawLayer2();
						//postInvalidate();
						PostDraw();
					}
					
					
					
				}
				
				
				
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		
	}


public void AssignProjector(org.osmdroid.views.MapView.Projection projection){
	ProjUnit = projection;
}
public void onDraw(Canvas canvas) {
		
		canvas.save();
		
		
		Canvas tmp1 = dynamicBitmap.AllCanvas,
			   tmp2 = dynamicBitmap.AllCanvas2;
		
		
		dynamicBitmap.AllCanvas = dynamicBitmap.AllCanvas2 = canvas;
		dynamicBitmap.DrawLayer1();
		
		if(dynamicBitmap.InEditMode)dynamicBitmap.DrawLayer2();
		//canvas.drawBitmap(AllBitmap1,0,0, paint);
		//if (dynamicBitmap.InEditMode)
		//canvas.drawBitmap(AllBitmap2,0,0,paint);
		dynamicBitmap.AllCanvas  = tmp1;
		dynamicBitmap.AllCanvas2 = tmp2;
		
		canvas.restore();
	}
public boolean onTouch(MotionEvent event) 
{
	
	 
	
	
	
	if ( event.getPointerCount() ==1 && InScaleMode  && event.getAction() == MotionEvent.ACTION_UP ){
		// this if block just to prevent the move while the two gestures used to rotate and scale
		//log.d("number of pointers change", "from 2 to 0 in scale mode");
		InScaleMode = false;
	}
	
	if (event.getAction() == MotionEvent.ACTION_UP){
		for(joButton x : dynamicBitmap.buttons){
			x.SetHover(false);
			
		}
		
		for(joShape x : dynamicBitmap.tempLayer){
			if (x.SHAPE_TYPE == Flags.SHAPE_RECTANGLE_FOR_SELECT){
				for (joRelativebuttonWithIcon x2 : ((joRectangle)x).RelativeButtons){
					x2.SetHover(false);
				}
			}
		}
		
		mOsmMap.invalidate();
	}
	
	
	if (LastPointerCount  != event.getPointerCount()) {
		LastPointerCount = event.getPointerCount();
	}
	
	IsDirtyTouch = true;
	mMoveDetector.onTouchEvent(event);
	mRotateDetector.onTouchEvent(event);
	mScaleDetector.onTouchEvent(event);
	gestureDetector.onTouchEvent(event);

	//invalidate();
	return true;
}
public void UndoClick(){
	dynamicBitmap.switchLasttoRedoStack();
	AllBitmap1.eraseColor(dynamicBitmap.BackColor);
	for (joShape x : dynamicBitmap.content) {
		x.Draw(AllCanvas, paint);
	}
	//invalidate();
	PostDraw();

}
public void RedoClick(){
	dynamicBitmap.switchRedoStackToLast();
	AllBitmap1.eraseColor(dynamicBitmap.BackColor);
	for (joShape x : dynamicBitmap.content) {
		x.Draw(AllCanvas, paint);
	}
	PostDraw();//invalidate();
}
public void DeleteClick(){
	dynamicBitmap.RemoveSelectedObjects();
	dynamicBitmap.DrawLayer1();
	dynamicBitmap.DrawLayer2();
	PostDraw();//invalidate();
}
public void UndoSelectClick(){
	dynamicBitmap.ReleaseObjectFromEditMode(null);
	dynamicBitmap.DrawLayer1();
	dynamicBitmap.DrawLayer2();
	PostDraw();//invalidate();
}
public float GetStrokeWidth(){
	
	float MaxWidth=0.0f;
	if (dynamicBitmap.InEditMode){
		for(joShape x: dynamicBitmap.tempLayer){
			if (x.ParentHandle.StrokeWidth > MaxWidth )MaxWidth = x.ParentHandle.StrokeWidth;
		}
		return MaxWidth;
	}
	return paint.getStrokeWidth();
}
public void SetStrokeWidth(float Value){
	
	if (dynamicBitmap.InEditMode){
		for(joShape x : dynamicBitmap.content){
			if (x.inEditMode){
				x.StrokeWidth = Value;
			}
		}
		dynamicBitmap.DrawLayer1();
		dynamicBitmap.DrawLayer2();
		PostDraw();//postInvalidate();
	}else
		paint.setStrokeWidth(Value);
}
public int GetSelectColor(){
	
	
	if (dynamicBitmap.InEditMode){
		for(joShape x: dynamicBitmap.tempLayer){
			return x.ParentHandle.LineColor;
		}
	}
	return paint.getColor();
}
public void SetBackGroundColor(int _Color){
	dynamicBitmap.SetBackColor(_Color);
	dynamicBitmap.DrawLayer1();
	PostDraw();//postInvalidate();
}
public void SetBackGroundColor_ColdSet(int _Color){
	dynamicBitmap.SetBackColor(_Color);
	//dynamicBitmap.DrawLayer1();
	//PostDraw();//postInvalidate();
}
public int GetBackGroundColor(){
	return dynamicBitmap.GetBackColor();
}
public void SetSelectColor(int _Color){
	
	if (dynamicBitmap.InEditMode){
		for(joShape x : dynamicBitmap.content){
			if (x.inEditMode){
				x.LineColor = _Color;
			}
		}
		dynamicBitmap.DrawLayer1();
		dynamicBitmap.DrawLayer2();
		PostDraw();//postInvalidate();
	}else
		paint.setColor(_Color);
}
public String GetObjectAnnotation(){
	if (dynamicBitmap.InEditMode && dynamicBitmap.tempLayer.size()>0){
		return dynamicBitmap.tempLayer.get(dynamicBitmap.tempLayer.size()-1).ParentHandle.TextTAG;
	}
	return "No object Selected!";
}
public void SetObjectAnnotation(String Value){
	
	if (dynamicBitmap.InEditMode){
		for(joShape x : dynamicBitmap.tempLayer){
			x.ParentHandle.TextTAG= Value;	
		}
		dynamicBitmap.DrawLayer1();
		dynamicBitmap.DrawLayer2();
		PostDraw();//postInvalidate();
	}
}
public Boolean BackPressed(){	//if true it means you can exit 
	if (dynamicBitmap.tempLayer.size()==0) {
		dynamicBitmap.DrawLayer1();
		dynamicBitmap.DrawLayer2();
		PostDraw();//invalidate();
		return true; // you can exit
	}
	UndoSelectClick();
	return false;
}
public void AssignMap(org.osmdroid.views.MapView  x){
	mOsmMap = x;
}
	
}
