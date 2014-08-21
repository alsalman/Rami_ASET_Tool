package jo.edu.just;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


public class ProgressWait extends Activity {
	
	@Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedSettings.WaitDialogHandle = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        
        setContentView(R.layout.waitdialog);
        
        
      
        
       
    }
	@Override
	public void onBackPressed(){
		sharedSettings.WaitDialogHandle = null;
		finish();
		// just to ignore the back press ; need to be modified later
		// so the user can cancel the server request 
	}

	

}
