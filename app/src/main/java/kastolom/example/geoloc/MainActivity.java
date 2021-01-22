package kastolom.example.geoloc;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;
    TextView tvPort;
    TextView tvUser;
    TextView tvIPadress;
    CheckBox cbStartService;
    Button send;

    SharedPreferences myPreferences;

    private LatTopServer mServer = null; //Изменения для UDP

    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
        tvPort = (TextView) findViewById(R.id.tvPort);
        tvUser = (TextView) findViewById(R.id.tvUser);
        tvIPadress = (TextView) findViewById(R.id.tvIPAdress);
        cbStartService = (CheckBox) findViewById(R.id.checkBox);
        send = (Button) findViewById(R.id.send);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LoadSetting();
    }

    @SuppressLint("MissingPermission")
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
            //SaveSetting(tvIPadress.getText().toString(), Integer.parseInt(tvPort.getText().toString()), tvUser.getText().toString());
            mServer = new LatTopServer(tvIPadress.getText().toString(), Integer.parseInt(tvPort.getText().toString()), tvUser.getText().toString());
            mServer.SendData(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(location.getTime());
        String coordinate = String.format("Coordinates: lat = %1$.8f, lon = %2$.8f", location.getLatitude(), location.getLongitude());
        return time + " " + coordinate;
    }

    public void ServiceStart(View view){
        Intent i=new Intent(this, GeolocationService.class);
        startService(i);
    }

    //Преобразуем тип double в массив из 8 байт

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void SaveSetting(View view){
        //myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        myPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("IP", tvIPadress.getText().toString());
        myEditor.putInt("PORT", Integer.parseInt(tvPort.getText().toString()));
        myEditor.putString("NAME", tvUser.getText().toString());
        myEditor.commit();
        Toast.makeText(this, "Данные сохранены!", Toast.LENGTH_SHORT).show();
    }

    public void  LoadSetting(){
        myPreferences = getPreferences(MODE_PRIVATE);
        String ip = myPreferences.getString("IP", "194.158.216.130");
        tvIPadress.setText(ip);
        int port = myPreferences.getInt("PORT", 8888);
        tvPort.setText(Integer.toString(port));
        String name = myPreferences.getString("NAME", "Пользователь");
        tvUser.setText(name);
        Toast.makeText(this, "Данные загружены!", Toast.LENGTH_SHORT).show();
    }

    public void CheckStartService(View view){
        myPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        if(cbStartService.isChecked()){
            myEditor.putBoolean("CBSERVICE", true);
        }
        else {
            myEditor.putBoolean("CBSERVICE", false);
        }
        myEditor.commit();
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

}
