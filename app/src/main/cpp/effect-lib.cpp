//
// Created by Administrator on 2019/11/19.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include "inc/fmod.hpp"
#include "common.h"

#define CLASSPATH "com/jianjin33/soundeffect/MainActivity"
#define LOG_TAG "SoundEffect-JNI"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)

using namespace std;


extern "C" JNIEXPORT void JNICALL
SoundEffect(JNIEnv *env, jobject thiz, jstring path) {
    const char *input = env->GetStringUTFChars(path, JNI_FALSE);
    if (input) {
        LOGI("%d", input);
    }
}


// 动态注册
static JNINativeMethod gMethods[] = {
        {"native_effect", "(Ljava/lang/String;)V", (void *) SoundEffect},
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