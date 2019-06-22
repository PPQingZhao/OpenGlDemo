package com.example.pp.opengldemo.weight;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author pp
 * @version $Rev$
 * @time 2019/6/22 21:43
 * @doc ${TODO}
 */

public class GLTexteure {

    private final GLShader shader;

    public GLTexteure() {
        shader = new GLShader();
    }

    public void draw(Bitmap bitmap) {
        shader.draw(bitmap);
    }

    public void init(String codeVertex, String codeFragment) {
        shader.init(codeVertex, codeFragment);
    }

    public void viewPort(int x, int y, int width, int height) {
        shader.viewPort(x, y, width, height);
    }
}
