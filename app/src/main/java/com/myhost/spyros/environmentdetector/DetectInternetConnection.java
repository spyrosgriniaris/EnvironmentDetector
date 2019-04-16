package com.myhost.spyros.environmentdetector;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DetectInternetConnection extends AsyncTask<Void,Void,Boolean> {

    public interface Consumer {
        void checkInternetConnection(boolean hasInternetConnection);
    }

    Consumer consumer;

    public DetectInternetConnection(Consumer consumer) {
        this.consumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com",80),1500);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internetConnection) {
        super.onPostExecute(internetConnection);
        consumer.checkInternetConnection(internetConnection);
    }
}