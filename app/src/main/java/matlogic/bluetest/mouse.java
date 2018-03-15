package matlogic.bluetest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Haran on 19/01/2018.
 */

public class mouse extends View {
    private Context context;
    private int x;
    private int y;
    private BluetoothMonitor bluetoothMonitor = BluetoothMonitor.getBluetoothMonitor();


    public mouse(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public mouse (Context context){
        super(context);
        this.context= context;
    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {
        x = (Math.round(e.getX())-200)*4;
        y = (Math.round(e.getY())-800)*2;
        if (x > 0 && y > 0 && x < 1350 && y < 780) {
            setValue(x, y);
        }
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


    public int getex() {
        return x;
    }

    public int getwhy(){
        return y;
    }
}
