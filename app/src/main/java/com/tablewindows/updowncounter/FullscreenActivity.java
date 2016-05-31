package com.tablewindows.updowncounter;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FullscreenActivity extends Activity {
	public Integer mCounter =0, intDirection=1, intCountMode=0, intAmDelay=1000, intStatus=0, intWallpaperSet=0,
			intThreadDepth=1000,intThreadSpeed=60000,intRunning=0,intPlayerStatus=0, bgColor=0;
	Boolean mSound = false, mVibrate=false, mTitle=true, mGesture=true, mVolKey=false, mShowCountOnBtn=false;
	Boolean mFirstTimePrefLoad=true;
	public TextView txtCounter, txtLastUpdate, txtTitle, txtCountertop, txtMode;
	private AdView mAdView;
	public RelativeLayout dashboard;
	private LinearLayout titlearea,toprightbtnMode,botrighttxt;
	private GestureDetector gestureScanner;
	 private static int RESULT_LOAD_IMAGE = 1;
	 private String selectedImagePath;
	 private ImageView img;
	 ImageButton cmdLock, cmdWallpaper, cmdTitle, cmdHelp, cmdSave, cmdReport;
	 private ImageButton cmdCounter;
	 Calendar mCal ;  
	 MediaPlayer player;
	 AutoCounter mAutoCounter;
	 public static final String PREFS_COUNT = "MyPrefsFile";
	 Boolean fApp =false;
	 Resources res;
	 String[] nCount  ;
	 private UpDownCounterDatabaseHelper dbUDC;
	 Long longDate;
	 List lstCount;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		dbUDC = new UpDownCounterDatabaseHelper(getApplicationContext()); 
		res = getResources();
		nCount = res.getStringArray(R.array.nCount);
		player = new MediaPlayer();
		
		assignUIComponents();
		loadpref();
		loadAd();
		final GestureDetector gesture = new GestureDetector(this,
	            new GestureDetector.SimpleOnGestureListener() {
	                @Override
	                public boolean onDown(MotionEvent e) {
	                	if (!mGesture && intCountMode==0)
	                		updateCounter();
	                	
	                    return true;
	                }
	                @Override
	                public boolean onDoubleTap(MotionEvent e) {
	                    if(intStatus==0 && intCountMode==0)
	                    	updateCounter();
	                	return super.onDoubleTap(e);

	                }
	                @Override
	                public void onLongPress(MotionEvent e) {
	                    super.onLongPress(e);
	                    if(intStatus==0)
	                    resetCounter();
	                }
	                @Override
	                public boolean onSingleTapConfirmed(MotionEvent e) {
	                    return super.onSingleTapConfirmed(e);
	                }
	                @Override
	                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	                                       float velocityY) {
	                	if(mGesture && intStatus ==0 && intCountMode==0)
	                	{
	                    final int SWIPE_MIN_DISTANCE = 120;
	                    final int SWIPE_MAX_OFF_PATH = 250;
	                    final int SWIPE_THRESHOLD_VELOCITY = 200;
	                    try {
	                        if (e1.getX() >= 1050 && e1.getY() <= 25 && e2.getX() >= 950 && e2.getY() <= 200) {
 //					        	Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_SHORT).show();
	                        } else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
	                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//	                        	Toast.makeText(getApplicationContext(), "B", Toast.LENGTH_SHORT).show();
	                        	downCounter();
	                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
	                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//	                        	Toast.makeText(getApplicationContext(), "C", Toast.LENGTH_SHORT).show();
	                        	upCounter();
	                        } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
//	                        	Toast.makeText(getApplicationContext(), "D", Toast.LENGTH_SHORT).show();
	                        	upCounter();
	                        } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
//	                        	Toast.makeText(getApplicationContext(), "E", Toast.LENGTH_SHORT).show();
	                        	downCounter();
	                        }
	                    } catch (Exception e) {
	                        Log.e("ControllerFragment", "Exception @ onFling");
	                    }
	                }
						 if (intCountMode ==1)
								Toast.makeText(getApplicationContext(), "Press update counter button again to continue", Toast.LENGTH_LONG).show();
	                    return super.onFling(e1, e2, velocityX, velocityY);
	                }
	            });

		dashboard.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            return gesture.onTouchEvent(event);
	        }
	    });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

    private boolean MyStartActivity(Intent aIntent) {
   	 try {
   	     startActivity(aIntent);
   	     return true;
   	 } catch (ActivityNotFoundException e) {
   	     return false;
   	 }
   	 }
	
    
    public void shwSettings(View v)
    {
        Intent intent = new Intent(getBaseContext(), MyPreferencesActivity.class);
        startActivity(intent);
    }
    
    public void saveCtr(View v)
    {
    	dbUDC.addCounter(String.valueOf(txtTitle.getText()), mCounter,longDate);
    	Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
    	mCounter=0;
		if (intWallpaperSet==1)
			txtCountertop.setText(String.valueOf(mCounter));
			//txtCountertop.setText(nCount[mCounter]);
		else
		txtCounter.setText(String.valueOf(mCounter));
    }
    
    public void shwRpt(View v)
    {
    	Intent intent = new Intent(getBaseContext(),ReportActivity.class);
        startActivity(intent);
    }
    
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_settings:
            Intent intent = new Intent(getBaseContext(), MyPreferencesActivity.class);
            startActivity(intent);
            break;
	    case R.id.MenuRateApp:
	   	     //Try Google play
	         Intent intentrate = new Intent(Intent.ACTION_VIEW);
	         intentrate.setData(Uri.parse("market://details?id="+getPackageName()));
	         if (MyStartActivity(intentrate) == false) {
	             //Market (Google play) app seems not installed, let's try to open a webbrowser
	             intentrate.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
	             if (MyStartActivity(intentrate) == false) {
	                 //Well if this also fails, we have run out of options, inform the user.
	                 Toast.makeText(this, "Unble to make connection pleaes rate the app on Google play site, Thanks", Toast.LENGTH_LONG).show();
	             }
	         }
	         //Do not disturb again (even if the user did not rated the app in the end)
	         fApp= true;

	    default:
	      break;
	    }
	    return true;
	  } 
	
@Override
	protected void onPause(){
		   super.onPause();
		   savepref();
		}
	

@Override
protected void onStop() {
  super.onStop();
  savepref();
}


	  @Override
	    public void onResume() {
	     super.onResume();
	     loadpref();
	  }
	
	
	private void assignUIComponents()
	{
		dashboard = (RelativeLayout) findViewById(R.id.dashboard);
		img = (ImageView) findViewById(R.id.wallpaper);
		mAdView = (AdView) findViewById(R.id.adView);
		txtCounter = (TextView) findViewById(R.id.txtCounter);
		txtCountertop= (TextView) findViewById(R.id.txtCountertop);
		txtLastUpdate=(TextView) findViewById(R.id.txtLastupdate);
		txtTitle=(TextView) findViewById(R.id.txtTitle);
		titlearea = (LinearLayout) findViewById(R.id.toprightbtn);
		botrighttxt = (LinearLayout) findViewById(R.id.botrighttxt);
		toprightbtnMode = (LinearLayout) findViewById(R.id.toprightbtnMode);
		txtMode= (TextView) findViewById(R.id.txtMode);
		
		cmdTitle = (ImageButton) findViewById(R.id.cmdTitle);
		cmdWallpaper= (ImageButton) findViewById(R.id.cmdWallpaper);
		cmdLock= (ImageButton) findViewById(R.id.cmdLock);
		cmdCounter= (ImageButton) findViewById(R.id.cmdCounter);
		cmdHelp =(ImageButton) findViewById(R.id.cmdHelp);
		cmdSave =(ImageButton) findViewById(R.id.cmdSave);
		cmdReport =(ImageButton) findViewById(R.id.cmdReport);
		bgColor=getResources().getColor(R.color.homebg);
		pScnCtrsWdOuWp();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	
    private void loadAd(){
        boolean inEmulator = "generic".equals(Build.BRAND.toLowerCase());
   /*     if (inEmulator) 
           //	mAdView.loadAd(new AdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
            else 
           //	mAdView.loadAd(new AdRequest.Builder().addTestDevice("57966FDD73F896A8DEA99BF55CCC76D3").build());
   */ }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    int action = event.getAction();
	    int keyCode = event.getKeyCode();
	if (mVolKey && intStatus==0)
	{
	    switch (keyCode) {
	        case KeyEvent.KEYCODE_VOLUME_UP:
	            if (action == KeyEvent.ACTION_DOWN) {
	                upCounter();
	            }
	            return true;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	            if (action == KeyEvent.ACTION_DOWN) {
	            	downCounter();
	            }
	            return true;
	        default:
	            return super.dispatchKeyEvent(event);
	        }
	    }
	 return super.dispatchKeyEvent(event);
	}
	public void updateCounter()
	{
		switch (intDirection)
		{
		case 1:
			mCounter ++;
			break;
		case 0:
			mCounter --;
			break;
		default:
			mCounter ++;
			break;
		}
		if (intWallpaperSet==1)
			txtCountertop.setText(String.valueOf(mCounter));
			//txtCountertop.setText(nCount[mCounter]);
		else
		txtCounter.setText(String.valueOf(mCounter));
		soundAndVbr();
	}
	
	public void upCounter()
	{
		mCounter ++;
		if(intWallpaperSet==1) 
			txtCountertop.setText(String.valueOf(mCounter));
		else
			txtCounter.setText(String.valueOf(mCounter));	
		soundAndVbr();
	}
	
	public void downCounter()
	{
		mCounter --;
		if(intWallpaperSet==1) 
			txtCountertop.setText(String.valueOf(mCounter));
		else
			txtCounter.setText(String.valueOf(mCounter));
		soundAndVbr();
	}
	public void resetCounter()
	{
		mCounter =0;
		if(intWallpaperSet==1) 
			txtCountertop.setText(String.valueOf(mCounter));
		else
			txtCounter.setText(String.valueOf(mCounter));
		soundAndVbr();
	}
	
	public void soundAndVbr()
	{

		if(mVibrate)
		{
		Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 500 milliseconds
		 v.vibrate(50);
		}
		if(mSound)
		{
			
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			player = MediaPlayer.create(this, R.raw.white);
			if (intPlayerStatus==0)
			{
				player.start();
				player.setLooping(false);
				intPlayerStatus=1;
				player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()   {
		            @Override
		            public void onCompletion(MediaPlayer mp)       {
		               player.reset();
		               player.release();
		               intPlayerStatus=0;
		            }	});
			}
			}
		longDate =  new  Date().getTime();
		txtLastUpdate.setText("Last Update: " + longDate.toString());
	}
	
	public void btnPressed(View v)
	{
		if(intCountMode==1) 		{
			if (intRunning == 0) 		{
				disableControls();
				intRunning = 1;
				mAutoCounter = new AutoCounter();
	    	    mAutoCounter.execute();
				cmdCounter.setBackgroundColor(Color.BLUE);
			}
			else {
					mAutoCounter.setThreadPassNo(200);
					mAutoCounter.cancel(true);
					cmdCounter.setBackgroundColor(Color.TRANSPARENT);
					enableControls();
					intRunning = 0;
			}
		}
		else
		{
		updateCounter();

		}
	}
	
	
	
	
	private void enableControls() {
		// TODO Auto-generated method stub
		
	}

	private void disableControls() {
		// TODO Auto-generated method stub
		
	}

	public void chgLock(View v)
	{
		if (intStatus==0 )
		{
			intStatus=1;
			cmdTitle.setEnabled(false);
			cmdLock.setBackgroundColor(Color.RED);
			//cmdLock.setBackground(getResources().getDrawable(R.drawable.ic_locked));
			cmdWallpaper.setEnabled(false);
			cmdCounter.setEnabled(false);
			cmdHelp.setEnabled(false);
		}
		else
		{
			intStatus=0;
			cmdTitle.setEnabled(true);
			if (intWallpaperSet==1)
				cmdLock.setBackgroundColor(bgColor);
			else
				cmdLock.setBackgroundColor(Color.TRANSPARENT);
			//cmdLock.setBackground(getResources().getDrawable(R.drawable.ic_unlocked));
			cmdWallpaper.setEnabled(true);
			cmdCounter.setEnabled(true);
			cmdHelp.setEnabled(true);
		}
	}
	
	public void chgTitle(View v)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Counter Title");
		alert.setMessage("Enter the title of counter:");
		final EditText txtTitleInput = new EditText(this);
		alert.setView(txtTitleInput);
		txtTitleInput.setText(txtTitle.getText());
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			  String opsText = txtTitleInput.getText().toString();
			  txtTitle.setText(opsText);
			  }
			});
			
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  @Override
			public void onClick(DialogInterface dialog, int whichButton) {

			  }
			});
			alert.show();
	}

	public void savepref()
	{
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		  SharedPreferences.Editor editor = mySharedPreferences.edit();
		  editor.putInt("mCounter", mCounter);
		  editor.putString("mTitle", txtTitle.getText().toString());
		  editor.putString("mdate", txtLastUpdate.getText().toString());
		  editor.putString("mPicturePath", selectedImagePath);
		  editor.putInt("mWallpaper", intWallpaperSet);
		  editor.putBoolean("mfApp", fApp);
		  editor.commit();
	}
	
	
	public void loadpref()
	{
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String strCountDirection  = mySharedPreferences.getString("direction", "up");
		String strCountMode  = mySharedPreferences.getString("mode", "manual");
		String strAmDelay = mySharedPreferences.getString("amdelay", "1 Second");
		
		Boolean bolSound  = mySharedPreferences.getBoolean("pref_sound", true);
		Boolean bolVibrate  = mySharedPreferences.getBoolean("pref_vibrate", false);
		Boolean bolTitle  = mySharedPreferences.getBoolean("pref_title", true);
		Boolean bolGesture  = mySharedPreferences.getBoolean("pref_gesture", true);
		Boolean bolVolKey  = mySharedPreferences.getBoolean("pref_volkey", false);
		Boolean bolfApp = mySharedPreferences.getBoolean("mfApp", false);
		Boolean bollupdate = mySharedPreferences.getBoolean("pref_lupdate", false);
		
		if (mFirstTimePrefLoad)
		{
			mCounter = mySharedPreferences.getInt("mCounter", 0);
			txtTitle.setText(mySharedPreferences.getString("mTitle", "Counter"));
			txtLastUpdate.setText(mySharedPreferences.getString("mdate", "Last Update"));
			if (mySharedPreferences.getInt("mWallpaper",0)==1) {
				img.setImageBitmap(getScaled(BitmapFactory.decodeFile(
						mySharedPreferences.getString("mPicturePath", "selectedImagePath"))));
				intWallpaperSet=1;
				pScnCtrsWdWp();
			}
			else
				pScnCtrsWdOuWp();
			mFirstTimePrefLoad=false;
			
		}
		if (strCountDirection.equalsIgnoreCase("up"))
			intDirection = 1;
		else if (strCountDirection.equalsIgnoreCase("down"))
			intDirection =0;
		else
			intDirection = 1;
		
		if (strCountMode.equalsIgnoreCase("auto"))
		{
			intCountMode = 1;
			toprightbtnMode.setVisibility(View.VISIBLE);
			txtMode.setVisibility(View.VISIBLE);
			txtMode.setText("Auto");
			Toast.makeText(getApplicationContext(), "Please press the Count button top right corner to start auto count", Toast.LENGTH_LONG).show();
		}
			else
		{
			intCountMode =0;
			txtMode.setText("");
			txtMode.setVisibility(View.GONE);
			toprightbtnMode.setVisibility(View.GONE);
			if(mAutoCounter!=null) {
			if (!mFirstTimePrefLoad && mAutoCounter.ThreadPassNo>0)
			{
				mAutoCounter.setThreadPassNo(1000);
				mAutoCounter.cancel(true);
				cmdCounter.setBackgroundColor(Color.TRANSPARENT);
			}}
		}
		
		switch (strAmDelay)
		{
		case "1 Second": 
     		intAmDelay =1000; 
     		break;
		case "2 Seconds": 
     		intAmDelay =2000; 
     		break;
		case "5 Seconds": 
     		intAmDelay =5000; 
     		break;
     	case "15 Seconds": 
     		intAmDelay =15000; 
     		break;
     	case "30 Seconds": 
     		intAmDelay = 30000;
     		break;
			case "1 Minute": 
	     		intAmDelay = 60000; 
	     		break;
			case "2 Minutes": 
				intAmDelay =120000;
				break;
			case "3 Minutes": 
				intAmDelay =180000; 
				break;
			case "4 Minutes":
				intAmDelay =240000;
				break;
			case "5 Minutes":
				intAmDelay =300000;
				break;
			case "6 Minutes":
				intAmDelay =360000;
				break;
			case "7 Minutes":
				intAmDelay =420000;
				break;
			case "8 Minutes":
				intAmDelay =480000;
				break;
			case "9 Minutes":
				intAmDelay =540000;
				break;
			case "10 Minutes":
				intAmDelay =600000;
				break;
			default:
			intAmDelay =60000;
			break;
		}
		
		mSound = bolSound;
		mTitle = bolTitle;
		mGesture = bolGesture;
		mVibrate = bolVibrate;
		mVolKey = bolVolKey;
		
		
		if(!mTitle) {
			cmdTitle.setVisibility(View.GONE);
			txtTitle.setVisibility(View.GONE);
	}
		else{
			cmdTitle.setVisibility(View.VISIBLE);
			txtTitle.setVisibility(View.VISIBLE);
		}

		if(!bollupdate) 
			botrighttxt.setVisibility(View.GONE);
		else
			botrighttxt.setVisibility(View.VISIBLE);
		
		
		
//		

//		Toast.makeText(getApplicationContext(), strCountDirection, Toast.LENGTH_SHORT).show();
//		Toast.makeText(getApplicationContext(), strCountMode, Toast.LENGTH_SHORT).show();
//		Toast.makeText(getApplicationContext(), bolSound.toString(), Toast.LENGTH_SHORT).show();
//		Toast.makeText(getApplicationContext(), bolVibrate.toString(), Toast.LENGTH_SHORT).show();
//		Toast.makeText(getApplicationContext(), bolTitle.toString(), Toast.LENGTH_SHORT).show();
//		Toast.makeText(getApplicationContext(), bolGesture.toString(), Toast.LENGTH_SHORT).show();
		
	}
	
	public void shwHelp(View v)
	{
        Intent intent = new Intent(getBaseContext(),HelpActivity.class);
        startActivity(intent);
	}
	
	public void chgWallpaper(View v)
	{
		if(intWallpaperSet==1){
			img.setImageBitmap(null);
    		//cmdTitle.setBackgroundColor(Color.BLACK);
			pScnCtrsWdOuWp();
    		intWallpaperSet=0;
    		mShowCountOnBtn=false;
    		SharedPreferences mySharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  		  SharedPreferences.Editor editor = mySharedPreferences2.edit();
  		  editor.putBoolean("pref_showcountonbtn", false);
  		  editor.commit();

		}
		else{
			Intent i = new Intent( Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    		startActivityForResult(i, RESULT_LOAD_IMAGE);
}
	}
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	         
	        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            selectedImagePath = cursor.getString(columnIndex);
	            cursor.close();
	            img.setImageBitmap(getScaled(BitmapFactory.decodeFile(selectedImagePath)));
	                //img.setImageURI(selectedImageUri);
	            pScnCtrsWdWp();
        		intWallpaperSet=1;
	            }
	        }
	 
	  public Bitmap getScaled(Bitmap d)
	  {
          int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
          Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
          return scaled;
	  }
	  
	  
	  public void pScnCtrsWdWp()
	  {
  		//cmdTitle.setBackgroundColor(Color.BLACK);
		 txtCountertop.setVisibility(View.VISIBLE);
		 txtCountertop.setText(String.valueOf(mCounter));
	  	txtCounter.setVisibility(View.GONE);
	  		
		 cmdWallpaper.setBackgroundColor(Color.BLUE);
  		cmdLock.setBackgroundColor(bgColor);
  		cmdCounter.setBackgroundColor(bgColor);
  		titlearea.setBackgroundColor(bgColor);
  		cmdHelp.setBackgroundColor(bgColor);
  		cmdSave.setBackgroundColor(bgColor);
  		cmdReport.setBackgroundColor(bgColor);
  		txtLastUpdate.setBackgroundColor(bgColor);
  		txtMode.setBackgroundColor(bgColor);
  		
	  }

	  public void pScnCtrsWdOuWp()
	  {
  		txtCountertop.setVisibility(View.GONE);
  		txtCounter.setVisibility(View.VISIBLE);
		txtCounter.setText(String.valueOf(mCounter));

		cmdWallpaper.setBackgroundColor(Color.TRANSPARENT);
  		cmdLock.setBackgroundColor(Color.TRANSPARENT);
  		cmdCounter.setBackgroundColor(Color.TRANSPARENT);
  		titlearea.setBackgroundColor(Color.TRANSPARENT);
  		cmdHelp.setBackgroundColor(Color.TRANSPARENT);
  		cmdSave.setBackgroundColor(Color.TRANSPARENT);
  		cmdReport.setBackgroundColor(Color.TRANSPARENT);
  		txtLastUpdate.setBackgroundColor(Color.TRANSPARENT);
  		txtMode.setBackgroundColor(Color.TRANSPARENT);
	  }
	  
		private class AutoCounter extends AsyncTask<Void, Integer, Void> {
			public Integer ThreadPassNo=0;
			
			public void setThreadPassNo(int pass) 	{
				ThreadPassNo=pass;
			}
			
			@Override
			protected Void doInBackground(Void... params) {

				for ( ;ThreadPassNo < intThreadDepth; ThreadPassNo ++) 		{
					try {
						sleep();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					publishProgress();

				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				Toast.makeText(getApplicationContext(), "Press update counter button again to continue", Toast.LENGTH_LONG).show();
				cmdCounter.setBackgroundColor(Color.TRANSPARENT);
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate();
				updateCounter();
//					cancelAsyncRunnerThread();

			}

			@Override
			protected void onCancelled(Void result) {
				super.onCancelled(result);
				}

			@Override
			protected void onCancelled() {
				super.onCancelled();
			}

			void sleep() throws InterruptedException 	{
				Thread.sleep(intAmDelay);
			}
		}
	  
		
		
		
		
	}
	  
