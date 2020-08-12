package kastolom.example.geoloc;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeolocationService extends Service {

    private LocationManager locationManager;
    private LatTopServer mServer = null;

    public GeolocationService() {
    }

    @Override
    public void onCreate(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        return START_STICKY;
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            SendData(location.getLatitude(), location.getLongitude(), location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void SendData(final double latitude, final double longitude , final Location location) {
        mServer = new LatTopServer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServer.OpenConnection();
                } catch (Exception e) {
                    Log.e(LatTopServer.LOG_TAG, e.getMessage());
                }
//Соединение
                try {
                    String lat = Double.toString(latitude);
                    String lon = Double.toString(longitude);
                    String message = lat + " " + lon + " " + "end";
                    mServer.SendData(message.getBytes());

                } catch (Exception e) {
                    Log.e(LatTopServer.LOG_TAG, e.getMessage());
                }
//Отправка
                mServer.closeConnection();
//Закрытие соединения
            }
        }).start();
    }
}
