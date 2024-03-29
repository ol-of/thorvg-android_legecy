#include <jni.h>
#include <android/bitmap.h>
#include "LottieDrawable.h"

using namespace std;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_thorvg_android_LottieNative_nCreateLottie(JNIEnv *env, jclass clazz,
        jstring contentString, jint length, jintArray outValues) {
    if (tvg::Initializer::init(tvg::CanvasEngine::Sw, 3) != tvg::Result::Success) {
        return 0;
    }

    const char* inputStr = env->GetStringUTFChars(contentString, nullptr);
    auto* newData = new LottieDrawable::Data(inputStr, length);
    env->ReleaseStringUTFChars(contentString, inputStr);

    jint* contentInfo = env->GetIntArrayElements(outValues, nullptr);
    if (contentInfo != nullptr) {
        contentInfo[0] = (jint) newData->mAnimation->totalFrame();
        contentInfo[1] = (jint) newData->mAnimation->duration();
        env->ReleaseIntArrayElements(outValues, contentInfo, 0);
    }

    return reinterpret_cast<jlong>(newData);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_LottieNative_nSetLottieBufferSize(JNIEnv *env, jclass clazz,
        jlong lottiePtr, jobject bitmap, jfloat width, jfloat height) {
    if (lottiePtr == 0) {
        return;
    }

    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
    void *buffer;
    if (AndroidBitmap_lockPixels(env, bitmap, &buffer) >= 0) {
        data->setBufferSize((uint32_t *) buffer, width, height);
        AndroidBitmap_unlockPixels(env, bitmap);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_LottieNative_nDrawLottieFrame(JNIEnv *env, jclass clazz, jlong lottiePtr,
        jobject bitmap, jint frame) {
    if (lottiePtr == 0) {
        return;
    }

    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
    void *buffer;
    if (AndroidBitmap_lockPixels(env, bitmap, &buffer) >= 0) {
        data->draw(frame);
        AndroidBitmap_unlockPixels(env, bitmap);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_LottieNative_nDestroyLottie(JNIEnv *env, jclass clazz, jlong lottiePtr) {
    tvg::Initializer::term(tvg::CanvasEngine::Sw);

    if (lottiePtr == 0) {
        return;
    }

    auto* data = reinterpret_cast<LottieDrawable::Data*>(lottiePtr);
    delete data;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_thorvg_android_SVGNative_nCreateSVG(JNIEnv *env, jclass clazz, jstring content) {
    // TODO: implement nCreateSVG()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_SVGNative_nDestroySVG(JNIEnv *env, jclass clazz, jlong svgPtr) {
    tvg::Initializer::term(tvg::CanvasEngine::Sw);

    if (svgPtr == 0) {
        return;
    }

    // TODO: implement nDestroySVG()
}