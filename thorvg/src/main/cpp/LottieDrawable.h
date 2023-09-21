#ifndef THORVG_ANDROID_LOTTIEDRAWABLE_H
#define THORVG_ANDROID_LOTTIEDRAWABLE_H

#include <cstdint>
#include <iostream>
#include <thorvg.h>

namespace LottieDrawable {

    class Data {
    public:
        Data(const char* content, uint32_t strLength, float width, float height);
        ~Data() {
            tvg::Initializer::term(tvg::CanvasEngine::Sw);
        }
        void init(uint32_t* buffer);
        void draw(uint32_t frame);
        std::unique_ptr<tvg::Animation> mAnimation;
    private:
        std::unique_ptr<tvg::SwCanvas> mCanvas;
        const char* mContent;
        uint32_t mContentLength;
        float mWidth;
        float mHeight;
    };

} // namespace VectorDrawable

#endif //THORVG_ANDROID_LOTTIEDRAWABLE_H