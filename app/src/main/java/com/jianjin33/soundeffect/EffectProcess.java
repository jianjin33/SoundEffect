package com.jianjin33.soundeffect;

public class EffectProcess {

    // 音效的类型
    public static final int MODE_NORMAL = 0;
    public static final int MODE_LUOLI = 1;
    public static final int MODE_DASHU = 2;
    public static final int MODE_JINGSONG = 3;
    public static final int MODE_GAOGUAI = 4;

    static {
        System.loadLibrary("effect-lib");
    }
    /**
     * 音效处理
     *
     * @param path
     * @param type
     */
    public native void native_effect(String path, int type);


}
