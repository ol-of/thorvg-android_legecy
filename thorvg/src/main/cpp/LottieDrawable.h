#ifndef THORVG_ANDROID_LOTTIEDRAWABLE_H
#define THORVG_ANDROID_LOTTIEDRAWABLE_H

#include <cstdint>
#include <iostream>
#include <thorvg.h>

namespace LottieDrawable {

    class Data {
    public:
        Data(const Data& data);
        Data(uint32_t * buffer, const char* content, uint32_t strLength, float width, float height);
        Data() {}
        void draw();
    private:
        const char* content;
        uint32_t strLength;
        uint32_t* buffer;
        float width;
        float height;
        std::unique_ptr<tvg::SwCanvas> canvas;
        std::unique_ptr<tvg::Animation> animation;
    };

} // namespace VectorDrawable

#endif //THORVG_ANDROID_LOTTIEDRAWABLE_H