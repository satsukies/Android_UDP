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
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.System.*;


public class MainActivity extends ActionBarActivity {

    Button btn1, btn2;
    TextView txt1, txt2;
    SurfaceView mSurfaceView;

    //Processingに合わせてbyte型にする
    byte[] sendBuffer = new byte[8];

    int n;
    DatagramSocket mSocket;
    DatagramPacket mPacket;

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);

        mSurfaceView = (SurfaceView) findViewById(R.id.touch);

        final String remoteIP = "192.168.43.255";
        final int portNum = 12345;

        // Button1 がクリックされた時に呼び出されるコールバックを登録
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n++;
                txt1.setText(n + "");

                sendMessage(remoteIP, portNum, 100, 200);

            }
        });

        // カウンタの減少
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n--;
                txt1.setText(n + "");
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txt1.setText((int) event.getX() + ":" + (int) event.getY());
                sendMessage(remoteIP, portNum, (int) event.getX(), (int) event.getY());
                return true;
            }
        });

        n = 0;        // カウント値の初期値
        try {
            InetAddress host = InetAddress.getByName(remoteIP);      // IPアドレス
            String message = "send by Android";  // 送信メッセージ
            mSocket = new DatagramSocket();  //DatagramSocket 作成
            byte[] data = message.getBytes();
            mPacket = new DatagramPacket(data, data.length, host, portNum);  //DatagramPacket 作成
            txt2.setText("初期化が完了しました");
        } catch (Exception e) {
            err.println("Exception : " + e);
            txt2.setText("初期化に失敗しました");
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

    void sendMessage(final String ip, final int port, final int x, final int y) {
        // UDP　送信
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress host = InetAddress.getByName(ip);
                    mSocket = new DatagramSocket();  //DatagramSocket 作成

                    byte[] tmpArray;
                    tmpArray = intToByteArray(x);
                    arraycopy(tmpArray, 0, sendBuffer, 0, 4);
                    tmpArray = intToByteArray(y);
                    System.arraycopy(tmpArray, 0, sendBuffer, 4, 4);

                    mPacket = new DatagramPacket(sendBuffer, sendBuffer.length, host, port);  //DatagramPacket 作成
                    mSocket.send(mPacket);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            txt2.setText("送信完了しました");
                        }
                    });
                } catch (Exception e) {
                    err.println("Exception : " + e);
                    txt2.setText("送信失敗しました");
                }
            }
        }).start();

    }
}
