package com.tablewindows.updowncounter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView; 
import android.widget.Toast;


import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;


 
public class HelpActivity extends Activity {
 Boolean fApp =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
    
		InputStream iFile = getResources().openRawResource(R.raw.help);
        TextView helpText = (TextView) findViewById(R.id.TextView_HelpText);
        helpText.setMovementMethod(new ScrollingMovementMethod());
        try {

            String strFile = inputStreamToString(iFile);
            helpText.setText(Html.fromHtml(strFile));
            //helpText.
        } catch (Exception e) {
           // Log.e(DEBUG_TAG, "InputStreamToString failure", e);

        }        


    }
 
     public String inputStreamToString(InputStream is) throws IOException {
        StringBuffer sBuffer = new StringBuffer();
        DataInputStream dataIO = new DataInputStream(is);
        String strLine = null;

        while ((strLine = dataIO.readLine()) != null) {
            sBuffer.append(strLine + "\n");
        }

        dataIO.close();
        is.close();

        return sBuffer.toString();
    }
 



     
     
     
}