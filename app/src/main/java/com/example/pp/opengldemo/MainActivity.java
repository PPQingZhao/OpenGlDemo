package com.example.pp.opengldemo;

import android.os.Bundle;

import com.example.pp.opengldemo.weight.FboRenderer;
import com.example.pp.opengldemo.weight.GLSurfaceView;
import com.example.pp.opengldemo.weight.ShareContextRenderer;
import com.example.pp.opengldemo.weight.PPGLSurfaceView;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class MainActivity extends AppCompatActivity {

    private PPGLSurfaceView surfaceView1;
    private PPGLSurfaceView surfaceView2;
    private PPGLSurfaceView surfaceView3;
    private PPGLSurfaceView surfaceView4;
    private ShareContextRenderer gLenderer2;
    private ShareContextRenderer gLenderer3;
    private ShareContextRenderer gLenderer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupSurfaceView();
    }

    FboRenderer.OnTextureListener onTextureListener = new FboRenderer.OnTextureListener() {
        @Override
        public void onTextureCreated(int[] textureId) {
            Log.e("TAG", "textureid: " + Arrays.toString(textureId));
            gLenderer2.setTexture(textureId);
            gLenderer3.setTexture(textureId);
            gLenderer4.setTexture(textureId);
        }
    };

    GLSurfaceView.EGLContextFactory shareContextFactory = new GLSurfaceView.EGLContextFactory() {
        private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};

            EGLContext eglContext = surfaceView1.getEGLContext();
            return egl.eglCreateContext(display, config, (null == eglContext) ? EGL10.EGL_NO_CONTEXT : eglContext, attrib_list);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("TAG", "display:" + display + " context: " + context);
            }
        }
    };

    private void setupSurfaceView() {
        FboRenderer ppglRenderer = new FboRenderer(getApplicationContext());
        ppglRenderer.setOnTextureListener(onTextureListener);
        surfaceView1.setRender(ppglRenderer);

        gLenderer2 = new ShareContextRenderer(getApplicationContext(), R.raw.shader_vertx_m, R.raw.shader_fragment1);
        surfaceView2.setEGLContextFactory(shareContextFactory);
        surfaceView2.setRender(gLenderer2);

        gLenderer3 = new ShareContextRenderer(getApplicationContext(), R.raw.shader_vertx_m, R.raw.shader_fragment2);
        surfaceView3.setEGLContextFactory(shareContextFactory);
        surfaceView3.setRender(gLenderer3);

        gLenderer4 = new ShareContextRenderer(getApplicationContext(), R.raw.shader_vertx_m, R.raw.shader_fragment3);
        surfaceView4.setEGLContextFactory(shareContextFactory);
        surfaceView4.setRender(gLenderer4);
    }

    private void initView() {
        surfaceView1 = (PPGLSurfaceView) findViewById(R.id.main_glsurfaceview);
        surfaceView2 = (PPGLSurfaceView) findViewById(R.id.main_glsurfaceview2);
        surfaceView3 = (PPGLSurfaceView) findViewById(R.id.main_glsurfaceview3);
        surfaceView4 = (PPGLSurfaceView) findViewById(R.id.main_glsurfaceview4);
    }
}
