package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/4/21 19:24
 * @doc ${TODO}
 */

public class PPGLSurfaceView extends GLSurfaceView {

    private GLSurfaceView.Renderer  mGlRenderer;

    public PPGLSurfaceView(Context context) {
        this(context, null);
    }

    public PPGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRender(GLSurfaceView.Renderer render) {
        //设置egl版本
        setEGLContextClientVersion(2);
        mGlRenderer = render;
        // 设置 渲染回调
        setRenderer(mGlRenderer);
    }
}
