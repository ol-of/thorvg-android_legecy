#include <jni.h>
#include <string>
#include <cstdio>
#include <iostream>
#include <thread>
#include <thorvg.h>
#include <android/bitmap.h>
#include <android/log.h>

using namespace std;

static unique_ptr<tvg::SwCanvas> canvas;
static unique_ptr<tvg::Animation> animation;

extern "C"
JNIEXPORT void JNICALL
Java_com_thorvg_android_NativeLib_nRenderLottie(JNIEnv *env, jclass clazz,
        jstring json_content, jint content_size, jobject bitmap, jfloat width, jfloat height) {
    auto threads = std::thread::hardware_concurrency();
    if (threads > 0) --threads;

    if (tvg::Initializer::init(tvg::CanvasEngine::Sw, threads) == tvg::Result::Success) {
        void *buffer;
        if (AndroidBitmap_lockPixels(env, bitmap, &buffer) >= 0) {
            // Create a Canvas
            canvas = tvg::SwCanvas::gen();
            canvas->target((uint32_t *) buffer, (uint32_t) width, (uint32_t) width,
                           (uint32_t) height, tvg::SwCanvas::ABGR8888);
            if (!canvas) return;

            // Generate an animation
            animation = tvg::Animation::gen();
            // Acquire a picture which associated with the animation.
            auto picture = animation->picture();

            const char *content = env->GetStringUTFChars(json_content, nullptr);
            if (picture->load(content, content_size, false) != tvg::Result::Success) {
                env->ReleaseStringUTFChars(json_content, content);
                __android_log_print(ANDROID_LOG_INFO, "XXX", "Error: Lottie is not supported. Did you enable Lottie Loader?");
                return;
            }
            env->ReleaseStringUTFChars(json_content, content);

            picture->size(width, height);

            canvas->push(tvg::cast<tvg::Picture>(picture));
            canvas->draw();
            canvas->sync();

            AndroidBitmap_unlockPixels(env, bitmap);
        }

        tvg::Initializer::term(tvg::CanvasEngine::Sw);
    }
}