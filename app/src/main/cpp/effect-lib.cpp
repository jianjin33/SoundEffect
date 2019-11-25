//
// Created by Administrator on 2019/11/19.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>
#include "inc/fmod.hpp"
#include "common.h"

#define CLASSPATH "com/jianjin33/soundeffect/EffectProcess"
#define LOG_TAG "SoundEffect-JNI"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)

// 声音类型
#define MODE_NORMAL 0
#define MODE_LUOLI 1
#define MODE_DASHU 2
#define MODE_JINGSONG 3
#define MODE_GAOGUAI 4
#define MODE_KONGLING 5

using namespace std;
using namespace FMOD;

static bool gPause = false;

static int gPlayCount = 0;
static bool gModeChanged = false;
static const char *path;
static int mode;
JavaVM *g_jvm;


void *EffectByFMOD(void *argv) {
    JNIEnv *env;
    g_jvm->AttachCurrentThread(&env, NULL);

    bool playing = true;
    System *system;
    Sound *sound ;
    Channel *channel;
    ChannelGroup *channelGroup;
    DSP *dsp = 0;
    float frequency = 0;

    try {
        System_Create(&system);
        system->init(32, FMOD_INIT_NORMAL, NULL);

        LOGI("PATH:%s", path);
        system->getMasterChannelGroup(&channelGroup);

        //创建声音
        system->createSound(path, FMOD_DEFAULT, 0, &sound);

        LOGI("MODE:%d", mode);

        switch (mode) {
            case MODE_NORMAL:
                //原生播放
                LOGI("%s", "normal play");
                system->playSound(sound, 0, 0, &channel);
                break;
            case MODE_LUOLI:
                // DSP digital signal process 数字信号处理
                // FMOD_DSP_TYPE_PITCHSHIFT，提升或降低音调 用的一种音效;
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                system->playSound(sound, 0, false, &channel);
                channelGroup->addDSP(0, dsp);
                // 设置音调的参数
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.4);
                LOGI("%s", "luoli play");
                break;
            case MODE_DASHU:
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                system->playSound(sound, 0, false, &channel);
                channelGroup->addDSP(0, dsp);
                // 设置音调的参数
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.6);
                break;
            case MODE_JINGSONG:
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 0.5);
                system->playSound(sound, 0, false, &channel);
                channel->addDSP(0, dsp);
                break;
            case MODE_GAOGUAI:
                system->playSound(sound, 0, false, &channel);
                channel->getFrequency(&frequency);
                frequency = frequency * 1.6;
                channel->setFrequency(frequency);
                break;
            case MODE_KONGLING:
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 300);
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 20);
                system->playSound(sound, 0, false, &channel);
                channel->addDSP(0, dsp);
                break;
        }
    } catch (...) {
        LOGE("%s", "ERROR");
        goto end;
    }

    while (playing) {
        LOGI("%s", "正在播放");
        if (gPause) {
            LOGI("%s", "需要暂停");
            gPause = false;
            channel->setPaused(true);
            gPlayCount = 0;
            goto end;
        }

        if (gModeChanged) {
            gModeChanged = false;
        }

        system->update();
        channel->isPlaying(&playing);
        usleep(1000 * 1000);
    };
    goto end;

    end:
    LOGI("%s", "播放完成");
    sound->release();
    system->close();
    system->release();

    g_jvm->DetachCurrentThread();
    return (void *) 0;
}

extern "C" JNIEXPORT void JNICALL
SoundEffect(JNIEnv *env, jobject thiz, jstring jpath, jint jmode) {
    const char *cpath = env->GetStringUTFChars(jpath, JNI_FALSE);
    LOGI("%s", cpath);

    //EffectByFMOD(NULL);

    if (gPlayCount == 0) {
        pthread_t pthread;
        pthread_create(&pthread, NULL, EffectByFMOD, NULL);
        gPlayCount++;
    } else {
        gModeChanged = true;
    }


    path = cpath;
    mode = jmode;

    env->ReleaseStringUTFChars(jpath, cpath);
}


extern "C" JNIEXPORT void JNICALL Start(JNIEnv *env, jobject thiz) {

}

extern "C" JNIEXPORT void JNICALL Pause(JNIEnv *env, jobject thiz) {
    gPause = true;
}


// 动态注册
static JNINativeMethod gMethods[] = {
        {"native_effect", "(Ljava/lang/String;I)V", (void *) SoundEffect},
        {"native_start",  "()V",                    (void *) Start},
        {"native_pause",  "()V",                    (void *) Pause},
};

static int RegisterNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);

    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int RegisterMethods(JNIEnv *env) {
    return RegisterNativeMethods(env, CLASSPATH, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("%s", "GetEnv failed\n");
        goto bail;
    }
    g_jvm = vm;

    if (RegisterMethods(env) < 0) {
        LOGE("%s", "native registration failed\n");
        goto bail;
    }

    result = JNI_VERSION_1_6;
    LOGE("%s", "native registration success\n");
    bail:
    return result;
}