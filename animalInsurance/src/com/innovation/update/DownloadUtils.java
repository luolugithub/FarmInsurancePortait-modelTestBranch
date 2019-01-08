package com.innovation.update;



import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class DownloadUtils {
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int DATA_TIMEOUT = 40000;
    private final static int DATA_BUFFER = 8192;



    public interface DownloadListener {
        public void downloading(int progress);
        public void downloaded();
    }

    public static long download(String urlStr, File dest, boolean append, DownloadListener downloadListener) throws Exception {
        int downloadProgress = 0;
        long remoteSize = 0;
        int currentSize = 0;
        long totalSize = -1;

        if (!append && dest.exists() && dest.isFile()) {
            dest.delete();
        }

        if (append && dest.exists() && dest.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dest);
                currentSize = fis.available();
            } catch (IOException e) {
                throw e;
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }


        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        InputStream is = null;


        if (connection.getResponseCode() == 200) {
            int fileSize = connection.getContentLength() / 1024;
            Log.e("------->","fileSize"+fileSize);
            int totla = 0;
            is = connection.getInputStream();
            remoteSize = connection.getContentLength();

            FileOutputStream os = new FileOutputStream(dest, append);
            byte buffer[] = new byte[DATA_BUFFER];
            int readSize = 0;
            int temp=0;
            while ((readSize = is.read(buffer)) > 0) {
                os.write(buffer, 0, readSize);
                os.flush();
                totalSize += readSize;
                if (downloadListener != null) {
                    downloadProgress = (int) (totalSize * 100 / remoteSize);
                    if(downloadProgress>=temp){
                        temp++;
                        downloadListener.downloading(downloadProgress);
                    }
                }
            }
            if (totalSize < 0) {
                totalSize = 0;
            }
        }

        if (totalSize < 0) {
            throw new Exception("Download file fail: " + urlStr);
        }

        if (downloadListener != null) {
            downloadListener.downloaded();
        }



        return totalSize;
    }
}
