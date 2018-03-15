package matlogic.bluetest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.Set;


public class MainActivity extends Activity {

    // GUI Components
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private Button tomouse;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private final String TAG = MainActivity.class.getSimpleName();
    public BluetoothMonitor bluetoothMonitor;
    private RelativeLayout layout;


    private Intent intent;


    //mouse movements
    private int oldx = 0;
    private int oldy = 0;
    private int newx = 0;
    private int newy = 0;
    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothMonitor =  BluetoothMonitor.getBluetoothMonitor(getApplicationContext(),this);
        bluetoothMonitor.mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        tomouse = (Button) findViewById(R.id.tomouse);

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(bluetoothMonitor.mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(bluetoothMonitor.mDeviceClickListener);

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        if (bluetoothMonitor.mBTArrayAdapter == null) {
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothMonitor.bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothMonitor.bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    bluetoothMonitor.listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                   bluetoothMonitor.discover(v);
                }
            });

            final Activity activity = this;

            tomouse.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mScanBtn.setVisibility(View.INVISIBLE);
                    mOffBtn.setVisibility(View.INVISIBLE);
                    mListPairedDevicesBtn.setVisibility(View.INVISIBLE);
                    mDiscoverBtn.setVisibility(View.INVISIBLE);
                    tomouse.setVisibility(View.INVISIBLE);
                    mDevicesListView.setVisibility(View.INVISIBLE);



                    layout = (RelativeLayout) findViewById(R.id.main);
                    layout.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(result) {
                                bluetoothMonitor.mConnectedThread.write(11111111 + "\r\n");
                                bluetoothMonitor.mConnectedThread.write(111111 + "\r\n");
                            }

                        }
                    });

                    layout.setOnTouchListener(new View.OnTouchListener() {


                        @Override
                        public boolean onTouch(View v, MotionEvent e) {
                            return speedDetection(e);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (Math.round(e.getX()));
        int y = (Math.round(e.getY()));
        //if (x > 0 && y > 0 && x < 1350 && y < 780) {
        setValue(x, y);
        //}
        //Toast.makeText(context,String.valueOf(getex()),Toast.LENGTH_SHORT).show();
        return true;
    }

    String code;
    String code2;
    public void setValue(int p,int q){


        if(bluetoothMonitor.mConnectedThread != null) { //First check to make sure thread created
            code = String.valueOf(p);
            code2 = String.valueOf(q);
            if (pointerCount == 1) {
                bluetoothMonitor.mConnectedThread.write(code + "\r\n");
                bluetoothMonitor.mConnectedThread.write(code2 + "\r\n");

            }else if(pointerCount > 1){
                bluetoothMonitor.mConnectedThread.write(111 + "\r\n");
                bluetoothMonitor.mConnectedThread.write(code2 + "\r\n");
            }
        }

    }




    private static final String DEBUG_TAG = "Velocity";
    private int x = 0;
    private int y = 0;
    float clickx = 0.0f;
    private float clicky = 0.0f;
    private float releasex;
    private float releasey;
    private boolean result;
    private int pointerCount;
    private VelocityTracker mVelocityTracker = null;
    public boolean speedDetection(MotionEvent event){
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        result = false;
        newx= Math.round(event.getX());
        newy = Math.round(event.getY());

        pointerCount = event.getPointerCount();
        Log.d("result -------->>>>>>>",String.valueOf(event.getPointerCount()));

        switch(action) {

            case MotionEvent.ACTION_DOWN:

                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the
                    // velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);

                clickx = x;
                clicky = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                mVelocityTracker.computeCurrentVelocity(100);


                int i = (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                        pointerId);
                int j = (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                        pointerId);

                if (pointerCount >1){
                    setValue(i,j);
                    break;
                }
                x+=i;
                y+=j;
                if (x<0){
                    x=0;
                }else if(x>1350){
                    x=1350;
                }

                if (y<0){
                    y=0;
                }else if(y>800){
                    y=800;
                }



                oldx = newx;

                if(!result){
                    setValue(x,y);
                }
                isFirst = true;


                break;
            case MotionEvent.ACTION_UP:
                releasex = x;
                releasey = y;
                result = ((clickx - releasex) == 0.0f && (clicky-releasey)==0.0f);
                if(!result){
                    setValue(x,y);
                }
                Log.d("diff-x -------->>>>>>>",String.valueOf(clickx-releasex));
                Log.d("diff-y -------->>>>>>>",String.valueOf(clicky-releasey));
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                //mVelocityTracker.recycle();
                //break;
        }
        return false;
    }
}

