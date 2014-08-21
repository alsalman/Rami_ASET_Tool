package jo.edu.just;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout.Spec;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class TextNotationEx extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	String Annot;
	int SpecialTab;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private int numberOfTabs = 3;
	
	
	@Override
	public void onBackPressed(){
		Intent data = new Intent();
    	data.setData(Uri.parse("|none" ) );
    	setResult(RESULT_OK, data);
		finish();
    	
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		
		
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null)  {
			String [] tmpStr = extras.getString("annotation").split("[|]+");
			Annot  = tmpStr[0];
			SpecialTab = extras.getInt("specialTab");
			if (SpecialTab ==0 ){// all modes togather
				numberOfTabs = 3;
			}
			if (SpecialTab ==1 ){// voice mode  only
				numberOfTabs = 1;
				//setTheme(android.R.style.Animation_Activity);
			}
			if (SpecialTab ==2 ){// text mode  only
				numberOfTabs = 1;
				//setTheme(android.R.style.Theme_Dialog);
			}
			if (SpecialTab ==3){
				numberOfTabs = 1;
			}
			
			
		}
		
	//	if (SpecialTab ==0){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
	//	}
        setContentView(R.layout.textnotation_ex);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
			
			
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position ==1){
				android.support.v4.app.Fragment fragment = new TextFragment();
				Bundle args = new Bundle();
				args.putString("Annot",Annot);
				fragment.setArguments(args);
				return fragment;
			}
			
			if (position ==2){
				android.support.v4.app.Fragment fragment = new VoiceFragment();
				Bundle args = new Bundle();
				args.putString("Annot",Annot);
				fragment.setArguments(args);
				return fragment;
			}
			if (position ==0){
				android.support.v4.app.Fragment fragment = null;// = new PreFragment();
				if (SpecialTab ==0 )  fragment =  new PreFragment();
				if (SpecialTab ==1 )  fragment =  new VoiceFragment();
				if (SpecialTab ==2 )  fragment =  new TextFragment();
				if (SpecialTab ==3 )  fragment =  new WizardFragment();
				
				Bundle args = new Bundle();
				args.putString("Annot",Annot);
				fragment.setArguments(args);
				return fragment;
			}
			
			
			return  new android.support.v4.app.Fragment();
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return numberOfTabs;
		}

		
		
		
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				if (SpecialTab == 0) return "Annotate using Predefined Names";
				if (SpecialTab == 1) return "Annotate by voice";
				if (SpecialTab == 2) return "Annotate by Text";
				return null;
			case 1:
				return "Annotate by Text";
			case 2:
				return "Annotate by voice";
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class TextFragment extends android.support.v4.app.Fragment{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		TextView dummyTextView ;
		public android.support.v4.app.FragmentActivity father ;
		
		public TextFragment(){
			
			
			
		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.text_notation_by_text, container, false);
			dummyTextView = (TextView) rootView.findViewById(R.id.editText1xx);
			dummyTextView.setText(getArguments().getString("Annot"));
			
			
			Button h= (Button)rootView.findViewById(R.id.button2x2);
	        
	        h.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent data = new Intent();
					
						
					
						data.setData(Uri.parse(dummyTextView.getText().toString() + "|text")  );
						getActivity().setResult(RESULT_OK, data);
						getActivity().finish();
				
				}
			});
			
			return rootView;
		}
	}
	
	
	public static class VoiceFragment extends android.support.v4.app.Fragment{
		TextView dummyTextView ;
		ListView wordsList;
		public VoiceFragment(){
			
			
			
		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.text_notation_by_voice, container, false);
			dummyTextView = (TextView) rootView.findViewById(R.id.editText1xx_voice);
			dummyTextView.setText(getArguments().getString("Annot"));
			wordsList = (ListView) rootView.findViewById(R.id.list_voice);
			
			Button speak_b = (Button)rootView.findViewById(R.id.button2x2voice_voice_speak);
			
			 PackageManager pm = getActivity().getPackageManager();
		     List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		     if (activities.size() == 0)
		     {
		    	 speak_b.setEnabled(false);
		         speak_b.setText("Recognizer not present");
		     }
			
			
			
			Button ok= (Button)rootView.findViewById(R.id.button2x2voice_voice);
	        
	        ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent data = new Intent();
					
						data.setData(Uri.parse(dummyTextView.getText().toString() + "|voice")  );
						getActivity().setResult(RESULT_OK, data);
						getActivity().finish();
				
				}
			});
			
	        
	        speak_b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Build your query...");
			        startActivityForResult(intent, 72);
				}
			});
	        
	        
	      wordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			
				dummyTextView.setText(
				
				arg0.getItemAtPosition(arg2).toString()
			);
			}
		});
	       
	        
	        
	        
			return rootView;
		}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 72 && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
           // ArrayAdapter<T>android.s
            
            
            
            
            wordsList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                    matches));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	}   
	
	
	public static class PreFragment extends android.support.v4.app.Fragment{
		//TextView dummyTextView ;
		List<String> list = new ArrayList<String>();
		private Spinner spinner;
		
		public PreFragment(){
			 	//list.add("-From the text box-");
		      
			if (MainContainer.ModeOfServerUse ==1)
			{
			
		        list.add("building");
		        list.add("parking");
		        list.add("university");
		        list.add("pitch");
		        list.add("park");
		        list.add("miniature_golf");
		        list.add("scrub");
		        list.add("water_park");
		        list.add("water");
		        list.add("public_building");
		        list.add("library");
		        list.add("kindergarten");
		        list.add("school");
			}
			if (MainContainer.ModeOfServerUse ==0){
				 list.add("building"                    );
				 list.add("swimming_pool"               );
				 list.add("weir"                        );
				 list.add("water_park"                  );
				 list.add("police"                      );
				 list.add("wetland"                     );
				 list.add("bicycle_parking"             );
				 list.add("garden"                      );
				 list.add("water"                       );
				 list.add("courthouse"                  );
				 list.add("riverbank"                   );
				 list.add("cinema"                      );
				 list.add("nature_reserve"              );
				 list.add("grave_yard"                  );
				 list.add("fountain"                    );
				 list.add("slipway"                     );
				 list.add("library"                     );
				 list.add("grass"                       );
				 list.add("school"                      );
				 list.add("kindergarten"                );
				 list.add("drain"                       );
				 list.add("park"                        );
				 list.add("pub"                         );
				 list.add("pitch"                       );
				 list.add("parking"                     );
				 list.add("fell"                        );
				 list.add("playground"                  );
				 list.add("restaurant"                  );
				 list.add("car_wash"                    );
				 list.add("brothel"                     );
				 list.add("wood"                        );
				 list.add("cafe"                        );
				 list.add("place_of_worship"            );
				 list.add("private parking"             );
				 list.add("fuel"                        );
				 list.add("marketplace"                 );
				 list.add("nightclub"                   );
				 list.add("track"                       );
				 list.add("stadium"                     );
				 list.add("university"                  );
				 list.add("meadow"                      );
				 list.add("generator"                   );
				 list.add("hospital"                    );
				 list.add("land"                        );
				 list.add("fast_food"                   );
				 list.add("station"                     );
				 list.add("recycling_center"            );
				 list.add("green"                       );
				 list.add("common"                      );
				 list.add("dock"                        );
				 list.add("beach"                       );
				 list.add("public_building"             );
				 list.add("sub_station"                 );
				 list.add("bus_station"                 );
				 list.add("biergarten"                  );
				 list.add("bank"                        );
				 list.add("sports_centre"               );
				 list.add("post_office"                 );
				 list.add("fair"                        );
				 list.add("canal"                       );
				 list.add("prison"                      );
				 list.add("pharmacy"                    );
				 list.add("arts_centre"                 );
				 list.add("marina"                      );
				 list.add("golf_course"                 );
				 list.add("nursing_home"                );
				 list.add("shelter"                     );
				 list.add("college"                     );
				 list.add("marsh"                       );
				 list.add("theatre"                     );
				 list.add("fire_station"                );
				 list.add("scrub"                       );
				 list.add("mud"                         );
				 list.add("miniature_golf"              );
			}
			
		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.text_notation_by_pre_def, container, false);
			//dummyTextView = (TextView) rootView.findViewById(R.id.editText1xx_pre_def);
			//dummyTextView.setText(getArguments().getString("Annot"));			
			Button ok = (Button)rootView.findViewById(R.id.button2x2voice_pre_def);
			
			
			
			
			
	        ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent data = new Intent();
					
						data.setData(Uri.parse(spinner.getSelectedItem().toString() + "|pre_def")  );
						getActivity().setResult(RESULT_OK, data);
						getActivity().finish();
				
				}
			});
			
	       
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner = (Spinner)rootView.findViewById(R.id.spinnerAnnot_pre);
	        spinner.setAdapter(dataAdapter);
	       
	       
	        
	        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					//dummyTextView.setText(arg0.getItemAtPosition(arg2).toString()    );
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	        


	        for (int i  =0  ; i< list.size()+1;i++){
   			 if ( i  ==  list.size() ){
   				// dummyTextView.setText(getArguments().getString("Annot"));
   				 
   			 }else
   			 if (list.get(i).compareTo(getArguments().getString("Annot")) ==0){
   				 //dummyTextView.setText(getArguments().getString("Annot"));
   				 spinner.setSelection(i,true);
   			 }
   		 }
	        
	        
			return rootView;
		}
	
	
	
	}   
	
	public static class WizardFragment extends android.support.v4.app.Fragment{
		//TextView dummyTextView ;
		List<String> list = new ArrayList<String>();
		List<String> listDimention = new ArrayList<String>();
		List<String> listDimention_distance = new ArrayList<String>();
		List<String> listDimention_direction = new ArrayList<String>();
		List<String> listDimention_Topolgy = new ArrayList<String>();
		
		Map<Long , String > dimention_rel_map = new HashMap<Long , String>();
		Map<Long , String > dimention_distance_map = new HashMap<Long , String>();
		Map<Long , String > dimention_direction_map = new HashMap<Long , String>();
		Map<Long , String > dimention_Topology_map = new HashMap<Long , String>();
		
		
		
		private Spinner spinner1;
		private Spinner spinner2;
		private Spinner spinner_Dimention;
		private Spinner spinner_Relation;
		public WizardFragment(){
			 	//list.add("-From the text box-");
		      
				listDimention.add("Distance");
				listDimention.add("Direction");
				listDimention.add("Toplogy");
				
				dimention_rel_map.put(0l, "calculus_dist");
				dimention_rel_map.put(1l, "calculus_dir");
				dimention_rel_map.put(2l, "calculus_top");
				
				
				
				listDimention_distance.add("Near to");
				listDimention_distance.add("Meduim");
				listDimention_distance.add("Far");
				dimention_distance_map.put(0l, "near");
				dimention_distance_map.put(1l, "medium");
				dimention_distance_map.put(2l, "far");
				
				
				
				listDimention_direction.add("North");
				listDimention_direction.add("North West");
				listDimention_direction.add("North East");
				listDimention_direction.add("South");
				listDimention_direction.add("South East");
				listDimention_direction.add("South West");
				listDimention_direction.add("East");
				listDimention_direction.add("West");
				listDimention_direction.add("Center");
				dimention_direction_map.put(0l, "010000000");
				dimention_direction_map.put(1l, "100000000");
				dimention_direction_map.put(2l, "001000000");
				dimention_direction_map.put(3l, "000000010");
				dimention_direction_map.put(4l, "000000001");
				dimention_direction_map.put(5l, "000000100");
				dimention_direction_map.put(6l, "000001000");
				dimention_direction_map.put(7l, "000100000");
				dimention_direction_map.put(8l, "000010000");
				
				
				
				
				listDimention_Topolgy.add("Covered by");
				listDimention_Topolgy.add("Meets");
				listDimention_Topolgy.add("equal");
				listDimention_Topolgy.add("disjoint");
				listDimention_Topolgy.add("overlap");
				listDimention_Topolgy.add("contains");
				listDimention_Topolgy.add("covers");
				listDimention_Topolgy.add("inside");
				dimention_Topology_map.put(0l, "false false true false false false false false true");
				dimention_Topology_map.put(1l, "false false true true false false false false false");
				dimention_Topology_map.put(2l, "true false false false false false false false false");
				dimention_Topology_map.put(3l, "false true false false false false false false false");
				dimention_Topology_map.put(4l, "false false true false false true false false false");
				dimention_Topology_map.put(5l, "false false true false false false true false false");
				dimention_Topology_map.put(6l, "false false true false false false false true false");
				dimention_Topology_map.put(7l, "false false true false true false false false false");
				
				
				
			
		        list.add("building");
		        list.add("parking");
		        list.add("university");
		        list.add("pitch");
		        list.add("park");
		        list.add("miniature_golf");
		        list.add("scrub");
		        list.add("water_park");
		        list.add("water");
		        list.add("public_building");
		        list.add("library");
		        list.add("kindergarten");
		        list.add("school");
			
			
		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.text_notation_bywizard, container, false);
			//dummyTextView = (TextView) rootView.findViewById(R.id.editText1xx_pre_def);
			//dummyTextView.setText(getArguments().getString("Annot"));			
			Button ok = (Button)rootView.findViewById(R.id.button1_wizard);
			
			
			
			
	        ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent data = new Intent();
					
					
					String WebCommand = "http://aset.informatik.uni-bremen.de/rec.php/" +
							"?fObj=" + spinner2.getSelectedItem()+ 
							"&sObj=" + spinner1.getSelectedItem()+
							"&rel="  + dimention_rel_map.get(spinner_Dimention.getSelectedItemId()) +
							"&value=" ;
					if (spinner_Dimention.getSelectedItemId() == 0) WebCommand += dimention_distance_map.get(spinner_Relation.getSelectedItemId());
					if (spinner_Dimention.getSelectedItemId() == 1) WebCommand += dimention_direction_map.get(spinner_Relation.getSelectedItemId());
					if (spinner_Dimention.getSelectedItemId() == 2) WebCommand += dimention_Topology_map.get(spinner_Relation.getSelectedItemId());
					
					WebCommand += "&type=wizard";
					
					
					//log.("server wizard query",WebCommand);
					
					
						data.setData(Uri.parse(WebCommand + "|wizard")  );
						
						
						getActivity().setResult(RESULT_OK, data);
						getActivity().finish();
				
				}
			});
			
	       
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner1 = (Spinner)rootView.findViewById(R.id.Spinner01xx_wizard);
	        spinner1.setAdapter(dataAdapter);
	        spinner2 = (Spinner)rootView.findViewById(R.id.spinner1_wizard);
	        spinner2.setAdapter(dataAdapter);
	        spinner_Dimention = (Spinner)rootView.findViewById(R.id.Spinner01_wizard);
	        spinner_Relation  = (Spinner)rootView.findViewById(R.id.Spinner01x_wizard);
	       
	        
	        dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listDimention);
		    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    
	        spinner_Dimention.setAdapter(dataAdapter);
	        
	        spinner_Dimention.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

	        	
	        	
	        	
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					if (arg2 ==0 ){
						 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listDimention_distance);
					     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					     spinner_Relation.setAdapter(dataAdapter);     
					}
					if (arg2 ==1 ){
						 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listDimention_direction);
					     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					     spinner_Relation.setAdapter(dataAdapter);     
					}
					if (arg2 ==2 ){
						 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listDimention_Topolgy);
					     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					     spinner_Relation.setAdapter(dataAdapter);     
					}
					
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					//dummyTextView.setText(arg0.getItemAtPosition(arg2).toString()    );
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	        


	        for (int i  =0  ; i< list.size()+1;i++){
   			 if ( i  ==  list.size() ){
   				// dummyTextView.setText(getArguments().getString("Annot"));
   				 
   			 }else
   			 if (list.get(i).compareTo(getArguments().getString("Annot")) ==0){
   				 //dummyTextView.setText(getArguments().getString("Annot"));
   				// spinner.setSelection(i,true);
   			 }
   		 }
	        
	        
			return rootView;
		}
	
	
	
	}   
	
}