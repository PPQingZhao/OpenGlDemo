package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.example.pp.opengldemo.R;
import com.example.pp.opengldemo.opengl.FBOTexture;
import com.example.pp.opengldemo.opengl.GLTexture;
import com.example.pp.opengldemo.opengl.GLTexture1;
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
    private final GLTexture1 glTexture1;
    private final FBOTexture fboTexture;

    public PPGLRenderer(Context context) {
        this.mContext = context;
        glTexteure = new GLTexture();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.androids);

        glTexture1 = new GLTexture1();
        fboTexture = new FBOTexture();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        String codeVertex = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx));
        //正交投影顶点shader
        String codeVertex = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx_m));
        // 顶点shader
        String codeVertex1 = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_vertx));
        //片源着色器
        String codeFragment = StreamUtil.streamToSrting(mContext.getResources().openRawResource(R.raw.shader_fragment));
        // 创建渲染程序
        glTexteure.init(codeVertex, codeFragment, codeVertex1, codeFragment);

        glTexture1.init(codeVertex, codeFragment);
        fboTexture.init(codeVertex, codeFragment);
        if (null != onTextureListener) {
            onTextureListener.onTextureCreated(glTexteure.getTextureIDs());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置显示大小
        glTexteure.viewPort(0, 0, width, height);
        glTexture1.viewPort(0, 0, width, height);
        fboTexture.viewPort(0, 0, width, height);
    }

    int drawFan = 100;

    @Override
    public void onDrawFrame(GL10 gl) {
        int[] ints;
        if (drawCount < drawFan) {
            ints = fboTexture.drawTexture(bitmap2);
        } else {
            ints = fboTexture.drawTexture(bitmap);
        }

        glTexture1.glTextureDraw(ints, new float[]{
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f});
        drawCount++;
        drawCount = drawCount > drawFan * 2 ? 0 : drawCount;
    }

    private OnTextureListener onTextureListener;

    public void setOnTextureListener(OnTextureListener onTextureListener) {
        this.onTextureListener = onTextureListener;
    }

    public interface OnTextureListener {
        void onTextureCreated(int[] textureId);
    }
}
