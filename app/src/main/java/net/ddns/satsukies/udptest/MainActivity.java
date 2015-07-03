package net.ddns.satsukies.udptest;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.System.*;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    EditText editIp, editPort;

    Button btnIp, btnUp, btnDown, btnRight, btnLeft;

    RadioGroup mRadioGroup;
    RadioButton radio1p, radio2p;

    int playerId = 0;

    //Processingに合わせてbyte型にする
    byte[] sendBuffer = new byte[8];

    DatagramSocket mSocket;
    DatagramPacket mPacket;

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String remoteIP = "192.168.43.255";
        final int portNum = 12345;

        editIp = (EditText) findViewById(R.id.edit_ip);
        editPort = (EditText) findViewById(R.id.edit_port);

        btnUp = (Button) findViewById(R.id.up);
        btnUp.setOnClickListener(this);

        btnDown = (Button) findViewById(R.id.down);
        btnDown.setOnClickListener(this);

        btnRight = (Button) findViewById(R.id.right);
        btnRight.setOnClickListener(this);

        btnLeft = (Button) findViewById(R.id.left);
        btnLeft.setOnClickListener(this);

        btnIp = (Button) findViewById(R.id.btn_ip);
        btnIp.setOnClickListener(this);

        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);

        radio1p = (RadioButton)findViewById(R.id.radio1);
        radio2p =(RadioButton)findViewById(R.id.radio2);



        try {
            InetAddress host = InetAddress.getByName(remoteIP);      // IPアドレス
            String message = "Initialize";  // 送信メッセージ
            mSocket = new DatagramSocket();  //DatagramSocket 作成
            byte[] data = message.getBytes();
            mPacket = new DatagramPacket(data, data.length, host, portNum);  //DatagramPacket 作成
        } catch (Exception e) {
            err.println("Exception : " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //intをbyte-arrayに変える
    byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)
        };
    }

    void sendMessage(final String ip, final int port, final int code, final int player) {
        // UDP　送信
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress host = InetAddress.getByName(ip);
                    mSocket = new DatagramSocket();  //DatagramSocket 作成

                    byte[] tmpArray;
                    tmpArray = intToByteArray(code);
                    arraycopy(tmpArray, 0, sendBuffer, 0, 4);
                    tmpArray = intToByteArray(player);
                    System.arraycopy(tmpArray, 0, sendBuffer, 4, 4);

                    mPacket = new DatagramPacket(sendBuffer, sendBuffer.length, host, port);  //DatagramPacket 作成
                    mSocket.send(mPacket);
                } catch (Exception e) {
                    err.println("Exception : " + e);
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        int keyCode = 0;
        //Buttonが押されたときの挙動
        switch (v.getId()) {
            case R.id.btn_ip:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        editIp.setFocusable(false);
                        editPort.setFocusable(false);
                    }
                });
                return;
            case R.id.up:
                keyCode = 1;
                break;
            case R.id.down:
                keyCode = 2;
                break;
            case R.id.right:
                keyCode = 3;
                break;
            case R.id.left:
                keyCode = 4;
                break;
            default:
                keyCode = 0;
                break;
        }

        try {
            sendMessage(editIp.getText().toString().equals("") ? "192.168.11.33" : editIp.getText().toString(), editPort.getText().toString().equals("") ? 12345 : Integer.parseInt(editPort.getText().toString()), keyCode, playerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton btn = (RadioButton)findViewById(checkedId);
        Toast.makeText(getApplicationContext(), "selected:" + btn.getText(), Toast.LENGTH_SHORT).show();

        switch (checkedId){
            case R.id.radio1:
                playerId = 1;
                break;
            case R.id.radio2:
                playerId = 2;
                break;
            default:
                playerId = 0;
                break;
        }
    }
}
