package com.example.pp.opengldemo.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class FBOTexture extends BaseTexture {

    // fbo id
    private final int[] fbos = new int[TEXTURE_COUNT];
    private int[] fboTextureId = new int[TEXTURE_COUNT];

    @Override
    public void init(String codeVertex, String codeFragment) {
        super.init(codeVertex, codeFragment);
        createFBOTexture();
        createFBO();
    }

    private void createFBOTexture() {
        // 创建纹理材质
        GLES20.glGenTextures(fboTextureId.length, fboTextureId, 0);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            texParameteri(fboTextureId[i]);
        }
    }

    private void createFBO() {
        GLES20.glGenFramebuffers(TEXTURE_COUNT, fbos, 0);
    }

    public int[] getFboTextureId() {
        return fboTextureId;
    }

    @Override
    protected void setupMatrix(float[] matrix, int windowWidth, int windowHeight, int pictureWidth, int pictureHeight) {
        super.setupMatrix(matrix, windowWidth, windowHeight, pictureWidth, pictureHeight);
        //　旋转  绕　X轴旋转180度
        Matrix.rotateM(matrix, 0, 180, 1, 0, 0);
    }

    @Override
    public int[] drawTexture(Bitmap[] bitmapArr) {
        if (GLProgram.ERROR_INT == mGLProgram.getProgram()) {
            Log.e("TAG", "useProgram program is null!");
            return new int[0];
        }
        /*if (null != bitmap) {
            if (lastPictureWidth != bitmap.getWidth() || lastPictureHeight != bitmap.getHeight()) {
                // 设置变换矩阵
                setupMatrix(matrix, windowWidth, windowHeight, bitmap.getWidth(), bitmap.getHeight());
                int internalFormat = GLUtils.getInternalFormat(bitmap);
                for (int i = 0; i < texture.length; i++) {
                    //　设置fbo渲染纹理大小
                    setupTextureSize(0, texture[i], bitmap.getWidth(), bitmap.getHeight(), internalFormat);
                    //　设置fbo窗口
                    setupTextureSize(fbos[i], getFboTextureId()[i], windowWidth, windowHeight, internalFormat);
                    int result = setupFBO(fbos[i], getFboTextureId()[i], vbo[i]);
                    if (result == ERROR_INT) {
                        Log.e("TAG", "setupFBO failed!!!");
                        return getFboTextureId();
                    }
                }
            }
        }*/


        for (int i = 0; i < bitmapArr.length; i++) {
            Bitmap bitmap = bitmapArr[i];
            if (null != bitmap) {
                if (lastPictureWidth != bitmap.getWidth() || lastPictureHeight != bitmap.getHeight()) {
                    // 设置变换矩阵
                    setupMatrix(matrix, windowWidth, windowHeight, bitmap.getWidth(), bitmap.getHeight());
                    int internalFormat = GLUtils.getInternalFormat(bitmap);
                    //　设置fbo渲染纹理大小
                    setupTextureSize(0, texture[0], bitmap.getWidth(), bitmap.getHeight(), internalFormat);
                    //　设置fbo窗口
                    setupTextureSize(fbos[0], getFboTextureId()[0], windowWidth, windowHeight, internalFormat);
                    int result = setupFBO(fbos[0], getFboTextureId()[0], vbo[0]);
                    if (result == ERROR_INT) {
                        Log.e("TAG", "setupFBO failed!!!");
                        return getFboTextureId();
                    }
                }

//                clearColor();
                setupVertexAttribPointer(vbo[0]);
                glFBODraw(texture[0], 0, matrix, bitmap);
                lastPictureWidth = bitmap.getWidth();
                lastPictureHeight = bitmap.getWidth();
            }
        }
        return getFboTextureId();
    }

    protected void glFBODraw(int textId, int index, float[] matrix, Bitmap bitmap) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[index]);
        clearColor();
        GLES20.glUseProgram(mGLProgram.getProgram());
        GLES20.glUniformMatrix4fv(mGLProgram.getuMatrix(), 1, false, matrix, 0);
        // 绑定第index层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textId);
        // 激活第index层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
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
}
