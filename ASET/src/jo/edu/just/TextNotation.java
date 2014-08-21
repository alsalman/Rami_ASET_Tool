package jo.edu.just;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class TextNotation extends Activity {
	//TextView txt;
	//SeekBar x;
	List<String> list = new ArrayList<String>();
	private Spinner spinner;
	
	EditText editTxt;
	
	@Override

    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.textnotation);
        Bundle extras = getIntent().getExtras();

        list.add("-From the text box-");
        list.add("park");
        list.add("university");
        list.add("pitch");
        list.add("parking");
        list.add("miniature_golf");
        list.add("scrub");
        list.add("water_park");
        list.add("building");
        list.add("water");
        list.add("public_building");
        list.add("library");
        list.add("kindergarten");
        list.add("school");
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinner = (Spinner) findViewById(R.id.spinnerAnnot);
        
        
        spinner.setAdapter(dataAdapter);
        
        editTxt = (EditText)findViewById(R.id.editText1);
        
    	 if (extras != null)  {
    		 String tmpStr = extras.getString("annotation");
    		 for (int i  =0  ; i< list.size()+1;i++){
    			 if ( i  ==  list.size() ){
    				 editTxt.setText(tmpStr);
    				 spinner.setSelection(0,true);
    			 }else
    			 if (list.get(i).compareTo(tmpStr) ==0){
    				 editTxt.setText("");
    				 spinner.setSelection(i,true);
    			 }
    		 }
    		 
    		 //editTxt.setText(extras.getString("annotation") );
    	
    	 
    	 }

     	Button h= (Button)findViewById(R.id.button2);
        
        h.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent data = new Intent();
				
				if (spinner.getSelectedItemPosition() == 0 || spinner.getSelectedItemPosition() == -1 ){
					data.setData(Uri.parse(editTxt.getText().toString()) );
				}else{
					data.setData(Uri.parse((String) spinner.getSelectedItem()) );
				}
				
		    	//data.setData(Uri.parse(editTxt.getText().toString()) );
		    	setResult(RESULT_OK, data);
				finish();
			}
		});
    }
	@Override
	public void onBackPressed(){
		Intent data = new Intent();
    	data.setData(Uri.parse(editTxt.getText().toString()) );
    	setResult(RESULT_OK, data);
		finish();
    	
	}
	

}
