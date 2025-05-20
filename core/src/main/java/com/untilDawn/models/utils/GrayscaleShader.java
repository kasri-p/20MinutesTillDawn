package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class GrayscaleShader implements Disposable {
    private static GrayscaleShader instance;
    private final String vertexShader =
        "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
            "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
            "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "\n" +
            "void main() {\n" +
            "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
            "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
            "   gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
            "}";
    private final String fragmentShader =
        "#ifdef GL_ES\n" +
            "    precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" +
            "    // Convert to grayscale using luminance formula\n" +
            "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
            "    gl_FragColor = vec4(gray, gray, gray, color.a);\n" +
            "}";
    private ShaderProgram grayscaleShader;
    private ShaderProgram defaultShader;

    private GrayscaleShader() {
        ShaderProgram.pedantic = false; // Disable strict checking for better compatibility
        grayscaleShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!grayscaleShader.isCompiled()) {
            Gdx.app.error("GrayscaleShader", "Shader compilation failed:\n" + grayscaleShader.getLog());
        }
    }

    public static GrayscaleShader getInstance() {
        if (instance == null) {
            instance = new GrayscaleShader();
        }
        return instance;
    }

    public void enable(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (defaultShader == null) {
            defaultShader = batch.getShader();
        }
        batch.setShader(grayscaleShader);
    }
    
    public void disable(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        batch.setShader(defaultShader);
    }

    @Override
    public void dispose() {
        if (grayscaleShader != null) {
            grayscaleShader.dispose();
        }
    }
}
