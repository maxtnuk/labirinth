package com.Daedalus.game2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class Blue_task extends AsyncTask<Void,Void,Void>{

    //BluetoothAdapter
    BluetoothAdapter mBluetoothAdapter;
    private UUID myuuid= UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
    private UUID control_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private Activity mActivity;
    private String myName = myuuid.toString();
    private byte delimiter='@';
    private String control_blue="20:16:09:21:36:45";
    private int START_UNITY=11;
    private int REQUEST_ENABLE_BT=20;

    public BluetoothSPP btspp=new BluetoothSPP(context);
    public boolean exit= false;

    //블루투스 요청 액티비티 코드

    //UI
    //ListView listPaired;
    //ListView listDevice;


    public Blue_task(Context context) {
        this.context = context;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        mActivity=((Activity) context);
    }
    public void connect(){
        btspp.setupService();
        btspp.startService(BluetoothState.DEVICE_ANDROID);
        btspp.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                //btspp.send("raspberry@",true);
            }

            @Override
            public void onDeviceDisconnected() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"유저가 나가셨습니다",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDeviceConnectionFailed() {

            }
        });
        btspp.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            @Override
            public void onAutoConnectionStarted() {

            }

            @Override
            public void onNewConnection(final String name, String address) {
            }
        });
        btspp.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                ArrayList<Byte> readBuffer = new ArrayList<>() ;
                try {
                    for(Byte b : data) {
                        if(b == delimiter) {
                            byte[] encodedBytes = new byte[readBuffer.size()];
                            for(int count=0;count<readBuffer.size();count++){
                                encodedBytes[count]=readBuffer.get(count);
                            }
                            final String str_data = new String(encodedBytes, "utf-8");
                            readBuffer.clear();

                            parseString(str_data);
                        }
                        else {
                            readBuffer.add(b);
                        }
                    }
                }
                catch (IOException ex) {
                    mActivity.finish();
                }
            }
        });
        btspp.autoConnect("");
    }
    public void parseString(final String str){
        try {
            JSONObject content=new JSONObject(str);
            if(!content.isNull("shop")){
                JSONObject shop_content= content.getJSONObject("shop");
                for(String item: shop_con.shop_list){
                    if(!shop_content.isNull(item)){
                        final String Item=item;
                        final int percent=shop_content.getInt(Item);
                        if(item.equals("hp")){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,Item+" "+percent+"% 접수 되었습니다",Toast.LENGTH_SHORT).show();
                                }
                            });
                            MainActivity.hp=shop_content.getInt(item);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(Void... voids) {
        if(btspp.isBluetoothAvailable()) {
           if(btspp.isBluetoothEnabled()){
               connect();
           }else{
               mActivity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),REQUEST_ENABLE_BT);
           }
        }else{

        }
        return null;
    }
}