package com.example.dlsgh.myapplication3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.Buffer;


/**
 * 음성 녹음을 하는 방법에 대해 알 수 있습니다.
 *
 * @author Mike
 *
 */


public class MainActivity extends AppCompatActivity {


    private static String RECORDED_FILE;
    AudioTrack audioTrack;
    MediaPlayer player;
    MediaRecorder recorder;
    short[] CorrShort;
    byte generatedSnd[];
    short[] Buffer = null;
    short[] Buffer_original = null;


    boolean keepGoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File sdcard = Environment.getExternalStorageDirectory();

        File file = new File(sdcard, "recorded.wav");
        RECORDED_FILE = file.getAbsolutePath();

        Button recordBtn = (Button) findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
        Button playBtn = (Button) findViewById(R.id.playBtn);
        Button playStopBtn = (Button) findViewById(R.id.playStopBtn);
        Button playSoundBtn = (Button) findViewById(R.id.PlaysoundBtn);
        Button stopSoundBtn = (Button) findViewById(R.id.StopsoundBtn);


        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }

                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                recorder.setOutputFile(RECORDED_FILE);

                try {
                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();

                    recorder.prepare();
                    recorder.start();
                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder == null)
                    return;

                recorder.stop();
                recorder.release();
                recorder = null;

                Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }

                Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();
                try {
                    player = new MediaPlayer();

                    player.setDataSource(RECORDED_FILE);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Log.e("SampleAudioRecorder", "Audio play failed.", e);
                }
            }
        });


        playStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player == null)
                    return;
                Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.", Toast.LENGTH_LONG).show();

                player.stop();
                player.release();
                player = null;
            }
        });
        playSoundBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Path = Environment.getExternalStorageDirectory() + "recorded.mp4";

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                }
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }

                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                recorder.setOutputFile(RECORDED_FILE);

                try {
                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                    recorder.prepare();
                    recorder.start();
                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
                try {
                    player.setDataSource(Path);
                } catch(IOException  e){
                    System.out.println(e.toString());
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                }
                if (recorder == null)
                    return;

                recorder.stop();
                recorder.release();
                recorder = null;

                String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
                String filePath = sd + "/original.wav";
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(filePath);
                    Toast.makeText(getApplicationContext(), "파일이 추출되었습니다2.", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    byte bData[] = short2byte(Buffer_original);
                    os.write(bData, 0, 2048);
                    Toast.makeText(getApplicationContext(), "파일이 추출되었습니다3.", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();

            }
        });
        stopSoundBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keepGoing = false;
                audioTrack.stop();
            }
        });
    }

    protected void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        super.onPause();
    }

    void playSound() {
        Buffer = new short[48000 * 2+2];
        Buffer_original = new short[48000*2];
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }

        int minSize = AudioTrack.getMinBufferSize(48000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                48000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, Buffer.length, AudioTrack.MODE_STATIC);
        generatePulse(500, 48000);
        audioTrack.write(Buffer, 0, Buffer.length);
        audioTrack.setStereoVolume(1.0f, 1.0f);
        audioTrack.play();


    }

    void generatePulse(int freq, int SamplingFreq) {
        double omega, time;
        int i, Index = 0, Index2=0;
        short Vout;
        int freq2 = freq;


        for (i = 0; i <= SamplingFreq - 1; i++) {
           /*if ((1 / freq2) < (i / SamplingFreq)) {
                freq2 += 10;
            }*/

            omega = 2 * Math.PI * freq2;
            time = (double) i / SamplingFreq;
            Vout = (short) (32767 * Math.sin(omega * time));
            if(1/freq2>i/SamplingFreq) {
                Buffer_original[Index2] = Vout;   //LEFT 저장
                Index2++;
                Buffer_original[Index2] = Vout;  //RIGHT 저장
                Index2++;
            }
            Buffer[Index] = Vout;   //LEFT 저장
            Index++;
            Buffer[Index] = Vout;  //RIGHT 저장
            Index++;
        }
            Buffer[Index] = (short)freq;
            Index++;
            Buffer[Index] = (short)freq2;
    }
    private byte[] short2byte(short[] sData) {

        int shortArrsize = sData.length;

        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {

            bytes[i * 2] = (byte) (sData[i] & 0x00FF);

            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);

            sData[i] = 0;

        }

        return bytes;

    }
    public String[] GetAllMp3Path() {

        // MP3 경로를 가질 문자열 배열.
        String[] resultPath = null;

        // 외장 메모리 접근 권한을 가지고 있는지 확인. ( Marshmallow 이상 )  // mAcitivity == Main Activity
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            // 찾고자하는 파일 확장자명.
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");

            String[] selectionArgsMp3 = new String[]{ mimeType };

            Cursor c = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DATA}, selectionMimeType, selectionArgsMp3, null);

            if (c.getCount() == 0)
                return null;

            resultPath = new String[c.getCount()];
            while (c.moveToNext()) {
                // 경로 데이터 셋팅.
                resultPath[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            }
        }

        return resultPath;
    }


}