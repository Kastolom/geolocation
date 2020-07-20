package kastolom.example.geoloc;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class LatTopServer {
    public static final String LOG_TAG = "myServerAPP";
    private String mServername = "194.158.216.130";
    private int mServerPort = 8005;
    private Socket mSocket = null;

    public LatTopServer() {
    }

    public void OpenConnection() throws Exception, IOException {
        //Перед открытием освобождаем ресурсы
        closeConnection();

        try {
            mSocket = new Socket(mServername, mServerPort);
        } catch (IOException e) {
            throw new Exception("Невозможно создать сокет: " + e.getMessage());
        }

    }

    public void closeConnection() {

        if(mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Невозможно закрыть соккет: " + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }

    public void SendData(byte [] data) throws  Exception {
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Невозможно отправить данные. Сокет не создан или закрыт!");
        }

        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();

        } catch (IOException e) {
            throw new Exception("Невозможно отправить данные" + e.getMessage() );
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
