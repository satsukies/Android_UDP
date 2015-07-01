package net.ddns.satsukies.udptest;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.util.Log;

/**
 * Created by satsukies on 15/07/01.
 * 参考：http://percy.hatenablog.com/entry/2015/01/15/072902
 */
public class UDPUtil {
    private DatagramSocket recieveSocket;
    private String recieveData;
    private String message;

    public static final int PORT_NUM = 50000;

    //実行には別スレッドを立てる
    public String getMessage() {
        Thread mThread = new Thread() {
            public void run() {
                //PORT_NUM番号のポートを監視するソケットを生成
                try {
                    recieveSocket = new DatagramSocket(PORT_NUM);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                //受け付けるデータバッファとUDPパケットの作成
                byte recieveBuffer[] = new byte[1024];

                DatagramPacket recievePacket = new DatagramPacket(recieveBuffer, recieveBuffer.length);

                try {
                    recieveSocket.receive(recievePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                message = new String(recievePacket.getData(), 0, recievePacket.getLength());
                Log.d("Debug", message);
            }
        };

        mThread.start();

        try {
            mThread.join();
        } catch (InterruptedException e) {
            Log.d("Debug", e.toString());
        }
        return message;
    }

    //実行するときには別スレッドを立てよう
    public void sendMessage(String message) {
        //ソケット生成。自分のIP(ほんとは送信先ね),指定のportでgo
        InetSocketAddress remoteAddress = new InetSocketAddress("192.168.11.3", PORT_NUM);

        //パケットの中身
        byte[] sendBuffer = message.getBytes();

        //パケットそのもの
        DatagramPacket sendPacket;

        try {
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, remoteAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
