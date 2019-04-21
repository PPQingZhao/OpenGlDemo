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
    public PPGLSurfaceView(Context context) {
        this(context, null);
    }

    public PPGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(new PPGLRenderer());
    }
}
