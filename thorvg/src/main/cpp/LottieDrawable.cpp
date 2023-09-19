#include <thread>
#include <android/log.h>
#include "LottieDrawable.h"

LottieDrawable::Data::Data(const char *content, uint32_t length, float width, float height) {
    __android_log_print(ANDROID_LOG_INFO, "XXX", "Data::Data w=%f, h=%f", width, height);
    mContent = content;
    mContentLength = length;
    mWidth = width;
    mHeight = height;
}

void LottieDrawable::Data::init(uint32_t *buffer) {
    if (mCanvas != nullptr) {
        return;
    }
    __android_log_print(ANDROID_LOG_INFO, "XXX", "Data::init buffer=%p", buffer);

    if (tvg::Initializer::init(tvg::CanvasEngine::Sw, 3) == tvg::Result::Success) {
        // Create a mCanvas
        mCanvas = tvg::SwCanvas::gen();
        mCanvas->target(buffer, (uint32_t) mWidth, (uint32_t) mWidth, (uint32_t) mHeight,
                tvg::SwCanvas::ABGR8888);
        // Generate an animation
        mAnimation = tvg::Animation::gen();
        // Acquire a picture which associated with the animation.
        auto picture = mAnimation->picture();
        if (picture->load(mContent, mContentLength, "", false) != tvg::Result::Success) {
            __android_log_print(ANDROID_LOG_INFO, "XXX", "Error: Lottie is not supported. Did you enable Lottie Loader?");
            return;
        }
        picture->size(mWidth, mHeight);
        mCanvas->push(tvg::cast<tvg::Picture>(picture));
    }
}

void LottieDrawable::Data::draw(uint32_t frame) {
    if (!mCanvas) return;
    __android_log_print(ANDROID_LOG_INFO, "XXX", "Draw::draw mAnimation=%d", mAnimation->curFrame());
    mAnimation->frame(frame);
    mCanvas->update(mAnimation->picture());
    mCanvas->draw();
    mCanvas->sync();
}
