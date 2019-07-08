package com.example.pp.opengldemo.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class GLTexture1 extends BaseTexture {

    @Override
    public int[] drawTexture(Bitmap bitmap) {
        if (GLProgram.ERROR_INT == mGLProgram.getProgram()) {
            Log.e("TAG", "useProgram program is null!");
            return new int[0];
        }
        if (null != bitmap) {
            if (lastPictureWidth != bitmap.getWidth() || lastPictureHeight != bitmap.getHeight()) {
                // 设置变换矩阵
                setupMatrix(matrix, windowWidth, windowHeight, bitmap.getWidth(), bitmap.getHeight());
                int internalFormat = GLUtils.getInternalFormat(bitmap);
                for (int i = 0; i < texture.length; i++) {
                    //　设置窗口
                    setupTextureSize(0, texture[i], bitmap.getWidth(), bitmap.getHeight(), internalFormat);
                }
            }
        }

        for (int i = 0; i < TEXTURE_COUNT; i++) {
            clearColor();
            setupVertexAttribPointer(vbo[i]);
            glDraw(texture[i], i, matrix, bitmap);
        }

        lastPictureWidth = bitmap.getWidth();
        lastPictureHeight = bitmap.getWidth();
        return texture;
    }

    private void glDraw(int textId, int index, float[] matrix, Bitmap bitmap) {
        // 绑定第一层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textId);
        // 激活第一层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        GLES20.glUniformMatrix4fv(mGLProgram.getuMatrix(), 1, false, this.matrix, 0);
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
