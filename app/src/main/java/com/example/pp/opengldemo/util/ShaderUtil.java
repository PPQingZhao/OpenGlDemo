package com.example.pp.opengldemo.util;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/6/20 21:37
 * @doc ${TODO}
 */

public class ShaderUtil {
    public final static int ERROR_INT = -1;

    public static int loadShader(int shaderType, String shaderSource) {
        // 返回0 表示失败
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e("TAG", "glCreateShader failed! shaderType: " + shaderType);
            return ERROR_INT;
        }
        // 加载shader资源
        GLES20.glShaderSource(shader, shaderSource);
        // 编译
        GLES20.glCompileShader(shader);
        // 检测编译结果
        int[] params = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
        if (GLES20.GL_TRUE != params[0]) {
            GLES20.glDeleteShader(shader);
            Log.e("TAG", "glCompileShader failed, shaderType: " + shaderType);
            return ERROR_INT;
        }
        return shader;
    }
}
