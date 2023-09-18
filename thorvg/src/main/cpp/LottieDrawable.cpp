#include <thread>
#include <android/log.h>
#include "LottieDrawable.h"

LottieDrawable::Data::Data(const LottieDrawable::Data &data) {
    buffer = data.buffer;
    content = data.content;
    strLength = data.strLength;
    width = data.width;
    height = data.height;
}

LottieDrawable::Data::Data(uint32_t * buffer, const char *content, uint32_t length, float width,
        float height) {
    this->buffer = buffer;
    this->content = content;
    this->strLength = length;
    this->width = width;
    this->height = height;

    auto threads = std::thread::hardware_concurrency();
    if (threads > 0) --threads;

    if (tvg::Initializer::init(tvg::CanvasEngine::Sw, threads) == tvg::Result::Success) {
        // Create a canvas
        canvas = tvg::SwCanvas::gen();
        canvas->target(buffer, (uint32_t) width, (uint32_t) width, (uint32_t) height,
                tvg::SwCanvas::ABGR8888);

        // Generate an animation
        animation = tvg::Animation::gen();

        // Acquire a picture which associated with the animation.
        auto picture = animation->picture();
        if (picture->load(content, strLength, "", false) != tvg::Result::Success) {
            __android_log_print(ANDROID_LOG_INFO, "XXX", "Error: Lottie is not supported. Did you enable Lottie Loader?");
            return;
        }
        picture->size(width, height);

        canvas->push(tvg::cast<tvg::Picture>(picture));

        tvg::Initializer::term(tvg::CanvasEngine::Sw);
    }
}

void LottieDrawable::Data::draw() {
    if (!canvas) return;
    canvas->draw();
    canvas->sync();
}
