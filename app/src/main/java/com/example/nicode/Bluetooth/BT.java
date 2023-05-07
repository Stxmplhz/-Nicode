package com.example.nicode.Bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.nicode.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BT extends AppCompatActivity implements OnSeekBarChangeListener {

    public String ultimodato;

    private static final String TAG = "bluetooth2";
    Handler h;
    final int RECEIVE_MESSAGE = 1;    //1    // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private final StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // MAC-address of Bluetooth module
    private static String address = "00:14:02:13:00:10";
    private long previousTime;
    private boolean to_send;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File folder = new File(Environment.getExternalStorageDirectory() + "/NicodeInfo"); //create folder
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        setContentView(R.layout.activity_getcoin);

        //READING FROM FILE OF THE MAC-ADDRESS TO CONNECT TO
        try{
            String MACFile = readFileAsString("/sdcard/MAC.txt");
            if(MACFile.equals("")){
                Toast.makeText(this,"\n" + "No device saved, go back to the previous step", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), GetCoinActivity.class);
                startActivity(i);
                finish();
            }else {
                address = MACFile;
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"\n" + "Data reading error" , Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), GetCoinActivity.class);
            startActivity(i);
            finish();
        }
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        Toast.makeText(getBaseContext(), strIncom, Toast.LENGTH_SHORT).show();
                        Log.d("INCOME", "INCOME: " + strIncom);

                        break;
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void SendMessageBT(View v){
        //SEND MESSAGE TO BLUETOOTH
        mConnectedThread.write("U");
    }
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        previousTime = System.currentTimeMillis();

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                if(System.currentTimeMillis() - previousTime > 10000){
                    previousTime = System.currentTimeMillis();
                    mConnectedThread.write("w");
                }

                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                ultimodato=message;
                boolean to_send = true;
                //connect();
                new connect_and_send().execute();
            }
        }
    }

    private class connect_and_send extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {  // Show only the dialog
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                btSocket.close();
                Log.d(TAG, "...ALERT: " + "CLOSED SOCKET" + "...");
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                Log.d(TAG, "...ALERT: " + "IMPOSSIBILE TO CLOSE SOCKET" + "...");
            }

            Log.d(TAG, "...onResume - try connect...");

            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            }

            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.d(TAG, "....Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Create Socket...");

            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            //RESEND LATEST DATA

            if (to_send == true){
                mConnectedThread.write(ultimodato);
                to_send = false;
            }

            previousTime = System.currentTimeMillis();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    public static String readFileAsString(String filePath) {

        String result = "";
        File file = new File(filePath);
        if ( file.exists() ) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                char current;
                while (fis.available() > 0) {
                    current = (char) fis.read();
                    result = result + current;

                }

            } catch (Exception e) {
                Log.d("TourGuide", e.toString());
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException ignored) {
                    }
            }

        }
        return result;
    }
}
