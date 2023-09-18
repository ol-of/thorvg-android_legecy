#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include "LottieDrawable.h"

using namespace std;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_thorvg_android_NativeLib_nCreateLottie(JNIEnv *env, jclass clazz, jobject bitmap,
        jstring contentString, jint length, jfloat width, jfloat height) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "nCreateLottie");

    void *buffer;
    if (AndroidBitmap_lockPixels(env, bitmap, &buffer) >= 0) {
        AndroidBitmap_unlockPixels(env, bitmap);
    }

    const char* inputStr = env->GetStringUTFChars(contentString, nullptr);
    auto* newData = new LottieDrawable::Data((uint32_t *) buffer, inputStr, length, width, height);
    env->ReleaseStringUTFChars(contentString, inputStr);
    return reinterpret_cast<jlong>(newData);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_NativeLib_nDrawLottie(JNIEnv *env, jclass clazz, jlong lottiePtr) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "nDrawLottie");
    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
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