package net.ddns.satsukies.udptest;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainActivity extends ActionBarActivity {

    Button btn1, btn2;
    TextView txt1, txt2;

    StringBuffer mBuffer = new StringBuffer(128);
    int n;
    DatagramSocket mSocket;
    DatagramPacket mPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler mHandler = new Handler();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);

        final String remoteIP = "192.168.11.255";
        final int portNum = 12345;

        // Button1 がクリックされた時に呼び出されるコールバックを登録
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n++;
                // 文字列を作る
                mBuffer.append(n);
                txt1.setText(mBuffer.toString());  // tv には final が必要
                mBuffer.delete(0, 99);
                //toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);

                // UDP　送信
                (new Thread(new Runnable() {
                    public void run() {
                        try {
                            InetAddress host = InetAddress.getByName(remoteIP);
                            //String message = "send by Android " + n + " \n";  // 送信メッセージ
                            String message = "100 100";  // 送信メッセージ
                            mSocket = new DatagramSocket();  //DatagramSocket 作成
                            byte[] data = message.getBytes();
                            mPacket = new DatagramPacket(data, data.length, host, portNum);  //DatagramPacket 作成
                            mSocket.send(mPacket);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    txt2.setText("送信完了しました");
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Exception : " + e);
                            txt2.setText("送信失敗しました");
                        }
                    }
                })).start();
            }
        });

        // カウンタの減少
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n--;
                mBuffer.append(n);
                txt1.setText(mBuffer.toString());
                mBuffer.delete(0, 99);
                //toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
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
            System.err.println("Exception : " + e);
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
}
