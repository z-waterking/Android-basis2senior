package com.bytedance.ies.camerarecorddemoapp.gl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Helper class to draw to whole view using specific texture and texture matrix
 */
public class GLDrawer2D {
    private static final String TAG = "Steven-GLDrawer2D";

    //顶点shader,画点
    private static final String vss
            = "uniform mat4 uMVPMatrix;\n"
            + "uniform mat4 uTexMatrix;\n"
            + "attribute highp vec4 aPosition;\n"
            + "attribute highp vec4 aTextureCoord;\n"
            + "varying highp vec2 vTextureCoord;\n"
            + "\n"
            + "void main() {\n"
            + " gl_Position = uMVPMatrix * aPosition;\n"
            + " vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n"
            + "}\n";

    //片元shader，画面
    private static final String fss
            = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "varying highp vec2 vTextureCoord;\n"
            + "void main() {\n"
            + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
            + "}";
    private static final float[] VERTICES = { 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f };
    private static final float[] TEXCOORD = { 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };

    private final FloatBuffer pVertex;
    private final FloatBuffer pTexCoord;
    private int hProgram;
    int maPositionLoc;
    int maTextureCoordLoc;//纹理坐标引用
    int muMVPMatrixLoc;//投影变换矩阵引用
    int muTexMatrixLoc;//纹理引用
    //投影变换矩阵（注意，opengl坐标系和手机屏幕坐标系不同，为了正常显示，opengl坐标需要左乘投影变换矩阵左）
    private final float[] mMvpMatrix = new float[16];

    private static final int FLOAT_SZ = Float.SIZE / 8;
    private static final int VERTEX_NUM = 4;
    private static final int VERTEX_SZ = VERTEX_NUM * 2;
    /**
     * Constructor
     * this should be called in GL context
     */
    public GLDrawer2D() {
        /**
         * 获取图形的顶点
         * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
         * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
         *
         */
        pVertex = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pVertex.put(VERTICES);
        pVertex.flip();
        /**
         * 同上
         */
        pTexCoord = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pTexCoord.put(TEXCOORD);
        pTexCoord.flip();

        hProgram = loadShader(vss, fss);
        //使用shader程序
        GLES20.glUseProgram(hProgram);
        /**
         * attribute变量是只能在vertex shader中使用的变量。（它不能在fragment shader中声明attribute变量，也不能被fragment shader中使用）
         一般用attribute变量来表示一些顶点的数据，如：顶点坐标，法线，纹理坐标，顶点颜色等。
         在application中，一般用函数glBindAttribLocation（）来绑定每个attribute变量的位置，然后用函数glVertexAttribPointer（）为每个attribute变量赋值。
         */
        maPositionLoc = GLES20.glGetAttribLocation(hProgram, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(hProgram, "aTextureCoord");
        /**
         * uniform变量是外部application程序传递给（vertex和fragment）shader的变量。因此它是application通过函数glUniform**（）函数赋值的。在（vertex和fragment）shader程序内部，uniform变量就像是C语言里面的常量（const ），它不能被shader程序修改。（shader只能用，不能改）
         如果uniform变量在vertex和fragment两者之间声明方式完全一样，则它可以在vertex和fragment共享使用。（相当于一个被vertex和fragment shader共享的全局变量）
         uniform变量一般用来表示：变换矩阵，材质，光照参数和颜色等信息。
         */
        muMVPMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uTexMatrix");

        Matrix.setIdentityM(mMvpMatrix, 0);
         /*
         * 应用投影和视口变换
         * 为当前程序对象指定Uniform变量的值
         *  location
            指明要更改的uniform变量的位置
            count
            指明要更改的矩阵个数
            transpose
            指明是否要转置矩阵，并将它作为uniform变量的值。必须为GL_FALSE。
            value
            指明一个指向count个元素的指针，用来更新指定的uniform变量。
         */
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mMvpMatrix, 0);
        //将顶点位置数据传送进渲染管线, 为画笔指定顶点的位置坐标数据
        GLES20.glVertexAttribPointer(maPositionLoc,//顶点位置数据引用
                2, //每2个数字代表一个坐标
                GLES20.GL_FLOAT,//坐标单位为浮点类型
                false,
                VERTEX_SZ,//每组数据字节数量
                pVertex);//缓冲区
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, pTexCoord);
        //将纹理数据传进渲染管线，为画笔指定纹理坐标数据
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
    }

    /**
     * terminatinng, this should be called in GL context
     */
    public void release() {
        if (hProgram >= 0)
            GLES20.glDeleteProgram(hProgram);
        hProgram = -1;
    }

    /**
     * draw specific texture with specific texture matrix
     * @param tex_id texture ID
     * @param tex_matrix texture matrix、if this is null, the last one use(we don't check size of this array and needs at least 16 of float)
     */
    public void draw(final int tex_id, final float[] tex_matrix) {
        GLES20.glUseProgram(hProgram);
        if (tex_matrix != null)
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, tex_matrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
        //激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex_id);
        //第一个参数表示绘制方式（三角形），第二个参数表示偏移量，第三个参数表示顶点个数。
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_NUM);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    /**
     * Set model/view/projection transform matrix
     * @param matrix
     * @param offset
     */
    public void setMatrix(final float[] matrix, final int offset) {
        if ((matrix != null) && (matrix.length >= offset + 16)) {
            System.arraycopy(matrix, offset, mMvpMatrix, 0, 16);
        } else {
            Matrix.setIdentityM(mMvpMatrix, 0);
        }
    }
    /**
     * create external texture
     * @return texture ID
     */
    public static int initTextureId() {
        final int[] tex = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, tex, 0);
        //纹理帮定的目标(target)并不是通常的GL_TEXTURE_2D，而是GL_TEXTURE_EXTERNAL_OES,这是因为Camera使用的输出texture是一种特殊的格式
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //纹理坐标系用S-T来表示，S为横轴，T为纵轴。
        // 参数1：纹理类型，参数2：纹理环绕方向， 参数3：纹理坐标范围（GL_CLAMP_TO_EDGE：纹理坐标到[1/2n,1-1/2n]，GL_CLAMP：截取纹理坐标到 [0,1]）
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        //第二个参数指定滤波方法，其中参数值GL_TEXTURE_MAG_FILTER指定为放大滤波方法，
        // GL_TEXTURE_MIN_FILTER指定为缩小滤波方法；第三个参数说明滤波方式
        //GL_NEAREST则采用坐标最靠近象素中心的纹素，这有可能使图像走样；
        // 若选择GL_LINEAR则采用最靠近象素中心的四个象素的加权平均值。
        // GL_NEAREST所需计算比GL_LINEAR要少，因而执行得更快，但GL_LINEAR提供了比较光滑的效果。
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        Log.v(TAG, "initTextureId:" + tex[0]);
        return tex[0];
    }

    /**
     * delete specific texture
     */
    public static void deleteTex(final int hTex) {
        Log.v(TAG, "deleteTex:");
        final int[] tex = new int[] {hTex};
        GLES20.glDeleteTextures(1, tex, 0);
    }

    /**
     * load, compile and link shader
     * @param vss source of vertex shader
     * @param fss source of fragment shader
     * @return
     */
    public static int loadShader(final String vss, final String fss) {
        Log.v(TAG, "loadShader:");
        int vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vs, vss);//加载顶点 shader
        GLES20.glCompileShader(vs);//编译shader
        final int[] compiled = new int[1];
        //获取shader的编译结果
        GLES20.glGetShaderiv(vs, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {//获取失败，删除shader并log
            Log.e(TAG, "Failed to compile vertex shader:" + GLES20.glGetShaderInfoLog(vs));
            GLES20.glDeleteShader(vs);
            vs = 0;
        }
        //创建片元着色器
        int fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fs, fss);
        GLES20.glCompileShader(fs);
        GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.w(TAG, "Failed to compile fragment shader:" + GLES20.glGetShaderInfoLog(fs));
            GLES20.glDeleteShader(fs);
            fs = 0;
        }

        //创建shader程序
        int program = GLES20.glCreateProgram();
        if(program != 0) {//创建成功
            //加入顶点着色器
            GLES20.glAttachShader(program, vs);
            //加入片元着色器
            GLES20.glAttachShader(program, fs);
            //链接程序
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            //获取链接程序结果
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

}
