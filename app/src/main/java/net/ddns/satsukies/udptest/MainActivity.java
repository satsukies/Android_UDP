package net.ddns.satsukies.udptest;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.arraycopy;
import static java.lang.System.err;


public class MainActivity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.edit_ip) EditText editIp;
    @Bind(R.id.edit_port) EditText editPort;

    @Bind(R.id.btn_ip) Button btnIp;
    @Bind(R.id.up) Button btnUp;
    @Bind(R.id.down) Button btnDown;
    @Bind(R.id.right) Button btnRight;
    @Bind(R.id.left) Button btnLeft;

    @Bind(R.id.radioGroup) RadioGroup mRadioGroup;
    @Bind(R.id.radio1) RadioButton radio1p;
    @Bind(R.id.radio2) RadioButton radio2p;

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

        ButterKnife.bind(this);

        final String remoteIP = "192.168.43.255";
        final int portNum = 12345;

        mRadioGroup.setOnCheckedChangeListener(this);

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

    @OnClick({R.id.btn_ip, R.id.up, R.id.down, R.id.right, R.id.left})
    public void bindButton(View v) {
        Toast.makeText(getApplicationContext(), "onClick", Toast.LENGTH_SHORT).show();
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
