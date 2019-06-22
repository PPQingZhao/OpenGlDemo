package com.example.pp.opengldemo.weight;

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
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;
    private final int[] textureIDs = new int[1];
    private final int[] vbos = new int[1];
    private int program;

    private int v_position;
    private int f_Position;
    private int sTexture;
    private int lastPictureWidth;
    private int lastPictureHeight;

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
    }

    public void init(String codeVertex, String codeFragment) {
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

        // 设置纹理层
        GLES20.glUniform1i(sTexture, 0);  // sTexture 对应第一层
        // 创建定点缓冲区 vbo
//        createShaderBuffer();
        // 创建文理
        createTexture();
    }

    private void createShaderBuffer() {
        // 创建缓冲区
        GLES20.glGenBuffers(vbos.length, vbos, 0);
    }


    private void setupTexture(int textureId, Bitmap bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 使 顶点变量有效
        GLES20.glEnableVertexAttribArray(v_position);
        //传递数据赋值
        GLES20.glVertexAttribPointer(v_position, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        // 加入纹理坐标数据
        GLES20.glEnableVertexAttribArray(f_Position);
        GLES20.glVertexAttribPointer(f_Position, 2, GLES20.GL_FLOAT, false, 8, fragmentBuffer);
        // 设置文理格式和大小
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void createTexture() {
        // 创建纹理材质
        GLES20.glGenTextures(textureIDs.length, textureIDs, 0);
        for (int textureId : textureIDs) {
            // 绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            // 设置图形环绕方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            // 设置过滤器
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // 解绑纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
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

    public void draw(Bitmap bitmap) {
        if (program == ERROR_INT) {
            Log.e("TAG", "useProgram program is null!");
            return;
        }
        clearColor();
        if (null != bitmap) {
            if (lastPictureWidth != bitmap.getWidth() || lastPictureHeight != bitmap.getHeight()) {
                for (int id : textureIDs) {
                    setupTexture(id, bitmap);
                }
            }
        }

        texSubImage2D(0, bitmap);
        // 更新渲染数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        lastPictureWidth = bitmap.getWidth();
        lastPictureHeight = bitmap.getWidth();
    }

    public void clearColor() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //颜色清屏 我们会在屏幕上看到这种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private void texSubImage2D(int index, Bitmap bitmap) {
        // 绑定第一层文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[index]);
        // 激活第一层文理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        // 更新渲染数据 替换文理内容
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
    }

    public void viewPort(int x, int y, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}
