package kastolom.example.geoloc;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GeolocationService extends Service {

    private LocationManager locationManager;
    private LatTopServer mServer = null;
    SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(GeolocationService.this);
    SharedPreferences.Editor myEditor = myPreferences.edit();

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

            mServer = new LatTopServer(myPreferences.getString("IP", "194.158.216.130"), myPreferences.getInt("PORT", 8888), myPreferences.getString("NAME", "Пользователь"));
            mServer.SendData(location);
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
}
