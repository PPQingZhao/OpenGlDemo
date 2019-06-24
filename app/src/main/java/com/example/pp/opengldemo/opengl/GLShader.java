package com.example.pp.opengldemo.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/6/22 21:59
 * @doc ${TODO}
 */

public class GLShader {
    public static final int ERROR_INT = -1;
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

    static final float[] fboFragmentData = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };
    private static final int TEXTURE_COUNT = 1;

    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;
    private FloatBuffer fboFragmentBuffer;

    private final int[] textureIDs = new int[TEXTURE_COUNT];
    private final int[] imageTextureIDs = new int[TEXTURE_COUNT];

    private final int[] vbos = new int[TEXTURE_COUNT];
    private final int[] fbos = new int[TEXTURE_COUNT];

    private int lastPictureWidth;
    private int lastPictureHeight;
    private int windowHeight;
    private int windowWidth;

    private final GLProgram glProgram;

    public GLShader() {
        // 分配内存
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);

        fboFragmentBuffer = ByteBuffer.allocateDirect(fboFragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fboFragmentData);
        fboFragmentBuffer.position(0);
        glProgram = new GLProgram();
    }

    public void init(String codeVertex, String codeFragment) {
        glProgram.init(codeVertex, codeFragment);
        if (ERROR_INT == glProgram.getProgram()) {
            Log.e("TAG", "GLShader creatProgram failes!");
            return;
        }
        // 创建定点缓冲区 vbo
        createShaderBuffer();
        // 创建文理
        createTexture();
        createImageTexture();

        createFBO();
    }

    private void createImageTexture() {
        // 创建纹理材质
        GLES20.glGenTextures(imageTextureIDs.length, imageTextureIDs, 0);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            texParameteri(imageTextureIDs[i]);
        }
    }

    private void createFBO() {
        GLES20.glGenFramebuffers(TEXTURE_COUNT, fbos, 0);
    }

    /**
     * 设置渲染纹理,　需要在纹理设置大小之后调用
     *
     * @param fboID
     * @param textureID
     * @param vboID
     */
    private int setupFBO(int fboID, int textureID, int vboID) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("TAG", "glCheckFramebufferStatus failed!");
            return ERROR_INT;
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return 1;
    }

    private void createShaderBuffer() {
        // 创建缓冲区
        GLES20.glGenBuffers(vbos.length, vbos, 0);
        int size = vertexData.length * 4 + fragmentData.length * 4 + fboFragmentData.length * 4;
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[i]);
            // 分配空间 用于存储vertexData 和 fragmentData
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, null, GLES20.GL_STATIC_DRAW);
            // 分别摄置数据 vertexData 和 fragmentData 数据
            // vertexData 数据设置在前部分内存(即从0开始设置)
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
            // fragmentData 数据设置在vertexData(vertexData.length * 4)
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
            //　fbo
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, fboFragmentData.length * 4, fboFragmentBuffer);
            // 解绑
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }

    private void setupTextureSize(int fboID, int textureId, int width, int height, int internalformat) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 设置文理格式和大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalformat, width, height, 0, internalformat, GLES20.GL_UNSIGNED_BYTE, null);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void setupTexture(GLProgram program, int vboID, int vertexOffest, int fragmentOffest) {
        if (null == program) {
            Log.e("TAG", "setTexture() program is null!");
            return;
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        // 使 顶点变量有效
        GLES20.glEnableVertexAttribArray(program.getV_position());
        //传递数据赋值
        GLES20.glVertexAttribPointer(program.getV_position(), 2, GLES20.GL_FLOAT, false, 8, vertexOffest);
        // 加入纹理坐标数据
        GLES20.glEnableVertexAttribArray(program.getF_Position());
        GLES20.glVertexAttribPointer(program.getF_Position(), 2, GLES20.GL_FLOAT, false, 8, fragmentOffest);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void createTexture() {
        // 创建纹理材质
        GLES20.glGenTextures(textureIDs.length, textureIDs, 0);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            texParameteri(textureIDs[i]);
        }
    }

    private void texParameteri(int textureID) {
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


    public void draw(Bitmap bitmap) {
        if (glProgram.getProgram() == ERROR_INT) {
            Log.e("TAG", "useProgram program is null!");
            return;
        }
        clearColor();
        if (null != bitmap) {
            if (lastPictureWidth != bitmap.getWidth() || lastPictureHeight != bitmap.getHeight()) {
                int internalFormat = GLUtils.getInternalFormat(bitmap);
                for (int i = 0; i < TEXTURE_COUNT; i++) {
                    //　设置窗口
                    setupTextureSize(fbos[i], textureIDs[i], windowWidth, windowHeight, internalFormat);
                    //　设置fbo渲染纹理大小
                    setupTextureSize(0, imageTextureIDs[i], bitmap.getWidth(), bitmap.getHeight(), internalFormat);
                    int result = setupFBO(fbos[i], textureIDs[i], vbos[i]);
                    if (result == ERROR_INT) {
                        return;
                    }
                }
            }
        }
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            setupTexture(glProgram, vbos[i], 0, vertexData.length * 4 + fragmentData.length*4);
            glFBODraw(i, bitmap);
            setupTexture(glProgram, vbos[i], 0, vertexData.length * 4);
            glWindowDraw(i);
        }

        lastPictureWidth = bitmap.getWidth();
        lastPictureHeight = bitmap.getWidth();
    }

    public void clearColor() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //颜色清屏 我们会在屏幕上看到这种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private void glFBODraw(int index, Bitmap bitmap) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[index]);
        // 绑定第index层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureIDs[index]);
        // 激活第index层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void glWindowDraw(int index) {
        // 绑定第一层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[index]);
        // 激活第一层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void glDraw(int index, Bitmap bitmap) {
        // 绑定第一层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[index]);
        // 激活第一层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void viewPort(int x, int y, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        windowWidth = width;
        windowHeight = height;
    }
}
