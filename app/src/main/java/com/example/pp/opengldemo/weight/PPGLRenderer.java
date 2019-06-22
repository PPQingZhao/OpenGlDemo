package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.pp.opengldemo.R;
import com.example.pp.opengldemo.util.StreamUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/4/21 19:25
 * @doc ${TODO}
 */

public class PPGLRenderer implements GLSurfaceView.Renderer {
    private final Context mContext;

    private final Bitmap bitmap;
    private final GLTexteure glTexteure;

    public PPGLRenderer(Context context) {
        this.mContext = context;
        glTexteure = new GLTexteure();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String codeVertex = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx));
        String codeFragment = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_fragment));
        // 创建渲染程序
        glTexteure.init(codeVertex, codeFragment);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置显示大小
        glTexteure.viewPort(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glTexteure.draw(bitmap);
    }
}
