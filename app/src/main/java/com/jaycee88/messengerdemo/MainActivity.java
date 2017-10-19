package com.jaycee88.messengerdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Messenger mGetReplyMessenger = new Messenger(new MessageHandler());
    private Messenger mService;

    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_FROM_SERVICE:
                    Log.d(TAG, "received msg form service: msg = [" + msg.getData().getString(Constants.MSG_KEY) + "]");
                    Toast.makeText(MainActivity.this, "received msg form service: msg = [" + msg.getData().getString(Constants.MSG_KEY) + "]", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void bindService() {
        // 应用内跨进程通信
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        // 跨应用的进程间通信--隐式意图
//        intent = new Intent();
//        intent.setPackage("com.jaycee88.ipcservice");
//        intent.setAction("myMessenger");
    }

    public void unbindService() {
        unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            sendMessage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void sendMessage() {
        Message msg = Message.obtain(null, Constants.MSG_FROM_CLIENT);
        Bundle data = new Bundle();
        data.putString(Constants.MSG_KEY, "Hello! This is client.");
        msg.setData(data);
        msg.replyTo = mGetReplyMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
