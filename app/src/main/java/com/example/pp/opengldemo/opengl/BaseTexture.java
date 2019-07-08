package com.example.pp.opengldemo.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;



import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class BaseTexture {
    public static final int ERROR_INT = -1;
    /* 纹理数量*/
    protected static final int TEXTURE_COUNT = 1;
    // 顶点缓冲id
    protected final int[] vbo = new int[TEXTURE_COUNT];
    // 纹理id
    protected final int[] texture = new int[TEXTURE_COUNT];

    protected int windowWidth;
    protected int windowHeight;
    protected int lastPictureWidth;
    protected int lastPictureHeight;
    protected final GLProgram mGLProgram;

    static final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    static final float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer fragmentBuffer;
    final float[] matrix = new float[16];

    public float[] getVertexData() {
        return vertexData;
    }

    public float[] getFragmentData() {
        return fragmentData;
    }

    public float[] getMatrix() {
        return matrix;
    }

    public BaseTexture() {
        // 分配内存
        vertexBuffer = ByteBuffer.allocateDirect(getVertexData().length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(getVertexData());
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(getFragmentData().length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(getFragmentData());
        fragmentBuffer.position(0);
        mGLProgram = new GLProgram();
    }

    /**
     * 清屏
     */
    public void clearColor() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //颜色清屏 我们会在屏幕上看到这种颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    // 设置显示窗口
    public void viewPort(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        windowWidth = width;
        windowHeight = height;
    }

    public void init(String codeVertex, String codeFragment) {
        // 初始化渲染程序
        mGLProgram.init(codeVertex, codeFragment);
        if (GLProgram.ERROR_INT == mGLProgram.getProgram()) {
            Log.e("TAG", "program init failed!!!");
            return;
        }

        // 创建顶点缓冲区
        createShaderBuffer();

        // 创建纹理
        createTexture();
    }

    private void createTexture() {
        GLES20.glGenTextures(texture.length, texture, 0);
        for (int textureID : texture) {
            texParameteri(textureID);
        }
    }

    protected void texParameteri(int textureID) {
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        // 设置图形环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置过滤器
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    private void createShaderBuffer() {
        GLES20.glGenBuffers(vbo.length, vbo, 0);
        for (int vboId : vbo) {
            // 绑定 vbo
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
            // 分配空间,用于存储 vertexdata 和 fragmentdata
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, getVertexData().length * 4 + getFragmentData().length * 4, null, GLES20.GL_STATIC_DRAW);
            // 设置vertexdata数据,从0开始
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, getVertexData().length * 4, vertexBuffer);
            // 设置fragmentdata，紧接着vertexdata 进行存储
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, getVertexData().length * 4, getFragmentData().length * 4, fragmentBuffer);
            // 解绑vbo
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }

    public void glTextureDraw(int[] textureId, float[] matrix) {
        for (int i = 0; i < textureId.length; i++) {
            clearColor();
            setupVertexAttribPointer(vbo[i]);
            GLES20.glUseProgram(mGLProgram.getProgram());
            // 绑定第一层文理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[i]);
            // 激活第一层文理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glUniformMatrix4fv(mGLProgram.getuMatrix(), 1, false, matrix, 0);
            // 更新渲染数据 替换文理内容
            // 更新渲染数据
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * 设置纹理大小
     *
     * @param fboID
     * @param textureId
     * @param width
     * @param height
     * @param internalformat
     */
    protected void setupTextureSize(int fboID, int textureId, int width, int height, int internalformat) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 设置文理格式和大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalformat, width, height, 0, internalformat, GLES20.GL_UNSIGNED_BYTE, null);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * 传递纹理坐标数据,设置纹理属性
     *
     * @param vboID
     */
    protected void setupVertexAttribPointer(int vboID) {
        if (null == mGLProgram) {
            Log.e("TAG", "setTexture() program is null!");
            return;
        }
        GLES20.glUseProgram(mGLProgram.getProgram());
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        // 使 顶点变量有效
        GLES20.glEnableVertexAttribArray(mGLProgram.getV_position());
        //传递数据赋值
        GLES20.glVertexAttribPointer(mGLProgram.getV_position(), 2, GLES20.GL_FLOAT, false, 8, 0);
        // 加入纹理坐标数据
        GLES20.glEnableVertexAttribArray(mGLProgram.getF_Position());
        GLES20.glVertexAttribPointer(mGLProgram.getF_Position(), 2, GLES20.GL_FLOAT, false, 8, getVertexData().length * 4);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }


    protected void setupMatrix(float[] matrix, int windowWidth, int windowHeight, int pictureWidth, int pictureHeight) {
        float aspectRatio;
        if (pictureWidth / (float) pictureHeight > windowWidth / (float) windowHeight) {
            aspectRatio = (float) windowHeight / ((windowWidth / (float) pictureWidth) * pictureHeight);
            Matrix.orthoM(matrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        } else {
            aspectRatio = (float) windowWidth / ((windowHeight / (float) pictureHeight) * pictureWidth);
            Matrix.orthoM(matrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        }
    }

    public abstract int[] drawTexture(Bitmap bitmap);
}
