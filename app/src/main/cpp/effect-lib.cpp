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


extern "C" JNIEXPORT void JNICALL
SoundEffect(JNIEnv *env, jobject thiz, jstring path, jint mode) {
   const char *input = env->GetStringUTFChars(path, JNI_FALSE);
    LOGI("%s", input);

    bool playing = true;
    System *system;
    Sound *sound;
    Channel *channel;
    DSP *dsp;
    float frequency = 0;

    try {

        System_Create(&system);
        system->init(32, FMOD_INIT_NORMAL, NULL);

        //创建声音
        system->createSound(input, FMOD_DEFAULT, NULL, &sound);
        switch (mode) {
            case MODE_NORMAL:
                //原生播放
                system->playSound(sound, 0, false, &channel);
                LOGI("%s","fix normal");
                break;
            case MODE_LUOLI:
                // DSP digital signal process 数字信号处理
                // FMOD_DSP_TYPE_PITCHSHIFT，提升或降低音调 用的一种音效;
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                // 设置音调的参数
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2.5);
                system->playSound(sound, 0, false, &channel);
                channel->addDSP(0, dsp);
                LOGI("%s", "luoli play");
                break;
            case MODE_DASHU:
                break;
            case MODE_JINGSONG:
                break;
            case MODE_GAOGUAI:
                break;

        }
    } catch (...) {
        LOGE("%s", "ERROR");
        goto end;
    }

    system->update();

    while (playing) {
        channel->isPlaying(&playing);
        usleep(1000 * 1000);
    }
    goto end;

    end:
    env->ReleaseStringUTFChars(path, input);
    sound->release();
    system->close();
    system->release();
 /*   System *system;
    Sound *sound;
    Channel *channel;
    DSP *dsp;
    bool playing = true;
    float frequency = 0;

    const char* path_cstr = env->GetStringUTFChars(path,NULL);
    LOGI("%s",path_cstr);
    try {
        //初始化
        System_Create(&system);
        system->init(32, FMOD_INIT_NORMAL, NULL);

        //创建声音
        system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sound);
        switch (mode) {
            case MODE_NORMAL:
                //原生播放
                system->playSound(sound, 0, false, &channel);
                LOGI("%s","fix normal");
                break;
            case MODE_LUOLI:
                //萝莉
                //DSP digital signal process
                //dsp -> 音效 创建fmod中预定义好的音效
                //FMOD_DSP_TYPE_PITCHSHIFT dsp，提升或者降低音调用的一种音效
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT,&dsp);
                //设置音调的参数
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,2.5);

                system->playSound(sound, 0, false, &channel);
                //添加到channel
                channel->addDSP(0,dsp);
                LOGI("%s","fix luoli");
                break;

            case MODE_JINGSONG:
                //惊悚
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO,&dsp);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 0.5);
                system->playSound(sound, 0, false, &channel);
                channel->addDSP(0,dsp);

                break;
            case MODE_DASHU:
                //大叔
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT,&dsp);
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,0.8);

                system->playSound(sound, 0, false, &channel);
                //添加到channel
                channel->addDSP(0,dsp);
                LOGI("%s","fix dashu");
                break;
            case MODE_GAOGUAI:
                //搞怪
                //提高说话的速度
                system->playSound(sound, 0, false, &channel);
                channel->getFrequency(&frequency);
                frequency = frequency * 1.6;
                channel->setFrequency(frequency);
                LOGI("%s","fix gaoguai");
                break;
            case MODE_KONGLING:
                //空灵
                system->createDSPByType(FMOD_DSP_TYPE_ECHO,&dsp);
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY,300);
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK,20);
                system->playSound(sound, 0, false, &channel);
                channel->addDSP(0,dsp);
                LOGI("%s","fix kongling");
                break;

            default:
                break;
        }
    } catch (...) {
        LOGE("%s","发生异常");
        goto end;
    }
    system->update();

    //释放资源
    //单位是微秒
    //每秒钟判断下是否在播放
    while(playing){
        channel->isPlaying(&playing);
        usleep(1000 * 1000);
    }
    goto end;
    end:
    env->ReleaseStringUTFChars(path,path_cstr);
    sound->release();
    system->close();
    system->release();*/
}


// 动态注册
static JNINativeMethod gMethods[] = {
        {"native_effect", "(Ljava/lang/String;I)V", (void *) SoundEffect},
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

    if (RegisterMethods(env) < 0) {
        LOGE("%s", "native registration failed\n");
        goto bail;
    }

    result = JNI_VERSION_1_6;
    LOGE("%s", "native registration success\n");
    bail:
    return result;
}