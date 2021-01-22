package kastolom.example.geoloc;

import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LatTopServer {

    private DatagramSocket mServer = null;

    public static final String LOG_TAG = "myServerAPP";
    private String mServername = "194.158.216.130";
    private int mServerPort = 8888;
    //private Socket mSocket = null;
    private String nameUser = "Пользователь";
    private String provider;

    public LatTopServer(String ip, int port, String name, String _provider) {
        mServername = ip;
        mServerPort = port;
        nameUser = name;
        provider =_provider;
    }

    public void SendData(final Location location) {
        //mServer = new LatTopServer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //try {
                    //mServer.OpenConnection();
                //} catch (Exception e) {
                 //   Log.e(LatTopServer.LOG_TAG, e.getMessage());
                //}
//Соединение
                try {
                    mServer = new DatagramSocket(); //Изменения для UDP
                    String lat = Double.toString(location.getLatitude());
                    String lon = Double.toString(location.getLongitude());
                    String message = nameUser + " " + lat + " " + lon + " " + provider;
                    byte[] b = message.getBytes(); //Изменения для UDP
                    DatagramPacket dp = new DatagramPacket(b , b.length , InetAddress.getByName(mServername) , mServerPort); //Изменения для UDP
                    mServer.send(dp); //Изменения для UDP

                } catch (Exception e) {
                    Log.e(LatTopServer.LOG_TAG, e.getMessage());
                }
//Отправка
                //mServer.closeConnection();
//Закрытие соединения
            }
        }).start();
    }
}
