package com.jianjin33.soundeffect;

public class EffectProcess {

    // 音效的类型
    public static final int MODE_NORMAL = 0;
    public static final int MODE_LUOLI = 1;
    public static final int MODE_DASHU = 2;
    public static final int MODE_JINGSONG = 3;
    public static final int MODE_GAOGUAI = 4;
    public static final int MODE_KONGLING = 5;

    static {
        try {
            System.loadLibrary("fmodL");
            System.loadLibrary("fmod");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

        System.loadLibrary("effect-lib");
    }

    public void setPlayState(boolean isPlaying){

    }

    /**
     * 音效处理
     *
     * @param path
     * @param type
     */
    public native void native_effect(String path, int type);

    public native void native_start();

    public native void native_pause();


}
