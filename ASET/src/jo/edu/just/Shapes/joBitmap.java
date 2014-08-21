package jo.edu.just.Shapes;

import java.util.ArrayList;
import java.util.List;

import microsoft.mappoint.TileSystem;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;

import jo.edu.just.Flags;
import jo.edu.just.MainContainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.text.format.Time;
import android.util.Log;

public class joBitmap {
	
	public static final float DRAG_THRESHOLD     = 20.0f;
	public static final float MIN_WIDTH_SELECT   = 6.0f;
	public static final float MIN_SCALE_SELECT   = 0.75f;
	public static final float WIDTH_SELECT_SHIFT = 3* 30.0f;
	
	public static final int   TAG_BUTTON_ID = 1000;
	
	Bitmap AllBitmap1; 
	Bitmap AllBitmap2; 
	public Canvas AllCanvas; 
	public Canvas AllCanvas2;
	public Paint  paint;
	
	public Context FatherContext ;
	
	public  Rect screenRect = null;
	public  MapView mOsmMap;
	
	public Boolean   DrawObjects = true;
	public Boolean   DrawBackGround = true;
	public boolean   DrawButtons = true;
	public boolean   DrawSecondLayer = true;
	public boolean   CanBuildNewObjects = true;
	public Boolean   CanSelectObjects   = true;
	public boolean   CanSendTouchToButtons = true;
	
	public  float transformX(float in) {
        float out;
        out = ((screenRect.right - screenRect.left) * in) / (mOsmMap.getRight() - mOsmMap.getLeft())
                + screenRect.left;
        return out;
    }

    public float transformY(float in) {
        float out;
        out = ((screenRect.bottom- screenRect.top) * in) / (mOsmMap.getBottom() - mOsmMap.getTop())
                + screenRect.top;
        return out;
    }
   
	
	
	
	
	public  Projection  projUnit = null;
	joShape AnnotationPtr = null;
	public void SetOptions(float _width,float _height,Bitmap Layer1,Bitmap Layer2 ,Canvas Engine,Canvas Engine2,Paint paint ){
		AllBitmap1  = Layer1;
		AllBitmap2  = Layer2;
		AllCanvas   = Engine;
		AllCanvas2  = Engine2;
		this.paint  = paint; 
		width = _width;
		height = _height;
		
	}
	
	
	
	public List<joShape> content = new ArrayList<joShape>();
	public List<joButton> buttons= new ArrayList<joButton>();
	public List<joShape> tempLayer  = new ArrayList<joShape>();
	public List<joRelativebuttonWithIcon> buttonsForRects= new ArrayList<joRelativebuttonWithIcon>();
	
	
	public List<joShape> RedoContent = new ArrayList<joShape>();
	
	public float width,height;
	public Time CreateTime;
	public Time EndTime;
	public int BackColor = Color.BLACK;
	public boolean InEditMode=false;
	
	
	public  joShape tempLast = null;
	public void AddItem(joShape obj){
		content.add(obj);
		tempLast = obj;
	}
	public void Addbutton(joButton b){
		buttons.add(b);
	}
	
	public void switchLasttoRedoStack(){
		if (content.size()>0){
			RedoContent.add(content.get(content.size()-1) );
			content.remove(content.size()-1);
		}
	}
	
	public void switchRedoStackToLast(){
		if (RedoContent.size()>0){
			content.add(RedoContent.get(RedoContent.size()-1) );
			RedoContent.remove(RedoContent.size()-1);
		}
	}
	public joShape LastItem(){
	//for fastest Access
		return tempLast;
	}

	public joButton GetButtonByID(int ID) {
		for (joButton x :this.buttons)
			if (x.ButtonID == ID) return x;
		return null;
	}
	
	
	
	
	public Point fromMapPixels(int x, int y, Point reuse) {
        final Point out = (reuse != null) ? reuse : new Point();
		out.set(x - this.mOsmMap.getWidth() / 2, y - this.mOsmMap.getHeight() / 2);

		return out;
	}
	
	public Point toPixels(GeoPoint geo_in, Point out) {
		// Convert Location to GeoPoint
		//	GeoPoint geo_in = new GeoPoint((int) (in.getLatitude() / 1e6),
		//				(int) (in.getLongitude() / 1e6));
		//GeoPoint geo_in = new GeoPoint(in);

		// use toMapPixels, as toPixels is not implemented correctly in osmdroid
		projUnit.toMapPixels(geo_in, out);
		
		
		
		//this.mOsmMap.getProjection().fromPixelsToProjected(screenRect);
		//fromMapPixels(out.x, out.y, out);
		// convert from mappixels to screenpixels
		
		out.offset(-this.screenRect.left, -this.screenRect.top);

		return out;
	}
	
	public void DrawLayer1(){
		//AllBitmap1.eraseColor(this.BackColor);
		
		//log.("Pixelon map xx", "I'm drawing" );
		
		if (AllCanvas == null ) return;
		
		if (DrawBackGround){
			AllCanvas.drawColor(this.BackColor);
		}else{
			AllCanvas.drawColor(0);
		}
		
		for (joShape x : this.content) {
			//if (!x.inEditMode)
			if (x.SHAPE_TYPE == Flags.SHAPE_RELATIVE_RECTANGLE){
				joRelativeToMapRectangle y = (joRelativeToMapRectangle)x;
				Point pixelsPoint1 = new Point();
				Point pixelsPoint2 = new Point();
				
				pixelsPoint1 =  toPixels(y.GeoPoints[0],pixelsPoint1);
			 	pixelsPoint2 =  toPixels(y.GeoPoints[2], pixelsPoint2);
			 	
			 	y.x1 = transformX(pixelsPoint1.x);
				y.y1 = transformY(pixelsPoint1.y);
				
				
				
				y.x2 = transformX(pixelsPoint2.x) ;
				y.y2 = transformY(pixelsPoint2.y) ;
				y.LineColor = Color.BLUE;
				y.Draw(AllCanvas, paint);
			}
			else {
				if (DrawObjects == true) x.Draw(AllCanvas, paint);
			}
		}
		
		
	
		
		
		
		
		
		if (AnnotationPtr !=null && MainContainer.ModeOfUse == 1 && DrawButtons)
			GetButtonByID(TAG_BUTTON_ID).visible = true;
		else
			GetButtonByID(TAG_BUTTON_ID).visible = false;
			
				
			
		//	((joTextAnnotation) AnnotationPtr).screenRect = screenRect;
		//	AnnotationPtr.Draw(AllCanvas, paint);
		
		
		
		for (joButton x : this.buttons) {
			x.screenRect = screenRect;
			//if (!x.inEditMode)
			if (DrawButtons) x.Draw(AllCanvas, paint);
		}
		
		
	}
	public void DrawLayer2(){
		if (!DrawSecondLayer) return;
		AllBitmap2.eraseColor(0x00000000);
		//AllCanvas2.drawColor(0x00000000);
		//AllCanvas2.
		
		for (joShape x : this.tempLayer) {
			//if (!x.inEditMode)
			if (DrawObjects)
				if (x.SHAPE_TYPE == Flags.SHAPE_RECTANGLE_FOR_SELECT){
					joRectangle h = ((joRectangle)x);
					
					
					for(joRelativebuttonWithIcon x2: h.RelativeButtons){
						x2.screenRect = screenRect;
					}
					
					//if (h.hCam !=null) h.hCam.screenRect = screenRect;
					//if (h.hClose !=null) h.hClose.screenRect = screenRect;
					x.Draw(AllCanvas2, paint);
				}
				else
					x.Draw(AllCanvas2, paint);
		}
	}
	
	public double Distance (float x1,float y1,float x2,float y2){
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		
	}
	
	public void MoveObjectToEditMode(joShape obj){
		obj.inEditMode = true;
		InEditMode = true;
		
		joRectangle rec = new joRectangle(Color.BLUE);
		float pts [] = obj.GetMinBoundBox();
		rec.x1 = pts[0];
		rec.y1 = pts[1];
		rec.x2 = pts[2];
		rec.y2 = pts[3];
		rec.ParentHandle = obj;
		rec.scale = obj.scale;
		rec.translateX = obj.translateX;
		rec.translateY = obj.translateY;
		rec.Angle 	   = obj.Angle; 
		rec.fatherContext = FatherContext;
		rec.CreateUpdateButtons();
		tempLayer.add(rec);
		
		AnnotationPtr = new joTextAnnotation((int)width,(int)height,obj);
		GetButtonByID(TAG_BUTTON_ID).LinkedObjectForName = obj;
		GetButtonByID(TAG_BUTTON_ID).Text = obj.GetAnnotationString();
		
	}
	public void DeleteObject(joShape obj){
		if (obj.inEditMode)	{
			for (joShape x : tempLayer){
				if (x.ParentHandle == obj){
					tempLayer.remove(x);
					break;
				}
			}
		}
		this.content.remove(obj);
		if (tempLayer.size()==0){
			InEditMode = false;
			AnnotationPtr = null;
		}else{
		
			AnnotationPtr = new joTextAnnotation((int)width,
												 (int)height,
												  tempLayer.get(tempLayer.size()-1).ParentHandle);
			GetButtonByID(TAG_BUTTON_ID).Text = tempLayer.get(tempLayer.size()-1).ParentHandle.GetAnnotationString();
			GetButtonByID(TAG_BUTTON_ID).LinkedObjectForName= tempLayer.get(tempLayer.size()-1);
			
		}
	}
	public void ReleaseAllObjectsFromEditMode(){
		for (joShape x : this.content){
			x.inEditMode = false;
		}
		tempLayer.clear();
	}
	public void ReleaseObjectFromEditMode(joShape obj){
		// null object means to pop the last selected object
		if (tempLayer.size()!=0)
		if (obj == null){
			tempLayer.get(tempLayer.size()-1).ParentHandle.inEditMode = false;
			tempLayer.remove(tempLayer.size()-1);
		}
		else{ 
			obj.inEditMode = false;
			
			for (joShape x : tempLayer){
				if (x.ParentHandle == obj){
					tempLayer.remove(x);
					break;
				}
			}
		}
		if (tempLayer.size()==0){
			InEditMode = false;
			AnnotationPtr = null;
		}else{
		
			AnnotationPtr = new joTextAnnotation((int)width,
												 (int)height,
												  tempLayer.get(tempLayer.size()-1).ParentHandle);
			GetButtonByID(TAG_BUTTON_ID).Text = tempLayer.get(tempLayer.size()-1).ParentHandle.GetAnnotationString();
			GetButtonByID(TAG_BUTTON_ID).LinkedObjectForName= tempLayer.get(tempLayer.size()-1);
			
		}
	}

	public joShape GetObjectOnPos (float x, float y){//TODO: need to find better algorithm	
		float TempWidth =0.0f;
		float TempScale =1.0f;
	
		//AllCanvas2.setMatrix(new Matrix());
		
		
		
		for (int i = content.size()-1 ; i >=0; i-- ){
			AllBitmap2.eraseColor(0x00000000);//transparent
			
			//temp width to increase the width for small objects because they are hard to select
			TempWidth = content.get(i).StrokeWidth;
			TempScale = content.get(i).scale;
			if (TempWidth <MIN_WIDTH_SELECT ||
				TempScale <MIN_SCALE_SELECT
					) content.get(i).StrokeWidth = TempWidth + WIDTH_SELECT_SHIFT;
			content.get(i).Draw(AllCanvas2, paint);
			content.get(i).StrokeWidth = TempWidth;
			
			
			//log.("try get object", String.valueOf(i));
			if (AllBitmap2.getPixel((int)x , (int)y ) !=0 ){
				return content.get(i);
			}
		}
		
		return null;
	}
	
	public joShape GetObjectOnPosWithScale (float x, float y,Rect ViewRect ,float MapRight,float MapLeft,float MapTop,float MapBottom){//TODO: need to find better algorithm	
		float TempWidth =0.0f;
		float TempScale =1.0f;
	
		AllCanvas2.setMatrix(new Matrix());
		
		AllCanvas2.translate(-1* ViewRect.left , -1* ViewRect.top);
		 
		AllCanvas2.scale( 
				(MapRight - MapLeft) / (ViewRect.right - ViewRect.left)
				
				
				
				,
				
				(MapBottom - MapTop) / (ViewRect.bottom - ViewRect.top)
				);
		
		for (int i = content.size()-1 ; i >=0; i-- ){
			AllBitmap2.eraseColor(0x00000000);//transparent
			
			//temp width to increase the width for small objects because they are hard to select
			TempWidth = content.get(i).StrokeWidth;
			TempScale = content.get(i).scale;
			if (TempWidth <MIN_WIDTH_SELECT ||
				TempScale <MIN_SCALE_SELECT
					) content.get(i).StrokeWidth = TempWidth + WIDTH_SELECT_SHIFT;
			
			//temp else needs to be fixed
			else content.get(i).StrokeWidth = TempWidth + WIDTH_SELECT_SHIFT;
			
			
			content.get(i).Draw(AllCanvas2, paint);
			content.get(i).StrokeWidth = TempWidth;
			
			
			//log.("try get object", String.valueOf(i));
			if (AllBitmap2.getPixel((int)x , (int)y ) !=0 ){
				return content.get(i);
			}
		}
		
		return null;
	}
	
	public joButton GetbuttonIsClickedwithScale (float x, float y,Rect ViewRect ,float MapRight,float MapLeft,float MapTop,float MapBottom){//TODO: need to find better algorithm	
		float TempWidth =0.0f;
		float TempScale =1.0f;
	
		AllCanvas2.setMatrix(new Matrix());
		
		AllCanvas2.translate(-1* ViewRect.left , -1* ViewRect.top);
		 
		AllCanvas2.scale( 
				(MapRight - MapLeft) / (ViewRect.right - ViewRect.left)
				
				
				
				,
				
				(MapBottom - MapTop) / (ViewRect.bottom - ViewRect.top)
				);
		
		for (int i = buttons.size()-1 ; i >=0; i-- ){
			AllBitmap2.eraseColor(0x00000000);//transparent
			
			
			
			
			buttons.get(i).Draw(AllCanvas2, paint);
			
		
			////log.("try get object", String.valueOf(i));
			if (AllBitmap2.getPixel((int)x , (int)y ) !=0 ){
				return buttons.get(i);
			}
		}
		
		return null;
	}
	public joRelativebuttonWithIcon GetbuttontoRectIsClickedwithScale (float x, float y,Rect ViewRect ,float MapRight,float MapLeft,float MapTop,float MapBottom){//TODO: need to find better algorithm	
		//float TempWidth =0.0f;
		//float TempScale =1.0f;
	
		AllCanvas2.setMatrix(new Matrix());
		
		AllCanvas2.translate(-1* ViewRect.left , -1* ViewRect.top);
		 
		AllCanvas2.scale( 
				(MapRight - MapLeft) / (ViewRect.right - ViewRect.left)
				
				
				
				,
				
				(MapBottom - MapTop) / (ViewRect.bottom - ViewRect.top)
				);
		
		//boolean IsClose=false;
		//boolean isCam = false;
		//int minus =1;
		for (int i = tempLayer.size()-1 ; i >=0; i-- ){
			
			
			
			if (tempLayer.get(i).SHAPE_TYPE == Flags.SHAPE_RECTANGLE_FOR_SELECT){
				joRectangle xRec = (joRectangle) tempLayer.get(i);
				
				for (joRelativebuttonWithIcon x2: xRec.RelativeButtons){
				
					AllBitmap2.eraseColor(0x00000000);//transparent
					x2.Draw(AllCanvas2, paint);
					if (AllBitmap2.getPixel((int)x , (int)y ) !=0 )
						return x2;
				}
			
			}
			//.Draw(AllCanvas2, paint)
			
		
			////log.("try get object", String.valueOf(i));
			
		}
		
		return null;
	}
	
	public void SetBackColor(int _Color){
		BackColor = _Color;//.argb(255, Color.red(_Color), Color.green(_Color), Color.blue(_Color));   
	}
	public int GetBackColor(){
		return BackColor;
	}
	
	public void RemoveSelectedObjects(){
		List<joShape> tmp = new ArrayList<joShape>();
		for (joShape x : content) {
			if (x.inEditMode){
				ReleaseObjectFromEditMode(x);
				tmp.add(x);
			}
			
		}
		for(joShape x : tmp){
			content.remove(x);
			RedoContent.add(x);
		}

	}
}
