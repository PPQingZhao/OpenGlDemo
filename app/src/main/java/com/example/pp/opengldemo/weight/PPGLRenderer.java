package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.example.pp.opengldemo.R;
import com.example.pp.opengldemo.opengl.GLTexture;
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
    private final GLTexture glTexteure;
    private final Bitmap bitmap2;
    private int drawCount;

    public PPGLRenderer(Context context) {
        this.mContext = context;
        glTexteure = new GLTexture();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.androids);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String codeVertex = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx_m));
        String codeFragment = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_fragment));
        // 创建渲染程序
        glTexteure.init(codeVertex, codeFragment);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置显示大小
        glTexteure.viewPort(0, 0, width, height);
    }

    int drawFan = 100;

    @Override
    public void onDrawFrame(GL10 gl) {
        if (drawCount < drawFan) {
            glTexteure.draw(bitmap2);
        } else {
            glTexteure.draw(bitmap);
        }
        drawCount++;
        drawCount = drawCount > drawFan * 2 ? 0 : drawCount;
    }
}
