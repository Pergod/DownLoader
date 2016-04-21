package com.geekband.work8;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button downLoadButton;
    private EditText mEditText;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private static final String GEEK_BAND="GeekBand";
    private static final String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downLoadButton= (Button) findViewById(R.id.download_button);
        mEditText=(EditText)findViewById(R.id.input_edit_text);
        mProgressBar= (ProgressBar) findViewById(R.id.dowload_progressBar);
        mTextView= (TextView) findViewById(R.id.progress_update_text_view);
        downLoadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_button:
                String value=String.valueOf(mEditText.getText());
                Log.i(TAG, value);
                String testUrl="http://study.163.com/pub/study-android-official.apk";
                StartDownload(testUrl);
                break;
        }
    }

    public void StartDownload(final String url) {
        new AsyncTask<String,Integer,Boolean>(){
            Boolean flag=true;
            @Override
            protected void onPreExecute() {
                Toast.makeText(MainActivity.this,"Start DownLoad",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    URL url=new URL(params[0]);
                    URLConnection urlConnection= url.openConnection();
                    int total=urlConnection.getContentLength();

                    Log.i(TAG, "the download file's content length: "+ total);

                    InputStream inputStream=urlConnection.getInputStream();
                    String downLoadFolderName= Environment.getExternalStorageDirectory()+ File.separator + GEEK_BAND + File.separator;

                    File file=new File(downLoadFolderName);
                    if (!file.exists()){
                        file.mkdir();
                    }
                    String fileName=downLoadFolderName+"download.apk";

                    File downFile=new File(fileName);
                    if (downFile.exists()){
                        downFile.delete();
                    }

                    Double downloadSize = 0.0;
                    byte[] bytes = new byte[1024];
                    int Length=0;

                    OutputStream outputStream=new FileOutputStream(fileName);

                    while ((Length=inputStream.read(bytes))!=-1){
                        outputStream.write(bytes,0,Length);
                        downloadSize += Length;
                        int progress = (int) (downloadSize * 100.0/ total);
                        publishProgress(progress);
                        Log.i(TAG, "download progress: "+ progress);
                    }
                    Log.i(TAG, "download success");

                    inputStream.close();
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "download failure");
                    flag=false;
                }
                return flag;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                mTextView.setText(values[0]+" % ");
                mProgressBar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean flag) {
                if (flag){
                    Toast.makeText(MainActivity.this,"DownLoad Success",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"DownLoad Failed",Toast.LENGTH_SHORT).show();
                    mTextView.setText("非法的URL，不支持下载");
                }
            }
        }.execute(url);
    }
}
