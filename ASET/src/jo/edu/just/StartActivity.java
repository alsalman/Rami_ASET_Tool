package jo.edu.just;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

//1 refers to query by sketch
//2 refers to query using voice
//3 refers to query using wizard
//4 refers to query using text

public class StartActivity  extends Activity{
	
		List<String> list = new ArrayList<String>();
		private Spinner spinner;
		Bundle extras3 = new Bundle();
	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.start_activity);
	        
	        //log.("Start Activity", "Start activity started");
	        
	        
	        MainContainer.ModeOfUse = 0;
	        MainContainer.ModeOfServerUse = 0;
	       // MainContainer.CanDrawPathsOnMap = true;
	        //MainContainer.CansendTouchesToDrawView = true;
	        
	       
	        list.add("QHTC-9000 objects");
	        list.add("SQL-200 objects");
	        spinner = (Spinner) findViewById(R.id.spinner1_startActivity);
	        
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        
	        spinner.setAdapter(dataAdapter);
	       
	       
	        
	        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					
					extras3.putInt("ModeOfServerUse",arg2  );
					//dummyTextView.setText(arg0.getItemAtPosition(arg2).toString()    );
					//log.("spinner start activity index","x: " + arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        Button b1 = (Button) findViewById(R.id.button1_start);
	        b1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i3 = new Intent("jo.edu.just.MainContainer");
					//Bundle extras3 = new Bundle();
					extras3.putInt("mode",1);  
					i3.putExtras(extras3);	
					startActivity(i3);
				}
			});
	        
	        
	        Button b2 = (Button) findViewById(R.id.button2_start);
	        b2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i3 = new Intent("jo.edu.just.MainContainer");
					//Bundle extras3 = new Bundle();
					extras3.putInt("mode",2);  
					i3.putExtras(extras3);	
					startActivity(i3);
				}
			});
	        Button b3 = (Button) findViewById(R.id.button3_start);
	        b3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i3 = new Intent("jo.edu.just.MainContainer");
					//Bundle extras3 = new Bundle();
					extras3.putInt("mode",3);  
					i3.putExtras(extras3);	
					startActivity(i3);
				}
			});
	        
	        Button b4 = (Button) findViewById(R.id.button4_start);
	        b4.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i3 = new Intent("jo.edu.just.MainContainer");
					//Bundle extras3 = new Bundle();
					extras3.putInt("mode",4);  
					i3.putExtras(extras3);	
					startActivity(i3);
				}
			});
	        
	        
	        
	        
	        
		}
}
