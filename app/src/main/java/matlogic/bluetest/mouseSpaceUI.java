package matlogic.bluetest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class mouseSpaceUI extends Activity {

    private mouse mouseControl;
    private BluetoothMonitor bluetoothMonitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse_space_ui);

        //mouseControl =  (mouse) findViewById(R.id.chart);
        bluetoothMonitor = BluetoothMonitor.getBluetoothMonitor();


    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (Math.round(e.getX())-200)*4;
        int y = (Math.round(e.getY())-800)*2;
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
            bluetoothMonitor.mConnectedThread.write(code + "\r\n");
            bluetoothMonitor.mConnectedThread.write(code2 + "\r\n");
        }

    }


}
