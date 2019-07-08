package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/4/21 19:24
 * @doc ${TODO}
 */

public class PPGLSurfaceView extends GLSurfaceView {

    private PPGLRenderer mGlRenderer;

    public PPGLSurfaceView(Context context) {
        this(context, null);
    }

    public PPGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void startRender() {
        startRender(null);
    }

    public void startRender(PPGLRenderer.OnTextureListener onTextureListener) {
        //设置egl版本
        setEGLContextClientVersion(2);
        mGlRenderer = new PPGLRenderer(getContext());
        mGlRenderer.setOnTextureListener(onTextureListener);
        // 设置 渲染回调
        setRenderer(mGlRenderer);
    }
}
