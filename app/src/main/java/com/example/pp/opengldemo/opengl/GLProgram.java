package com.example.pp.opengldemo.opengl;

import android.opengl.GLES20;
import android.util.Log;

public class GLProgram {
    public static final int ERROR_INT = -1;
    private int program;
    private int v_position;
    private int f_Position;
    private int sTexture;

    public int getProgram() {
        return program;
    }

    public int getV_position() {
        return v_position;
    }

    public int getF_Position() {
        return f_Position;
    }

    public int getsTexture() {
        return sTexture;
    }

    public GLProgram() {
    }

    public void init(String codeVertex, String codeFragment){
        program = creatProgram(codeVertex, codeFragment);
        if (ERROR_INT == program) {
            Log.e("TAG", "GLShader creatProgram failes!");
            return;
        }
        // 激活渲染程序
        GLES20.glUseProgram(program);

        // 获取shader 属性位置
        v_position = GLES20.glGetAttribLocation(program, "v_Position");
        f_Position = GLES20.glGetAttribLocation(program, "f_Position");
        sTexture = GLES20.glGetUniformLocation(program, "sTexture");
    }

    private int loadShader(int shaderType, String shaderSource) {
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

    private int creatProgram(String codeVertex, String codeFragment) {
        int shaderVertex = loadShader(GLES20.GL_VERTEX_SHADER, codeVertex);
        int shaderFragment = loadShader(GLES20.GL_FRAGMENT_SHADER, codeFragment);
        if (shaderVertex == ERROR_INT || shaderFragment == ERROR_INT) {
            Log.e("TAG", "creatProgram failed ");
            return ERROR_INT;
        }
        Log.e("TAG", "create shader success!");
        program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e("TAG", "glCreateProgram failed! ");
            return ERROR_INT;
        }
        // 渲染程序加入着色器代码
        GLES20.glAttachShader(program, shaderVertex);
        GLES20.glAttachShader(program, shaderFragment);

        // 链接程序
        GLES20.glLinkProgram(program);
        return program;
    }
}
