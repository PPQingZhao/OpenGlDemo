package com.example.pp.opengldemo.weight;

import android.content.Context;

import com.example.pp.opengldemo.R;
import com.example.pp.opengldemo.opengl.GLTexture;
import com.example.pp.opengldemo.util.StreamUtil;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/4/21 19:25
 * @doc ${TODO}
 */

public class ShareContextRenderer implements GLSurfaceView.Renderer {
    private final Context mContext;
    private final GLTexture glTexture;
    private int[] textureId;

    public ShareContextRenderer(Context context) {
        this.mContext = context;
        glTexture = new GLTexture();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //正交投影顶点shader
        String codeVertex = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx_m));
        //片源着色器
        String codeFragment = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_fragment));
        glTexture.init(codeVertex, codeFragment);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置显示大小
        glTexture.viewPort(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (null != textureId) {
            glTexture.glTextureDraw(textureId, new float[]{
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f});
        }

    }

    public void setTexture(int[] textureId) {
        this.textureId = textureId;
    }
}
