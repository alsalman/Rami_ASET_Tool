package jo.edu.just;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import microsoft.mappoint.TileSystem;
import org.osmdroid.ResourceProxy;

import org.osmdroid.ResourceProxy;
import org.osmdroid.contributor.OSMUploader;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

import jo.edu.just.Helpers.*;
import jo.edu.just.Shapes.joButton;
import jo.edu.just.Shapes.joPath;
import jo.edu.just.Shapes.joRelativeToMapRectangle;
import jo.edu.just.Shapes.joRelativebuttonWithIcon;
import jo.edu.just.Shapes.joShape;
import jo.edu.just.XML.writer;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.method.ScrollingMovementMethod;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenu.SlideMenuItem;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;






public class MainContainer extends Activity implements OnSlideMenuItemClickListener  {
    /** Called when the activity is first created. */
	DrawOverMap dv;
	Boolean CanDrawOverMap = true;
	SlideMenu slidemenu;
	boolean ReadyToExit = false;
	//- temp map variables
	private MapView mOsmv;
    private TilesOverlay mTilesOverlay;
    private MapTileProviderBasic mProvider;
    private ResourceProxy mResourceProxy;
    
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
	//-
	private MainContainer MeHandle = null;
	public static  int ModeOfUse = 0;
	public static  int ModeOfServerUse = 0;
	//public static boolean CanDrawPathsOnMap = true;
	//public static boolean CansendTouchesToDrawView = true;
	
	public String LastAnnotaionData = "";
	public int    AnnotationUsedType = 0;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MeHandle = this;
        sharedSettings.FatherContext = this.getApplicationContext();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        Bundle extras = getIntent().getExtras();
        
        ModeOfUse = extras.getInt("mode");
        ModeOfServerUse = extras.getInt("ModeOfServerUse");
       
       // final RelativeLayout rl = new RelativeLayout(this);
		//dv = new DrawView(this);
        dv = new DrawOverMap(this);
        
        this.mOsmv = new MapView(this, 256);
        sharedSettings.Mosmv = this.mOsmv;
		dv.AssignMap(this.mOsmv);
		dv.AssignProjector(mOsmv.getProjection());
		setContentView(R.layout.main);
        //setContentView(dv);
		//mResourceProxy = new ResourceProxyImpl(getApplicationContext());
		slidemenu = (SlideMenu) findViewById(R.id.slideMenu);
		slidemenu.init(this, R.menu.slide, this, 300);
	   	slidemenu.setHeaderImage(getResources().getDrawable(R.drawable.paint_icon));
		
		
        LinearLayout ContainerLayout = (LinearLayout) findViewById(R.id.button1);
        //ContainerLayout.addView(dv);
        
       // CloudmadeUtil.retrieveCloudmadeKey(getApplicationContext());

       
        
        
        ContainerLayout.addView(this.mOsmv, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
        this.mOsmv.setBuiltInZoomControls(true);

        // zoom to the netherlands
        this.mOsmv.getController().setZoom(11);
        this.mOsmv.getController().setCenter(new GeoPoint(53.106709,8.852091));
        this.mOsmv.setMultiTouchControls(false);
        //this.mOsmv.getController().setCenter(new GeoPoint(984800.423065903 ,7003097.24473157/1000000));
        
        // Add tiles layer
        mProvider = new MapTileProviderBasic(getApplicationContext());
        mProvider.setTileSource(TileSourceFactory.MAPNIK);
        this.mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
        this.mOsmv.getOverlays().add(this.mTilesOverlay);
        mOsmv.setClickable(true);
        
        this.mOsmv.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//dv.onTouch(event);
				return false;
			}
		});
        this.mOsmv.setMapListener(// new DelayedMapListener(

        		new MapListener() {
					
					@Override
					public boolean onZoom(ZoomEvent arg0) {
						// TODO Auto-generated method stub
						for(joShape x : dv.dynamicBitmap.content){
							((joPath)x).RefreshPixelsFromGeo(mOsmv.getProjection());
							((joPath)x).UpdatePath(true);
						}
						mOsmv.invalidate();
						Log.d("zoom event", arg0.toString());
						return false;
					}
					
					@Override
					public boolean onScroll(ScrollEvent arg0) {
						// TODO Auto-generated method stub
						return true;
					}
				}
        		//, (long)1000)
        	);
        // this.setContentView(rl);
    //    this.mOsmv.getController().setZoom(13);
        
        MapOverlay movl = new MapOverlay(this);
        mOsmv.getOverlays().add(movl);
        
        
        //set the current location
        //locationListener = new MyLocationListener();        
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if( location != null ) {
        	 this.mOsmv.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
        //---------------
        
      
        dv.addEventListener(new FinishDrawEventClassListener() {
			
			@Override
			public void handleFinishDrawClassEvent(EventObject e) {
				// TODO Auto-generated method stub
				/*
				Intent i2 = new Intent("jo.edu.just.TextNotationEx");
				Bundle extras2 = new Bundle();
				extras2.putString("annotation",dv.GetObjectAnnotation());
				i2.putExtras(extras2);
				startActivityForResult(i2,3); */
			}
		});
        //this.setContentView(rl);
        
       
        //sharedSettings.FatherContext = this.getApplicationContext();
        
        joRelativebuttonWithIcon.addEventListener( new ButtonClickEventClassListener() {
			
			@Override
			public void handleButtonClickClassEvent(boolean IsHover, boolean isUp,
					boolean isClick, EventObject e) {
					joRelativebuttonWithIcon thisTmp = (joRelativebuttonWithIcon )e.getSource();
					if (IsHover){
						thisTmp.SetHover(true);
						mOsmv.invalidate();
					}
	   			
					if ((isUp && thisTmp.GetHoverState()) || isClick ){
						thisTmp.SetHover(false);
						
						if (thisTmp.TypeCloseOrCam == Flags.SHAPE_BUTTON_FOR_RECTANGLE_SELECT_CLOSE){
							dv.dynamicBitmap.DeleteObject(thisTmp.ParentRectangle.ParentHandle);
						}
						
						
						
					}
				
				
			}
		});
        
        
       dv.AddButton(1,"Send Query", 0.71f, 0.015f, 0.98f, 0.09f).addEventListener(new ButtonClickEventClassListener() {
   		
   		@Override
   		public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
   			joButton thisTmp = (joButton )e.getSource();
			
   			if (isHover){
   				thisTmp.SetHover(true);
   				mOsmv.invalidate();
   			}
   			
   			if ((isUp && thisTmp.GetHoverState()) || isClick ){
   				thisTmp.SetHover(false);
   		
   			
   			
   			Toast.makeText(getApplicationContext() ,"Please Wait....", Toast.LENGTH_SHORT).show();
   			
   		//	joButton thisTmp = (joButton )e.getSource();
   			
   			thisTmp.color=  Color.argb(200, 255,90, 20);
   			mOsmv.invalidate();
   			
   			if (AnnotationUsedType == Flags.ANNOTATE_BY_WIZARD){
   				
   				//String  res = (new writer().SendGeneralRequest(LastAnnotaionData) );
   				//Log.d ("server response to wizard", res);
				
   				//showAlertDialog(res);
   				dv.dynamicBitmap.ReleaseObjectFromEditMode(null);
   				SendQueryRequest(true,LastAnnotaionData);
   				
   			}else
   				SendQueryRequest(false,"");
   			thisTmp.color=  Color.argb(200, 255, 0, 0);
   			mOsmv.invalidate();
   		}
   		}
   	}) ; 
       dv.AddButton(1000," Tag: is non", 0.02f, 0.015f, 0.700f, 0.09f).addEventListener(new ButtonClickEventClassListener() {
		
		@Override
		public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
			// TODO Auto-generated method stub
			joButton thisTmp = (joButton )e.getSource();
			if (isHover){
   				thisTmp.SetHover(true);
   				mOsmv.invalidate();
   			}
   			
   			if ((isUp && thisTmp.GetHoverState()) || isClick ){
   				thisTmp.SetHover(false);
   		
			
			
			if (ModeOfUse == 1)
				ShowAnnotationDialog(0);
			if (ModeOfUse == 2)
				ShowAnnotationDialog(1);
			if (ModeOfUse ==2)
				ShowAnnotationDialog(2);
			
			if (ModeOfUse ==4)
				ShowAnnotationDialog(2);
   			}
		}
	}) ;
       
       
       if (ModeOfUse == 1){
    	   //CanDrawPathsOnMap = true;
    	   
    	   
    	   mOsmv.setClickable(true);
		   mOsmv.setMultiTouchControls(false);
       }else if (ModeOfUse ==2 || ModeOfUse == 3 || ModeOfUse ==4 ){
    	   dv.SetBackGroundColor_ColdSet(Color.argb(0, 0, 0, 0));
    	   SetToMapViewOnly(true);
    	   //CanDrawOverMap = true;
    	   //CanDrawPathsOnMap = false;
    	   //mOsmv.setClickable(false);
		   //mOsmv.setMultiTouchControls(true);
		   if (ModeOfUse == 2 ) dv.AddButton(2," Register the Query by Voice", 0.02f, 0.015f, 0.700f, 0.09f).addEventListener(new ButtonClickEventClassListener() {
				
				@Override
				public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
					try{
					joButton thisTmp = (joButton )e.getSource();
					
						if (isHover){
							
							thisTmp.SetHover(true);
							return;
						}
					if ( (isUp && thisTmp.GetHoverState() )|| isClick  ){
					//ShowAnnotationDialog(1);
					 PackageManager pm = MeHandle.getPackageManager();
				     List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
				     if (activities.size() == 0)
				     {
				    	 Toast.makeText(MeHandle.getApplicationContext(),"Voice recognition Engine is not present currently	",Toast.LENGTH_SHORT).show();
				    	 
				    	 //speak_b.setEnabled(false);
				         //speak_b.setText("Recognizer not present");
				     }else{
				    	 
				    	 
				    	 
				    	 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Build your query...");
					        startActivityForResult(intent, 55);//55 for the google voice recognition
				     }
				     
				     
				     
					
				}
				}catch(Exception ex){
					Toast.makeText(MeHandle.getApplicationContext(),"Voice recognition Engine is not present currently	",Toast.LENGTH_SHORT).show(); 
				}
				}
			}) ;
		   dv.AddButton(3," □ Check to Mark a region ", 0.02f, 0.094f, 0.98f, 0.169f).addEventListener(new ButtonClickEventClassListener() {
				
				@Override
				public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
					joButton thisTmp = (joButton )e.getSource();
					
					if (isHover){
		   				thisTmp.SetHover(true);
		   				mOsmv.invalidate();
		   				return;
		   			}
		   			
		   			if ((isUp && thisTmp.GetHoverState()) || isClick ){
		   				thisTmp.SetHover(false);
					
					
					if (thisTmp.checked   ){
						thisTmp.SetChecked(false);
						dv.dynamicBitmap.ReleaseAllObjectsFromEditMode();
						thisTmp.Text = " □ Check to Mark a region ";
						thisTmp.color=  Color.argb(200, 255, 0, 0);
						//CanDrawPathsOnMap = false;
						//dv.dynamicBitmap.tempLayer.clear();
						//dv.dynamicBitmap.content.clear();
						dv.dynamicBitmap.DrawObjects = true;
						dv.dynamicBitmap.CanSelectObjects = false;
						dv.dynamicBitmap.CanBuildNewObjects = false;
						
						mOsmv.setClickable(false);
						mOsmv.setMultiTouchControls(true);
						  
					}else{
						thisTmp.SetChecked(true) ;
						thisTmp.Text = " ■ Check to Mark a region ";
						thisTmp.color=  Color.argb(200, 255,90, 20);
						//CanDrawPathsOnMap = true;
						dv.dynamicBitmap.DrawSecondLayer = true;
						dv.dynamicBitmap.DrawObjects = true;
						dv.dynamicBitmap.CanSelectObjects = true;
						dv.dynamicBitmap.CanBuildNewObjects = true;
						
						
						//dv.dynamicBitmap.tempLayer.clear();
						//dv.dynamicBitmap.content.clear();
						mOsmv.setClickable(true);
						mOsmv.setMultiTouchControls(false);
						
					}
				}
					mOsmv.invalidate();
					
				}
			}) ;
		   if (ModeOfUse == 3 ) dv.AddButton(2," Register the Query", 0.02f, 0.015f, 0.700f, 0.09f).addEventListener(new ButtonClickEventClassListener() {
				
				@Override
				public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
					joButton thisTmp = (joButton )e.getSource();
					if (isHover){
						
						thisTmp.SetHover(true);
						return;
					}
						if ( (isUp && thisTmp.GetHoverState() )|| isClick  ){
					
						ShowAnnotationDialog(3);
					}
				}
			}) ;
   		   if (ModeOfUse == 4 ) dv.AddButton(2," Register the Query using Text", 0.02f, 0.015f, 0.700f, 0.09f).addEventListener(new ButtonClickEventClassListener() {
				
				@Override
				public void handleButtonClickClassEvent(boolean isHover , boolean isUp,boolean isClick,EventObject e) {
					ShowAnnotationDialog(2);
				}
			}) ;
		      
   		   
   		   
   		   
   		   
   		   
   		   
       }
        
	}
	
	public void AddPolygonOnMapFromServerCommand(String response){
		String Points  = response.substring(9,response.length() -3);
		String delims = "[,]+";
		String delims2 = "[ ]+";
		String[] GeoPoints = Points.split(delims);	

		
		
		
		//PathOverlay myOverlay= new PathOverlay(Color.RED, this);
		//myOverlay.getPaint().setStyle(Paint.Style.STROKE);
		//myOverlay.getPaint().setStrokeWidth(7);
		
		 //List<GeoPoint>  pts = new ArrayList<GeoPoint>(); 
		
		GeoPoint pts[] = new GeoPoint[5];
		
		 for (int i =0 ; i <5;i++){ //take the points of the polygon
			 
			 //Log.d("pos",GeoPoints[i]);
			 String [] xANDy = GeoPoints[i].split(delims2);
			 
			 double [] lonLat = ConvertFrom900913to4326(Double.valueOf(xANDy[0]), Double.valueOf(xANDy[1]));
			 pts[i] = new GeoPoint(lonLat[1],lonLat[0]);
			 
			 // pts.add(new GeoPoint(lonLat[1],lonLat[0]));
			// pts[i] = new GeoPoint(lonLat[1],lonLat[0]);
			// myOverlay.addPoint(pts.get(i));
			 
		 }
		 
		 
		 TriangleOverlay mOverlay = new TriangleOverlay(this,pts);
			
		// joRelativeToMapRectangle r = new joRelativeToMapRectangle(pts);
		 //						  r.SHAPE_TYPE = Flags.SHAPE_RELATIVE_RECTANGLE;
		 
		//dv.dynamicBitmap.content.add(r); 
		 
		//log.d("polygon adding now", "Polygon Added" );
		
		
		
		//mOsmv.invalidate();
		
		mOsmv.getOverlays().add(mOverlay);
		
		
		
// this.mOsmv.getController().setCenter(new GeoPoint(
//				 
//				 
//				// pts[1].getLongitudeE6() +    
//				 (	 pts[2].getLatitudeE6() + 	 pts[1].getLatitudeE6())/2.0
//				 
//				 
//				 
//				 ,
//				 
//				// pts[2].getLatitudeE6() + 
//				 	(pts[3].getLongitudeE6() + pts[2].getLongitudeE6())/2.0
//				 
//				 
//				 
//				 
//				 ));
		
 mOsmv.invalidate();
	}
	public void SendQueryRequest( boolean InvokeAsWizardAndVoice,String Command){
		
		
		
		
		
		
		ServerSendQuery taskOne = new ServerSendQuery();
		ServerQueryWithMode mode = new ServerQueryWithMode();
		mode.dv = this.dv;
		mode.QueryText = Command;
		
		
		mode.ModeUsed = Flags.QHTC_HASH_REQUEST_MODE;
		if (InvokeAsWizardAndVoice ) 
			mode.ModeUsed = Flags.QUERY_ASK_WIZARD_TYPE;
		
		
		taskOne.execute(mode);
		
		//mOsmv.setClickable(false);
		//MeHandle.mOsmv.setMultiTouchControls(true);
		//if (ModeOfServerUse == Flags.SERVER_MODE_SQL) CanDrawOverMap =  false;
		//else if (ModeOfServerUse == Flags.SERVER_MODE_QHTC){
			//CansendTouchesToDrawView =false;
			//CanDrawOverMap = true;
			//dv.dynamicBitmap.DrawObjects =true;
			//dv.dynamicBitmap.DrawBackGround = false;
		//}
		
	
	}
	
	
	
	public void showAlertDialog(String text){
		AlertDialog.Builder popupBuilderHTTP = new AlertDialog.Builder(this);
		
		
		
		
		TextView myMsgHTTP = new TextView(this);
		myMsgHTTP.setMovementMethod(new ScrollingMovementMethod());
		myMsgHTTP.setTextColor(myMsgHTTP.getTextColors().getDefaultColor());
		myMsgHTTP.setText(text);
		myMsgHTTP.setGravity(Gravity.LEFT);
		popupBuilderHTTP.setView(myMsgHTTP);
		popupBuilderHTTP.show();
		
	}
	
	public double [] ConvertFrom900913to4326(double x,double y ){
		double [] lanLat= new double[2];
		//lon is zero
		lanLat [0] =  x *180 / 20037508.34;
		//  lanLat [0] =  x / 6378137.0;
		//lat 
		lanLat[1] = y * 180 / 20037508.34;
		lanLat[1] = 180 / Math.PI * (2 * Math.atan( Math.exp( lanLat[1] * Math.PI / 180.0)) - Math.PI / 2.0);   		
		//lanLat[1] = Math.tanh( Math.pow(10, y * (Math.PI/180) ) / (Math.PI/360)      ) -90;
		//lanLat[1]  =  Math.toDegrees(2* Math.atan(Math.exp(Math.toRadians(y))) - Math.PI/2);
		return lanLat;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
		//clicking any button will interrupt the second back click for exit
		
		//log.d("hhr key",String.valueOf(keyCode));
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK : return super.onKeyUp(keyCode, event);
		case KeyEvent.KEYCODE_MENU :
        {
        	if (slidemenu.menuIsShown) 
        		slidemenu.hide();
        	else
        		slidemenu.show();
            //your Action code
        	dv.PrepareForDirtyTouch();
        	ReadyToExit = false;
            return true;
        }
    }
	dv.PrepareForDirtyTouch();
	ReadyToExit = false;
    return super.onKeyUp(keyCode, event);
		
	}
	@Override
	public void onBackPressed(){
		//if (dv.BackPressed()) finish();
		if (slidemenu.menuIsShown) {
			slidemenu.hide();
		}else
			//MainContainer.ModeOfUse = 0;
    		//MainContainer.ModeOfServerUse = 0;
    		//MainContainer.CanDrawPathsOnMap = true;
    		//MainContainer.CansendTouchesToDrawView = true;
    		//CanDrawOverMap = true;
			HandleSendQueryErrorToGoBackState();
			
			//mOsmv.setMapOrientation(degrees)
			
			
			if(dv.BackPressed()) {
				
				
				
				//CanDrawOverMap = true;
				mOsmv.setClickable(true);
				mOsmv.getOverlays().clear();
				MapOverlay movl = new MapOverlay(this);
		        mOsmv.getOverlays().add(movl);
		        mOsmv.setBuiltInZoomControls(false);
		        this.mOsmv.setMultiTouchControls(false);
				this.dv.dynamicBitmap.DrawObjects = true;
				
				finish();
				
//				if (ReadyToExit)
//					if (dv.IsThereDirtyTouch()){
//						Toast.makeText(this, "click Back again to exit", Toast.LENGTH_SHORT).show();
//						dv.PrepareForDirtyTouch();
//					}
//					else
//						finish();
//				else{
//					Toast.makeText(this, "click Back again to exit", Toast.LENGTH_SHORT).show();
//					ReadyToExit= true;
//					dv.PrepareForDirtyTouch();
//				}
			}
	}
	
	
	public void ShowAnnotationDialog(int mode){
		Intent i2 = new Intent("jo.edu.just.TextNotationEx");
		Bundle extras2 = new Bundle();
		extras2.putString("annotation",dv.GetObjectAnnotation());
		extras2.putInt("specialTab", mode);
		i2.putExtras(extras2);
		startActivityForResult(i2,3);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
		
			if (requestCode ==55){
				try{
				  ArrayList<String> matches = data.getStringArrayListExtra(
		                    RecognizerIntent.EXTRA_RESULTS);
				  
				  if (matches.size() > 0 ){
					  LastAnnotaionData = matches.get(0);
					  AnnotationUsedType = Flags.ANNOTATE_BY_VOICE;
					  Toast.makeText(this,matches.get(0), Toast.LENGTH_SHORT).show();
				  }
				 }catch(Exception e){
					 
				 }
				
				  
			// need to send the query text	
				
			}
		
		    if (requestCode == 1) //width window callback
		    	if (resultCode == RESULT_OK) 
		    		dv.SetStrokeWidth(Float.valueOf(data.getData().toString()) );
		    
		    if (requestCode == 2) //width window callback
			    if (resultCode == RESULT_OK) 
			    	dv.SetSelectColor(Integer.valueOf(data.getData().toString()) );
		 
		    if (requestCode == 3) //annotation window callback
			    if (resultCode == RESULT_OK) {
			    	String res [] = data.getData().toString().split("[|]+"); 	
			    	if(res[1].compareTo("wizard") ==0 ){
			    		LastAnnotaionData = res[0];
			    		AnnotationUsedType = Flags.ANNOTATE_BY_WIZARD;
			    		
			    		
			    	} else if (res[1].compareTo("text") ==0)
			    	{
			    		dv.SetObjectAnnotation(data.getData().toString());
			    	}
			    	else if (res[1].compareTo("pre_def") ==0)
			    	{
			    		dv.SetObjectAnnotation(data.getData().toString());
			    	}else if (res[1].compareTo("pre_def") ==0){
			    		dv.SetObjectAnnotation(data.getData().toString());
			    	}//else |none means the annotation canceled
			    		
			    	
			    	
			    	
			    	//dv.SetObjectAnnotation(data.getData().toString());
			    	////log.d("wizard value",data.getData().toString());
			    }
			    	
		    if (requestCode == 4) //color window callback
			    if (resultCode == RESULT_OK) 
			    	dv.SetBackGroundColor(Integer.valueOf(data.getData().toString()) );
	    }
	@Override
	public void onSlideMenuItemClick(int itemId) {
		slidemenu.hide();
		switch(itemId) {
		case R.id.item_one:
			dv.UndoClick();
			break;
		case R.id.item_two:
			dv.RedoClick();
			break;
		case R.id.item_three:
			dv.DeleteClick();
			break;
		case R.id.item_four:
			dv.UndoSelectClick();
			break;
		case R.id.item_five:
			Intent i = new Intent("jo.edu.just.WidthSelect");
			//Intent i = new Intent("jo.edu.just.Helpers.ColorDialog");
			Bundle extras = new Bundle();
			extras.putFloat("width", dv.GetStrokeWidth()/3.0f -1);
			i.putExtras(extras);
			startActivityForResult(i,1);
			break;
		case R.id.item_six:
			Intent i1 = new Intent("jo.edu.just.Helpers.ColorDialog");
			Bundle extras1 = new Bundle();
			extras1.putInt("color", dv.GetSelectColor());
			i1.putExtras(extras1);
			startActivityForResult(i1,2);
			break;
		case R.id.item_seven:
			ShowAnnotationDialog(0);
			break;
		case R.id.item_ground:
			Intent i3 = new Intent("jo.edu.just.Helpers.ColorDialog");
			Bundle extras3 = new Bundle();
			extras3.putInt("color", dv.GetBackGroundColor());
			i3.putExtras(extras3);
			startActivityForResult(i3,4);
			break;
		case R.id.item_export_with_stamps:
		case R.id.item_export_without_stamps:
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
			TextView myMsg = new TextView(this);
					 myMsg.setMovementMethod(new ScrollingMovementMethod());
					 myMsg.setTextColor(myMsg.getTextColors().getDefaultColor());
					 
			myMsg.setText((new jo.edu.just.XML.writer()).GenerateXML(dv.dynamicBitmap, itemId == R.id.item_export_with_stamps)
					);
			myMsg.setGravity(Gravity.LEFT);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
			break;
		case R.id.item_about:
			AboutDialog abt = new AboutDialog(this);
			abt.setTitle("About ASET");
			abt.show();
			break;
		case R.id.item_export_for_http_get:
			//AlertDialog.Builder popupBuilderHTTP = new AlertDialog.Builder(this);
			//TextView myMsgHTTP = new TextView(this);
				//myMsgHTTP.setMovementMethod(new ScrollingMovementMethod());
				//myMsgHTTP.setTextColor(myMsgHTTP.getTextColors().getDefaultColor());
					 
				//myMsgHTTP.setText((new jo.edu.just.XML.writer()).SendDataToServer(dv.dynamicBitmap));
				//myMsgHTTP.setGravity(Gravity.LEFT);
				//popupBuilderHTTP.setView(myMsgHTTP);
				//popupBuilderHTTP.show();
				//SendQueryRequest(false);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			slidemenu.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	
	
	
	
	public class MapOverlay extends org.osmdroid.views.overlay.Overlay {

        public MapOverlay(Context ctx) {super(ctx);}

    

        @Override
        public boolean onTouchEvent(MotionEvent e, MapView mapView) {
           // if(CanDrawOverMap && CansendTouchesToDrawView ){
            	dv.onTouch(e);
           // }
        	
            return false;
        }

		@SuppressLint("WrongCall")
		@Override
		protected void draw(Canvas pC, MapView arg1, boolean shadow) {
			 if(shadow )
		            return;
			 
			 if (ModeOfUse == 2 || ModeOfUse ==3 || ModeOfUse == 4)
				 dv.dynamicBitmap.GetButtonByID(1).visible =true;
			 
			 else if (ModeOfUse ==1){
				 if ( dv.dynamicBitmap.content.size() > 1){
					 dv.dynamicBitmap.GetButtonByID(1).visible =true;
				 }else
					 dv.dynamicBitmap.GetButtonByID(1).visible =false;
			 }
				 
			 
			 
			 final Projection projection = mOsmv.getProjection();
			 
			 dv.AssignProjector(projection);
			 dv.dynamicBitmap.projUnit = projection;
			 dv.dynamicBitmap.screenRect = projection.getScreenRect();
			 dv.dynamicBitmap.mOsmMap = mOsmv;
			 dv.AssignrectView(projection.getScreenRect());
			 dv.onDraw(pC);
		}
	}
	
	
	
	//- temp for the maps 
	public class TriangleOverlay extends org.osmdroid.views.overlay.Overlay {

		
		
		public GeoPoint pts [] ;
		
		private Point PointOne = new Point() ;
		private Point PointTwo = new Point(); 
		protected final Paint mPaint = new Paint();
		DashPathEffect effectDash = new DashPathEffect(new float[] {10, 5, 5, 5},3);
		
        public TriangleOverlay(Context ctx,GeoPoint _pts []) {
        	super(ctx);
        	 this.mPaint.setStrokeWidth(5);
             this.mPaint.setColor(Color.BLUE);
             this.mPaint.setAntiAlias(true);	
             this.mPaint.setStyle(Style.STROKE);
             this.mPaint.setPathEffect(effectDash);
             
             this.pts = _pts;
        }

    

        @Override
        public boolean onTouchEvent(MotionEvent e, MapView mapView) {
            return false;
        }

		@SuppressLint("WrongCall")
		@Override
		protected void draw(Canvas pC, MapView arg1, boolean shadow) {
			 if(shadow   )
		            return;
			 final Projection pj = arg1.getProjection();
             pj.toMapPixels(pts[1], PointOne);
             pj.toMapPixels(pts[3], PointTwo);

            pC.drawColor(Color.argb(0, 0, 0, 200));
            
            this.mPaint.setColor(Color.argb(150, 0, 0, 200));
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            this.mPaint.setStyle(Style.FILL);
            pC.drawRect(PointOne.x, PointOne.y,PointTwo.x,PointTwo.y,mPaint);
            this.mPaint.setColor(Color.BLUE);
            mPaint.setXfermode(null);
            this.mPaint.setStyle(Style.STROKE);
            pC.drawRoundRect( new RectF(  PointOne.x, PointOne.y,PointTwo.x,PointTwo.y),4.5f,4.5f   ,mPaint);
            
         
       }
             
	}

	public void ProcessSketchingRequest(ServerThreadResponse ServerResponce){
//		if (ModeOfServerUse == Flags.SERVER_MODE_QHTC){
			if (ServerResponce.ModeUsed == Flags.QHTC_HASH_REQUEST_MODE){
					if (ServerResponce.Response.startsWith("POLYGON((")){ 				
						AddPolygonOnMapFromServerCommand(ServerResponce.Response);
						//ServerResponce.osmMap.invalidate();
					}
					else if (ServerResponce.Response.startsWith("POINT(")){
						String x = ServerResponce.Response.substring(6,ServerResponce.Response.length() -2);
						x = x + ",1 1";
						ParseMarkersAndMapSet(x );
					}else { // the empty response means there is no matches
						
					}
					
				mOsmv.setClickable(false);
 				MeHandle.mOsmv.setMultiTouchControls(true);
 				this.dv.dynamicBitmap.DrawButtons = false;
 				this.dv.dynamicBitmap.DrawBackGround = false;
 				this.dv.dynamicBitmap.CanBuildNewObjects = false;
 				this.dv.dynamicBitmap.CanSelectObjects = false;
 				this.dv.dynamicBitmap.DrawObjects = false;
 				this.dv.dynamicBitmap.DrawSecondLayer = false;
 				//CanDrawOverMap =  false;
 				mOsmv.invalidate();
 				ServerSendQuery tasktwo = new ServerSendQuery();
 				ServerQueryWithMode mode = new ServerQueryWithMode();
 				mode.dv = ServerResponce.dv;
 				mode.ModeUsed = Flags.QHTC_NORMAL_REQUEST_MODE;
 				tasktwo.execute(mode);
				}
				if (ServerResponce.ModeUsed == Flags.QHTC_NORMAL_REQUEST_MODE){
					if (sharedSettings.WaitDialogHandle!= null){
						sharedSettings.WaitDialogHandle.finish();
						sharedSettings.WaitDialogHandle = null;
					}
					
		    		 
				//mOsmv.setClickable(false);
 				//MeHandle.mOsmv.setMultiTouchControls(true);
 				//CanDrawOverMap =  false;
 				ParseMarkersAndMapSet(ServerResponce.Response );
 				mOsmv.invalidate();
 			}				
			
	}       
		
	//- end temp for the maps 
	public class ServerThreadResponse{
		public MapView  osmMap;
		public String Response;
		public int ModeUsed;
		public DrawOverMap dv;
		public String QueryText = "";
	}
	public class ServerQueryWithMode {
		public MapView osmMap ; 
		public DrawOverMap dv;
		public int ModeUsed = 0;
		public String QueryText = "";
	}
	public class ServerSendQuery extends AsyncTask<ServerQueryWithMode,Void ,ServerThreadResponse> {
	    @Override
	    protected void onPreExecute (){
	    	if (sharedSettings.WaitDialogHandle == null){
	    		MeHandle.startActivity(new Intent("jo.edu.just.ProgressWait"));
	    		
	    	}
	    }
		
		@Override
		 protected ServerThreadResponse doInBackground(ServerQueryWithMode... obj) {
			ServerThreadResponse h = new ServerThreadResponse();
			try{
	    	 
	    	 	writer tmpWriter = 	new jo.edu.just.XML.writer();
				
	    	 	h.ModeUsed = obj[0].ModeUsed;
	    	 	h.QueryText = obj[0].QueryText;
	    	 	h.dv 		= obj[0].dv;
	    	 	if (h.ModeUsed == Flags.QHTC_HASH_REQUEST_MODE) sharedSettings.QHTC_SERVER_REQUEST_TRANSITION_MODE = Flags.QHTC_HASH_REQUEST_MODE;
	    	 	if (h.ModeUsed == Flags.QHTC_NORMAL_REQUEST_MODE){
	    	 		
	    	 		sharedSettings.QHTC_SERVER_REQUEST_TRANSITION_MODE = Flags.QHTC_NORMAL_REQUEST_MODE;
	    	 	}
	    	 	
	    	 	if (h.ModeUsed == Flags.QUERY_ASK_WIZARD_TYPE){
	    	 		//log.d("wizard request", "wizard request is here");
	    	 		//log.d("wiazard query text",h.QueryText);
	    	 		h.Response = tmpWriter.SendGeneralRequest(h.QueryText );
	    	 		//log.d("wizard response", h.Response);
	    	 		//log.d("wiazrd error ", tmpWriter.LastError);
	    	 		
	    	 	}else
	    	 		h.Response = tmpWriter.SendDataToServer(obj[0].dv.dynamicBitmap);
				
	    	 	h.dv = obj[0].dv;
				h.osmMap = obj[0].osmMap;
				
				
				
				while(sharedSettings.WaitDialogHandle == null);
				
				return h;
			}catch(Exception e){
				Log.d("wizard exception", e.getMessage());
				h.Response = "error"; 
				h.dv = obj[0].dv;
				h.osmMap = obj[0].osmMap;
				return h;
				
			}
	     }

	    
		
		
		
		

	     protected void onPostExecute(ServerThreadResponse ServerResponce) {
	    	 try{
	    		if (ServerResponce.Response.compareTo("error") ==0){
	    			HandleSendQueryErrorToGoBackState();
	    			showAlertDialog("Error Happened while contacting the Server!");	
	    			return;
	    		}
	    		 
	    		if (ModeOfServerUse == Flags.SERVER_MODE_SQL  || ServerResponce.ModeUsed == Flags.QUERY_ASK_WIZARD_TYPE){			 
	 				  if (ServerResponce.dv.dynamicBitmap.content.size() !=0 && ServerResponce.ModeUsed == Flags.QUERY_ASK_WIZARD_TYPE){
	 					 int[] min  =((joPath) dv.dynamicBitmap.content.get(0)).GetMinBoundBoxGeo();
	 					 GeoPoint [] pts =  new GeoPoint[5]; 
	 					 pts[1] = new GeoPoint(min[0],min[1]);
	 					 pts[3] = new GeoPoint(min[2],min[3]);
	 					 ParseMarkersAndMapSetWithGeoRect(ServerResponce.Response ,
	 				    		 ((joPath) dv.dynamicBitmap.content.get(0)));
	 				    		 
	 				  }else if (ServerResponce.ModeUsed == Flags.QUERY_ASK_WIZARD_TYPE)
	 					 ParseMarkersAndMapSet(ServerResponce.Response );
			    		 
		    		 ServerResponce.dv.dynamicBitmap.tempLayer.clear();
		    		 ServerResponce.dv.dynamicBitmap.InEditMode = false;
		    		 ServerResponce.dv.dynamicBitmap.tempLast = null; 
		    		 mOsmv.setClickable(false);
		 			 MeHandle.mOsmv.setMultiTouchControls(true);
		 			//CanDrawOverMap = true;
		 			 dv.dynamicBitmap.DrawButtons = false;
		 			 dv.dynamicBitmap.DrawObjects = true;
		 			 dv.dynamicBitmap.CanBuildNewObjects = false;
		 			 dv.dynamicBitmap.DrawSecondLayer = false;
		 			 dv.dynamicBitmap.DrawBackGround = false;
		 			if (sharedSettings.WaitDialogHandle!= null) {
	 					 sharedSettings.WaitDialogHandle.finish();
	 					 sharedSettings.WaitDialogHandle = null;
	 				 }
		 			return ;
	 			}
	 			
	 			if (ModeOfServerUse == Flags.SERVER_MODE_QHTC)
	 				ProcessSketchingRequest(ServerResponce);
	 				


	 			} catch(Exception e){
	 				Log.d(" wizard exception", "");
					
	 				HandleSendQueryErrorToGoBackState();
	 				showAlertDialog("Error Happened while contacting the Server 2!");	
	 			}
	 		
	 		
	 		
	 	
	     }
	 }
	 
	
	public void CreateRectangleOverlay(GeoPoint[] pts){
		TriangleOverlay t = new TriangleOverlay(this,pts);
		
		mOsmv.getOverlays().add(t);
		mOsmv.invalidate();
	}
	
	public void ParseMarkersAndMapSet( String GeoPointsStr){
		 final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		 Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.marker_red);
	       
		 
		 
		 String delims = "[,]+";
		 String delims2 = "[ ]+";
		 String[] GeoPoints = GeoPointsStr.split(delims);	
		
		 for (int i =0 ; i < GeoPoints.length-1;i++){
			 
			 ////log.d("pos",GeoPoints[i]);
			 String [] xANDy = GeoPoints[i].split(delims2);
			 
			 double [] lonLat = ConvertFrom900913to4326(Double.valueOf(xANDy[0]), Double.valueOf(xANDy[1]));
			 
			// Log.d("pos s" ,String.valueOf( lonLat[0]));
			// Log.d("pos",String.valueOf( lonLat[1]));
			 GeoPoint pt = new GeoPoint(lonLat[1],lonLat[0]);
			 
			 
			 
			 OverlayItem overlayItem = new OverlayItem("Option", "Option", pt);
			 overlayItem.setMarker(this.getResources().getDrawable(R.drawable.marker_red));
			 
			 mOsmv.getController().setCenter(pt);
			 
			 items.add(overlayItem);		 
		 }
		 
		 
		 this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(this.getApplicationContext(),items,
	                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
	                    @Override
	                    public boolean onItemSingleTapUp(final int index,
	                            final OverlayItem item) {
	                       
	                        return true; // We 'handled' this event.
	                    }
	                    @Override
	                    public boolean onItemLongPress(final int index,
	                            final OverlayItem item) {
	                       
	                        return false;
	                    }
	                });
	        this.mOsmv.getOverlays().add(this.mMyLocationOverlay);
	        this.mOsmv.invalidate();
		
	}
	
	public void ParseMarkersAndMapSetWithGeoRect( String GeoPointsStr,joPath obj){
		 final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		 Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.marker_red);
	       
		 
		 
		 String delims = "[,]+";
		 String delims2 = "[ ]+";
		 String[] GeoPoints = GeoPointsStr.split(delims);	
		
		 for (int i =0 ; i < GeoPoints.length-1;i++){
			 
			 ////log.d("pos",GeoPoints[i]);
			 String [] xANDy = GeoPoints[i].split(delims2);
			 
			 double [] lonLat = ConvertFrom900913to4326(Double.valueOf(xANDy[0]), Double.valueOf(xANDy[1]));
			 
			// Log.d("pos s" ,String.valueOf( lonLat[0]));
			// Log.d("pos",String.valueOf( lonLat[1]));
			 GeoPoint pt  = new GeoPoint(lonLat[1],lonLat[0]);
			 //GeoPoint pts = new GeoPoint(lonLat[1],-lonLat[0]);
				 
			 if ( obj.IsGeoPointInsideMyGeoPoints(pt)   ){
				OverlayItem overlayItem = new OverlayItem("Option", "Option", pt);
			 	overlayItem.setMarker(this.getResources().getDrawable(R.drawable.marker_red));
			 	mOsmv.getController().setCenter(pt);
			 	items.add(overlayItem);
			 }
			 
			 
		 }
		 
		 
		 this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(this.getApplicationContext(),items,
	                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
	                    @Override
	                    public boolean onItemSingleTapUp(final int index,
	                            final OverlayItem item) {
	                       
	                        return true; // We 'handled' this event.
	                    }
	                    @Override
	                    public boolean onItemLongPress(final int index,
	                            final OverlayItem item) {
	                       
	                        return false;
	                    }
	                });
	        this.mOsmv.getOverlays().add(this.mMyLocationOverlay);
	        this.mOsmv.invalidate();
		
	}
	
	public void ParseMarkersAndMapSetTempDebugFunction( GeoPoint GeoPT){
		 final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		 Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.marker_red);
	       
		 
		 
		 
			 
			 OverlayItem overlayItem = new OverlayItem("Option", "Option", GeoPT);
			 overlayItem.setMarker(this.getResources().getDrawable(R.drawable.marker_red));
			 
			// mOsmv.getController().setCenter(pt);
			 
			 items.add(overlayItem);		 
			 this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(this.getApplicationContext(),items,
	                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
	                    @Override
	                    public boolean onItemSingleTapUp(final int index,
	                            final OverlayItem item) {
	                       
	                        return true; // We 'handled' this event.
	                    }
	                    @Override
	                    public boolean onItemLongPress(final int index,
	                            final OverlayItem item) {
	                       
	                        return false;
	                    }
	                });
	        this.mOsmv.getOverlays().add(this.mMyLocationOverlay);
	        this.mOsmv.invalidate();
		
	}
	
	
	
	
	public void SetToMapViewOnly(boolean ClearOtherLayers){
		 
		mOsmv.setClickable(false);
		mOsmv.setMultiTouchControls(true);
		dv.dynamicBitmap.DrawButtons = true;
		dv.dynamicBitmap.DrawSecondLayer = false;
		dv.dynamicBitmap.CanSelectObjects = false;
		dv.dynamicBitmap.DrawObjects =false;
		dv.dynamicBitmap.DrawBackGround = false;
		dv.dynamicBitmap.CanBuildNewObjects = false;
		dv.dynamicBitmap.CanSendTouchToButtons = true;;
	
        if (ClearOtherLayers){
        	mOsmv.getOverlays().clear();
    		MapOverlay movl = new MapOverlay(this);
            mOsmv.getOverlays().add(movl);
            
        }
		
	}
	public void HandleSendQueryErrorToGoBackState(){
		CanDrawOverMap =  true;
		//MainContainer.CanDrawPathsOnMap = true;
		//MainContainer.CansendTouchesToDrawView = true;
		dv.dynamicBitmap.DrawButtons = true;
		dv.dynamicBitmap.DrawSecondLayer = true;
		dv.dynamicBitmap.CanSelectObjects = true;
		dv.dynamicBitmap.DrawObjects =true;
		dv.dynamicBitmap.DrawBackGround = true;
		dv.dynamicBitmap.CanSendTouchToButtons = true;
		
		mOsmv.getOverlays().clear();
		MapOverlay movl = new MapOverlay(this);
        mOsmv.getOverlays().add(movl);
        
        mOsmv.setClickable(true);
        mOsmv.setBuiltInZoomControls(true);
		mOsmv.invalidate();
		if (sharedSettings.WaitDialogHandle!= null) {		
				 sharedSettings.WaitDialogHandle.finish();
				 sharedSettings.WaitDialogHandle = null;
		}	
	}
	
	
	
	
	
	
	
}




