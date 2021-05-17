#include <cstdio>
#include <cstdlib>

#include "TEUtils.h"
#include "include/log.h"

const static char *TAG = "TEUtils";

struct BitmapFileHeader {
    uint16_t type;
    uint32_t size;
    uint16_t reserved1;
    uint16_t reserved2;
    uint32_t off_bits;

    uint32_t Size() {
        return sizeof(type) +
               sizeof(size) +
               sizeof(reserved1) +
               sizeof(reserved2) +
               sizeof(off_bits);
    }

    void Write(FILE *fp) {
        if (fp == nullptr) {
            return;
        }
        fwrite(&type, 2, 1, fp);
        fwrite(&size, 4, 1, fp);
        fwrite(&reserved1, 2, 1, fp);
        fwrite(&reserved2, 2, 1, fp);
        fwrite(&off_bits, 4, 1, fp);
    }
};

struct BitmapInfoHeader {
    uint32_t size;
    uint32_t width;
    uint32_t height;
    uint16_t planes;

    uint16_t bit_count;
    uint32_t compression;
    uint32_t size_image;
    uint32_t x_pels_per_meter;

    uint32_t y_pels_per_meter;
    uint32_t clr_used;
    uint32_t clr_important;

    uint32_t Size() {
        return sizeof(size) +
               sizeof(width) +
               sizeof(height) +
               sizeof(planes) +
               sizeof(bit_count) +
               sizeof(compression) +
               sizeof(size_image) +
               sizeof(x_pels_per_meter) +
               sizeof(y_pels_per_meter) +
               sizeof(clr_used) +
               sizeof(clr_important);
    }

    void Write(FILE *fp) {
        if (fp == nullptr) {
            return;
        }

        fwrite(&size, 4, 1, fp);
        fwrite(&width, 4, 1, fp);
        fwrite(&height, 4, 1, fp);
        fwrite(&planes, 2, 1, fp);

        fwrite(&bit_count, 2, 1, fp);
        fwrite(&compression, 4, 1, fp);
        fwrite(&size_image, 4, 1, fp);
        fwrite(&x_pels_per_meter, 4, 1, fp);

        fwrite(&y_pels_per_meter, 4, 1, fp);
        fwrite(&clr_used, 4, 1, fp);
        fwrite(&clr_important, 4, 1, fp);
    }
};

bool
TEUtils::writeBMP2File(const char *filename, const void *data, const int w, const int h,
                       const int bytesPerPixel) {
    if (filename == nullptr || data == nullptr || w <= 0 || h <= 0 || bytesPerPixel <= 0) {
        return false;
    }

    FILE *fp = fopen(filename, "wb+");
    if (fp == nullptr) {
        LOGE("Can not open file : %s!", filename);
        return 0;
    }

    BitmapFileHeader fileHeader = {0x424d, 0, 0, 0, 0};
    BitmapInfoHeader infoHeader = {40, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};

    int dataSize = w * h * bytesPerPixel;
    int fileSize = dataSize + sizeof(fileHeader) + sizeof(infoHeader);

    fileHeader.type = 0x4d42;       //
    fileHeader.size = fileSize;
    fileHeader.reserved1 = 0;
    fileHeader.reserved1 = 0;
    fileHeader.off_bits = fileHeader.Size() + infoHeader.Size();

    infoHeader.size = 40;
    infoHeader.width = w;
    infoHeader.height = h;
    infoHeader.planes = 1;
    infoHeader.bit_count = bytesPerPixel * 8;
    infoHeader.compression = 0;
    infoHeader.size_image = (w * bytesPerPixel * 8u + 31u) / 32u * 4u * h;
    infoHeader.x_pels_per_meter = 0;
    infoHeader.y_pels_per_meter = 0;
    infoHeader.clr_used = 0;
    infoHeader.clr_important = 0;

    fileHeader.Write(fp);
    infoHeader.Write(fp);
    fwrite(data, dataSize, 1, fp);

    fclose(fp);

    return true;
}


