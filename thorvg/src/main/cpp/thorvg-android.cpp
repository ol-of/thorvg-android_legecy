#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include "LottieDrawable.h"

using namespace std;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_thorvg_android_NativeLib_nCreateLottie(JNIEnv *env, jclass clazz, jstring contentString,
        jint length, jfloat width, jfloat height) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "nCreateLottie");

    const char* inputStr = env->GetStringUTFChars(contentString, nullptr);
    auto* newData = new LottieDrawable::Data(inputStr, length, width, height);
    env->ReleaseStringUTFChars(contentString, inputStr);
    return reinterpret_cast<jlong>(newData);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_NativeLib_nDrawLottieFrame(JNIEnv *env, jclass clazz, jlong lottiePtr,
        jobject bitmap, jint frame) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "nDrawLottie");

    if (lottiePtr == 0) {
        return;
    }
    void *buffer;
    if (AndroidBitmap_lockPixels(env, bitmap, &buffer) >= 0) {
        AndroidBitmap_unlockPixels(env, bitmap);
    }
    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
    data->init((uint32_t *) buffer);
    data->draw();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_NativeLib_nDestroyLottie(JNIEnv *env, jclass clazz, jlong lottiePtr) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "nDestroyLottie");

    if (lottiePtr == 0) {
        return;
    }
    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
    delete data;
}