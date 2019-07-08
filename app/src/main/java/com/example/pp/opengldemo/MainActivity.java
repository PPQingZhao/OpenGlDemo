package com.example.pp.opengldemo;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp.opengldemo.weight.PPGLRenderer;
import com.example.pp.opengldemo.weight.PPGLSurfaceView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private PPGLSurfaceView surfaceView1;
    private PPGLSurfaceView surfaceView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupSurfaceView();
    }

    private void setupSurfaceView() {
        surfaceView1.startRender(new PPGLRenderer.OnTextureListener() {
            @Override
            public void onTextureCreated(int[] textureId) {
                Log.e("TAG", "****************** textureId: " + Arrays.toString(textureId));
            }
        });
    }

    private void initView() {
        surfaceView1 = findViewById(R.id.main_glsurfaceview);
        surfaceView2 = findViewById(R.id.main_glsurfaceview2);
    }
}
