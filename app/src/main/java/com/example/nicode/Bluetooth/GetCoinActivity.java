package com.example.nicode.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.MainActivity;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.InsideCommunity.ChatMessage;
import com.example.nicode.databinding.ActivityGetcoinBinding;
import com.example.nicode.databinding.FragmentRecentchatBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GetCoinActivity extends AppCompatActivity {

    private ActivityGetcoinBinding binding;

    private FirebaseFirestore Fdatabase;
    private PreferenceManager preferenceManager;

    private String userID;
    int Number_stopRoll_now,Number_stopRoll_want,Number_stopRoll_today,Number_stopRoll_new_now,DayfromStart,DayfromStart_new,nicoCoin,nicocoin_new,smokinglyzer,StopRoll_want,StopRoll_now,smokinglyzer_all,smokinglyzer_new,
    dataFromBluetooth;

    ImageView listen;
    ImageView listDevices;
    ListView listView;
    TextView status;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "Nicode";
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetcoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.nicocoinInbar.setText(String.valueOf(preferenceManager.getInt(Constants.KEY_NICOCOIN)));
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listen = binding.ConnectBluetooth;
        listView = binding.listview;
        status = binding.status;
        listDevices= binding.listDevices;
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }

    private void implementListeners() {

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("กดรายชื่ออุปกรณ์ เพื่อเชื่อมต่อBluetooth");
            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            preferenceManager = new PreferenceManager(getApplicationContext());
            Fdatabase = FirebaseFirestore.getInstance();
            userID = preferenceManager.getString(Constants.KEY_USER_ID);
            DocumentReference NicodeWatchUpdateRef = Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(Constants.KEY_USER_ID);

            StopRoll_now = preferenceManager.getInt(Constants.KEY_CIGARETTESTOPROLL);
            StopRoll_want = preferenceManager.getInt(Constants.KEY_CIGARETTEROLLPERMONTH);
            Number_stopRoll_want = StopRoll_want / 31;
            DayfromStart = preferenceManager.getInt(Constants.KEY_DAYFROMSTART);
            nicoCoin = preferenceManager.getInt(Constants.KEY_NICOCOIN);
            //smokinglyzer_all = preferenceManager.getInt(Constants.KEY_SMOKINGLYZER);

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("กดรายชื่ออุปกรณ์ เพื่อเชื่อมต่อBluetooth");
                    break;
                case STATE_CONNECTING:
                    status.setText("กำลังเชื่อมต่อ...");
                    break;
                case STATE_CONNECTED:
                    status.setText("เเอปเชื่อมต่อกับนาฬิกาเเล้ว");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("เชื่อมต่อล้มเหลว โปรดลองใหม่อีกครั้ง");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    int dataFromBluetooth = Integer.parseInt(tempMsg);

                    if (dataFromBluetooth == 0) {
                        Toast.makeText(GetCoinActivity.this, "คุณไม่สูบบุหรี่เลยในวันนี้", Toast.LENGTH_LONG).show();

                        Number_stopRoll_today = Number_stopRoll_want;
                        Number_stopRoll_new_now = StopRoll_now + Number_stopRoll_today;
                        DayfromStart_new = DayfromStart + 1;
                        nicocoin_new = nicoCoin + 10;

                        NicodeWatchUpdateRef.update(Constants.KEY_NICOCOIN, FieldValue.increment(10));
                        NicodeWatchUpdateRef.update(Constants.KEY_CIGARETTESTOPROLL, FieldValue.increment(Number_stopRoll_today));
                        NicodeWatchUpdateRef.update(Constants.KEY_DAYFROMSTART, FieldValue.increment(1));

                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Number_stopRoll_new_now);
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART, DayfromStart_new);
                        preferenceManager.putInt(Constants.KEY_NICOCOIN, nicocoin_new);

                        binding.textClaimCoin.setText("คุณไม่สูบบุหรี่เลย วันนี้คุณจึงได้รับ 10 Nico Coin คุณเก่งมาก เป้าหมายคุณอยู่ไม่เกินเอื้อม! :D");
                        binding.textClaimCoin.setVisibility(View.VISIBLE);
                        binding.claimedImageShadow.setVisibility(View.GONE);
                    } else if (dataFromBluetooth == 1) {
                        Number_stopRoll_today = Number_stopRoll_want - dataFromBluetooth;
                        Number_stopRoll_new_now = StopRoll_now + Number_stopRoll_today;
                        DayfromStart_new = DayfromStart + 1;
                        nicocoin_new = nicoCoin + 7;

                        NicodeWatchUpdateRef.update(Constants.KEY_NICOCOIN, FieldValue.increment(7));
                        NicodeWatchUpdateRef.update(Constants.KEY_CIGARETTESTOPROLL, FieldValue.increment(Number_stopRoll_today));
                        NicodeWatchUpdateRef.update(Constants.KEY_DAYFROMSTART, FieldValue.increment(1));

                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Number_stopRoll_new_now);
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART, DayfromStart_new);
                        preferenceManager.putInt(Constants.KEY_NICOCOIN, nicocoin_new);

                        binding.textClaimCoin.setText("คุณสูบบุหรี่เพียง 1 ครั้ง วันนี้คุณจึงได้รับ 7 Nico Coin คุณเก่งมาก เป้าหมายคุณอยู่ไม่เกินเอื้อม! :D");
                        binding.textClaimCoin.setVisibility(View.VISIBLE);
                        binding.textWarningYellow.setText("ระวังเกี่ยวกับควันบุหรี่รอบตัวของคุณซึ่งอาจส่งผลในการได้รับ Nico Coin ของคุณได้");
                        binding.textWarningYellow.setVisibility(View.VISIBLE);
                        binding.claimedImageShadow.setVisibility(View.GONE);
                        binding.warningYellowShadow.setVisibility(View.GONE);

                        Toast.makeText(GetCoinActivity.this, "คุณสูบบุหรี่ 1 ครั้งในวันนี้", Toast.LENGTH_LONG).show();
                    } else if (dataFromBluetooth == 2) {
                        Number_stopRoll_today = Number_stopRoll_want - dataFromBluetooth;
                        Number_stopRoll_new_now = StopRoll_now + Number_stopRoll_today;
                        DayfromStart_new = DayfromStart + 1;
                        nicocoin_new = nicoCoin + 5;

                        NicodeWatchUpdateRef.update(Constants.KEY_NICOCOIN, FieldValue.increment(5));
                        NicodeWatchUpdateRef.update(Constants.KEY_CIGARETTESTOPROLL, FieldValue.increment(Number_stopRoll_today));
                        NicodeWatchUpdateRef.update(Constants.KEY_DAYFROMSTART, FieldValue.increment(1));

                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Number_stopRoll_new_now);
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART, DayfromStart_new);
                        preferenceManager.putInt(Constants.KEY_NICOCOIN, nicocoin_new);

                        binding.textClaimCoin.setText("คุณสูบบุหรี่เพียง 2 ครั้ง วันนี้คุณจึงได้รับ 5 Nico Coin คุณเก่งมาก เป้าหมายคุณอยู่ไม่เกินเอื้อม! :D");
                        binding.textClaimCoin.setVisibility(View.VISIBLE);
                        binding.textWarningYellow.setText("ระวังเกี่ยวกับควันบุหรี่รอบตัวของคุณซึ่งอาจส่งผลในการได้รับ Nico Coin ของคุณได้");
                        binding.textWarningYellow.setVisibility(View.VISIBLE);
                        binding.claimedImageShadow.setVisibility(View.GONE);
                        binding.warningYellowShadow.setVisibility(View.GONE);

                        Toast.makeText(GetCoinActivity.this, "คุณสูบบุหรี่ 2 ครั้งในวันนี้", Toast.LENGTH_LONG).show();
                    } else if (dataFromBluetooth == 3) {
                        Number_stopRoll_today = Number_stopRoll_want - dataFromBluetooth;
                        Number_stopRoll_new_now = StopRoll_now + Number_stopRoll_today;
                        DayfromStart_new = DayfromStart + 1;
                        nicocoin_new = nicoCoin + 3;

                        NicodeWatchUpdateRef.update(Constants.KEY_NICOCOIN, FieldValue.increment(3));
                        NicodeWatchUpdateRef.update(Constants.KEY_CIGARETTESTOPROLL, FieldValue.increment(Number_stopRoll_today));
                        NicodeWatchUpdateRef.update(Constants.KEY_DAYFROMSTART, FieldValue.increment(1));

                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Number_stopRoll_new_now);
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART, DayfromStart_new);
                        preferenceManager.putInt(Constants.KEY_NICOCOIN, nicocoin_new);

                        binding.textClaimCoin.setText("คุณสูบบุหรี่เพียง 3 ครั้ง วันนี้คุณจึงได้รับ 3 Nico Coin คุณพยายามได้ดี เป้าหมายคุณอยู่ไม่เกินเอื้อม! :D");
                        binding.textClaimCoin.setVisibility(View.VISIBLE);
                        binding.textWarningYellow.setText("ระวังเกี่ยวกับควันบุหรี่รอบตัวของคุณซึ่งอาจส่งผลในการได้รับ Nico Coin ของคุณได้");
                        binding.textWarningYellow.setVisibility(View.VISIBLE);
                        binding.claimedImageShadow.setVisibility(View.GONE);
                        binding.warningYellowShadow.setVisibility(View.GONE);

                        Toast.makeText(GetCoinActivity.this, "คุณสูบบุหรี่ 3 ครั้งในวันนี้", Toast.LENGTH_LONG).show();
                    } else if (dataFromBluetooth > 3 || dataFromBluetooth <100) {
                        Number_stopRoll_today = Number_stopRoll_want - dataFromBluetooth;
                        if (Number_stopRoll_today < 0){
                            Number_stopRoll_today = 0;
                            Number_stopRoll_new_now = 0;
                        } else if (Number_stopRoll_today > 0){
                            Number_stopRoll_new_now = StopRoll_now + Number_stopRoll_today;
                        }
                        DayfromStart_new = DayfromStart + 1;

                        NicodeWatchUpdateRef.update(Constants.KEY_CIGARETTESTOPROLL, FieldValue.increment(Number_stopRoll_today));
                        NicodeWatchUpdateRef.update(Constants.KEY_DAYFROMSTART, FieldValue.increment(1));

                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Number_stopRoll_new_now);
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART, DayfromStart_new);

                        binding.textWarningRed.setText("คุณสูบบุหรี่ทั้งหมด "+ dataFromBluetooth +" ครั้ง ซึ่งเกิน 3 ครั้ง คุณจึงไม่ได้รับNico Coin พยายามใหม่นะ เราเป็นกำลังใจให้คุณอยู่ข้างๆ :)");
                        binding.textWarningRed.setVisibility(View.VISIBLE);
                        binding.textWarningYellow.setText("ระวังเกี่ยวกับควันบุหรี่รอบตัวของคุณซึ่งอาจส่งผลในการได้รับ Nico Coin ของคุณได้");
                        binding.textWarningYellow.setVisibility(View.VISIBLE);
                        binding.warningRedImageShadow.setVisibility(View.GONE);
                        binding.warningYellowShadow.setVisibility(View.GONE);

                        Toast.makeText(GetCoinActivity.this, "คุณสูบบุหรี่มากกว่า 3 ครั้งในวันนี้", Toast.LENGTH_LONG).show();
                    } else if (dataFromBluetooth > 100){
                        smokinglyzer = smokinglyzer_all - dataFromBluetooth;
                        if (smokinglyzer < 0){
                            smokinglyzer_new = -smokinglyzer;
                            preferenceManager.putInt(Constants.KEY_SMOKINGLYZER,smokinglyzer_new);
                            NicodeWatchUpdateRef.update(Constants.KEY_SMOKINGLYZER, FieldValue.increment(smokinglyzer_new));

                        }else{
                            smokinglyzer_new = smokinglyzer_all - smokinglyzer;
                            preferenceManager.putInt(Constants.KEY_SMOKINGLYZER,smokinglyzer_new);
                            NicodeWatchUpdateRef.update(Constants.KEY_SMOKINGLYZER, FieldValue.increment(-smokinglyzer));
                        }

                        Toast.makeText(GetCoinActivity.this, "ได้รับค่าปอดของคุณในวันนี้เเล้ว", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(GetCoinActivity.this, "เกิดข้อผิดพลาดในการรับข้อมูล โปรดลองใหม่อีกครั้ง", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
            return true;
        }
    });

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}