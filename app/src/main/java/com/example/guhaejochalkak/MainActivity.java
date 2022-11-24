package com.example.guhaejochalkak;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private ImageButton record_btn;
    private ImageButton call_btn;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private  boolean recording = false;

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
    }
}