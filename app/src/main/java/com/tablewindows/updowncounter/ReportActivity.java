package com.tablewindows.updowncounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class ReportActivity extends Activity {
	
	public static ArrayList<UDCCount> ArrayofName = new ArrayList<UDCCount>();
	ListView listView;
    UpDownCounterDatabaseHelper dbUDC = new UpDownCounterDatabaseHelper(null);
    UDCCount cnt;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_report);
            listView = (ListView) findViewById(R.id.listofCounts);
            dbUDC = new UpDownCounterDatabaseHelper(getApplicationContext());
            ArrayofName = (ArrayList<UDCCount>) dbUDC.getAllCounts();
            listView.setAdapter(new UDCCountAdapter(this, android.R.layout.simple_list_item_1, ArrayofName)); 
      /*      listView.setOnItemClickListener(new OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
                   int itemPosition     = position;
                   String  itemValue    = (String) listView.getItemAtPosition(position);
                                    
                  }


    
             }); */
        }
    
        public class UDCCountAdapter extends ArrayAdapter<UDCCount> {

        	private ArrayList<UDCCount> counts;

        	public  UDCCountAdapter(Context context, int textViewResourceId, ArrayList<UDCCount> counts) {
        		super(context, textViewResourceId, counts);
        		this.counts = counts;
        	}

        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		View v = convertView;
        		if (v == null) {
        			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        			v = vi.inflate(R.layout.listitem, null);
        		}
        		
        		UDCCount cnt = counts.get(position);
        		if (cnt != null) {
        			TextView countname = (TextView) v.findViewById(R.id.countname);
        			TextView count = (TextView) v.findViewById(R.id.count);
        			TextView ldate = (TextView) v.findViewById(R.id.ldate);
        			if (cnt._name != null) {
        				countname.setText(cnt._name);
        				count.setText("Counts: " + cnt._ctrCount );
        				ldate.setText("Date: " + new Date(cnt._ctrDate) );
        			}
        		}
        		return v;
        	}
        }      
        
    }

