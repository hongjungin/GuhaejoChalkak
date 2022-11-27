package com.example.guhaejochalkak;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private ImageButton record_btn;
    private ImageButton call_btn;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private  boolean recording = false;

    float hr_avg = 0;
    float sensitivity = 0;
    int count = 0;
    int temp = 0;
    boolean fear_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        call_btn = (ImageButton)findViewById(R.id.callbtn);
        record_btn = (ImageButton) findViewById(R.id.photobtn);
        //동영상 녹화
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                            try {
                                mediaRecorder = new MediaRecorder();
                                camera.unlock();
                                mediaRecorder.setCamera(camera);
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                                mediaRecorder.setOrientationHint(90);
                                mediaRecorder.setOutputFile("/sdcard/test.mp4");
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                mediaRecorder.release();
                            }
                        }
                    });

                }


                //112 전화걸기
                call_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:112"));
                        startActivity(intent);
                    }
                });
            }
        });

        Button n_button = (Button) findViewById(R.id.button_normal);
        n_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    InputStream in = getResources().openRawResource(R.raw.test_normal);
                    byte[] b = new byte[in.available()];

                    in.read(b);
                    String normal_hr = new String(b);
                    Log.e("test",normal_hr);

                    String[] normal_hr_list = normal_hr.split(",");

                    int[] normal_hr_list_nums = Arrays.asList(normal_hr_list).stream().mapToInt(Integer::parseInt).toArray();
                    System.out.println(Arrays.toString(normal_hr_list_nums));

                    int sum = 0;

                    for (int i = 0; i < normal_hr_list_nums.length; i++){
                        sum += normal_hr_list_nums[i];
                    }

                    hr_avg = sum / normal_hr_list_nums.length;
                    System.out.printf("사용자 평균 심박수 : %f", hr_avg);
                    Log.e("test",String.valueOf(hr_avg));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView sense_v = (TextView) findViewById(R.id.sense_v);
        SeekBar sb = (SeekBar) findViewById(R.id.sense_sb);
        sb.setProgress(16);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 14) i = 14;
                else if (i > 20) i = 20;

                sensitivity = (float)sb.getProgress()/10;
                sense_v.setText(String.format("선택하신 값은 %.1f입니다", sensitivity));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button test_btn = (Button) findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputStream in = getResources().openRawResource(R.raw.test_fear);
                    byte[] b = new byte[in.available()];

                    in.read(b);
                    String test_hr = new String(b);
                    Log.e("test2",test_hr);

                    String[] test_hr_list = test_hr.split(",");

                    int[] hr_test = Arrays.asList(test_hr_list).stream().mapToInt(Integer::parseInt).toArray();
                    System.out.println(Arrays.toString(hr_test));

                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            if (count > hr_test.length) timer.cancel();
                            Log.e("hr rate test", String.valueOf(hr_test[count]));
//                            System.out.printf("현재 심박수 : %d\n", hr_test[count]);
                            count++;

                            temp = Math.abs(hr_test[count] - hr_test[count+1]);
                            if (temp >= 60){
                                // 중간장치 flag ON
                                fear_flag = true;
                            }

                            if (fear_flag = true && hr_test[count] > sensitivity * hr_avg){
                                System.out.printf("%d", hr_test[count]);
                                System.out.printf("%f", sensitivity * hr_avg);
                                camera_on();
//                                timer.cancel();
                            }

                        }
                    };
                    timer.schedule(task, 1000, 1000);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void camera_on(){
        System.out.println("CAMERA_ON");
    }
}