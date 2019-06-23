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
    private static final int TEXTURE_COUNT = 1;
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;
    private final int[] textureIDs = new int[TEXTURE_COUNT];
    private final int[] vbos = new int[TEXTURE_COUNT];
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
        createShaderBuffer();
        // 创建文理
        createTexture();
        setupTexture();
    }

    private void setupTexture() {
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            setupTexture(textureIDs[i], vbos[i]);
        }
    }

    private void createShaderBuffer() {
        // 创建缓冲区
        GLES20.glGenBuffers(vbos.length, vbos, 0);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[i]);
            // 分配空间 用于存储vertexData 和 fragmentData
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20.GL_STATIC_DRAW);
            // 分别摄置数据 vertexData 和 fragmentData 数据
            // vertexData 数据设置在前部分内存(即从0开始设置)
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
            // fragmentData 数据设置在vertexData(vertexData.length * 4)
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
            // 解绑
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }

    private void setupTextureSize(int textureId, int vboID, int width, int height, int internalformat) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        // 设置文理格式和大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalformat, width, height, 0, internalformat, GLES20.GL_UNSIGNED_BYTE, null);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void setupTexture(int textureId, int vboID) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        // 使 顶点变量有效
        GLES20.glEnableVertexAttribArray(v_position);
        //传递数据赋值
        GLES20.glVertexAttribPointer(v_position, 2, GLES20.GL_FLOAT, false, 8, 0);
        // 加入纹理坐标数据
        GLES20.glEnableVertexAttribArray(f_Position);
        GLES20.glVertexAttribPointer(f_Position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
        // 当前文理设置完属性后,解绑文理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void createTexture() {
        // 创建纹理材质
        GLES20.glGenTextures(textureIDs.length, textureIDs, 0);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            // 绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[i]);
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
                int internalFormat = GLUtils.getInternalFormat(bitmap);
                for (int i = 0; i < TEXTURE_COUNT; i++) {
                    setupTextureSize(textureIDs[i], vbos[i], bitmap.getWidth(), bitmap.getHeight(), internalFormat);
                }
            }
        }
        glDraw(0, bitmap);

        lastPictureWidth = bitmap.getWidth();
        lastPictureHeight = bitmap.getWidth();
    }

    public void clearColor() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //颜色清屏 我们会在屏幕上看到这种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
    }
}
