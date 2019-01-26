//
// Created by Steven on 2019/1/6.
//
#include "FaceDetectHelper.h"

void *bmp_write_thread(void *arg);

static bool isBmpWriteThreadStart;
static unsigned char *tmpImage;

static bool needWriteBMP = false;
static int w;
static int h;

FaceDetectHelper::FaceDetectHelper() {
//    initEffectFaceDetectHandler();
    int ret = 0;
    isBmpWriteThreadStart = true;
    ret = pthread_create(&bmpWriteThread, NULL, bmp_write_thread, NULL);
    if (ret) {
        LOGE("bmp_write_thread create fail");
    }
}

FaceDetectHelper::~FaceDetectHelper() {
    needWriteBMP = false;
    isBmpWriteThreadStart = false;
    pthread_join(bmpWriteThread, NULL);
    destroyEffectHandler();
}

void FaceDetectHelper::setDetectFaceModelPath(const char *modelPath) {
    mEffectModelPath = modelPath;
    initEffectFaceDetectHandler();
}

void FaceDetectHelper::initEffectFaceDetectHandler() {
    if (mEffectHandler == NULL) {
        byted_effect_result_t effect_result = byted_effect_face_detect_create(
                BEF_DETECT_FULL,
                mEffectModelPath,
                &mEffectHandler);
        if (effect_result != BEF_RESULT_SUC) {
            LOGE("byted_effect_face_detect_create fail , result is %d", effect_result);
            mEffectHandler = NULL;
            return;
        }

        LOGE("LICENSE 2 is %s", mEffectLicense);
        int len = strlen(mEffectLicense);
        byted_effect_result_t result = byted_effect_face_check_license(
                mEffectHandler,
                mEffectLicense,
                len
        );
        if (result != BEF_RESULT_SUC) {
            LOGE("byted_effect_face_check_license fail , result is %d, LICENSE is %s, length is %d",
                 result,
                 mEffectLicense, len);
            return;
        }
    }
}

void FaceDetectHelper::destroyEffectHandler() {
    if (mEffectHandler != NULL) {
        byted_effect_face_detect_destroy(mEffectHandler);
        mEffectHandler = NULL;
    }
}

void
FaceDetectHelper::detectFace(const unsigned char *image, int pixelFormat, int width, int height,
                             int stride) {

    w = width;
    h = height;

    LOGD("detectFace image is %s", image);
    if (mEffectHandler == NULL) {
        if (mDetectFaceCallback != NULL) {
            mDetectFaceCallback(-1, -1, -1, -1, -1);
        }
        return;
    }

    if (needWriteBMP) {
        if (tmpImage == nullptr) {
            int i = -1;
            while (i++, '\0' != image[i]);

            tmpImage = (unsigned char *) malloc(i);
            memcpy((unsigned char *) tmpImage, image, i);
        }
    }


    bef_face_info pFaceInfo;
    bef_rotate_type orientation = BEF_CLOCKWISE_ROTATE_270;
    bef_pixel_format pixel_format = BEF_PIX_FMT_RGBA8888;//TODO: pixelFormat
    byted_effect_result_t result = byted_effect_face_detect(
            mEffectHandler,
            image,
            pixel_format,
            width,
            height,
            stride,
            orientation,
            BEF_DETECT_MODE_VIDEO | BEF_DETECT_FULL,
            &pFaceInfo
    );

    if (result == BEF_RESULT_SUC) {
        if (mDetectFaceCallback != NULL) {
            LOGD("byted_effect_face_detect face count is %d", pFaceInfo.face_count);
            if (pFaceInfo.face_count > 0) {
                int len = sizeof(pFaceInfo.base_infos) / sizeof(pFaceInfo.base_infos[0]);
                LOGD("byted_effect_face_detect face info size : %d", len);
                for (int i = 0; i < len; ++i) {
                    bef_face_106 item = pFaceInfo.base_infos[i];
                    LOGD("byted_effect_face_detect face info action : %d - %d", i, item.action);
                    if (item.action > 0) {
                        //TODO: add face rect point
                        //TODO: zsf
                        mDetectFaceCallback(item.action, item.rect.top, item.rect.bottom, item.rect.left,
                                item.rect.right);
                    }
                }
            }
        }
    } else {
        LOGE("byted_effect_face_detect fail, result is %d", result);
        if (mDetectFaceCallback != NULL) {
            mDetectFaceCallback(-2, -1, -1, -1, -1);
        }
    }
}

void FaceDetectHelper::writeBMP() {
    needWriteBMP = true;
}

int bmp_write(unsigned char *image, int imageWidth, int imageHeight, char *filename) {
    unsigned char header[54] = {
            0x42, 0x4d, 0, 0, 0, 0, 0, 0, 0, 0,
            54, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 32, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0
    };

    long file_size = (long) imageWidth * (long) imageHeight * 4 + 54;
    header[2] = (unsigned char) (file_size & 0x000000ff);
    header[3] = (file_size >> 8) & 0x000000ff;
    header[4] = (file_size >> 16) & 0x000000ff;
    header[5] = (file_size >> 24) & 0x000000ff;

    long width = imageWidth;
    header[18] = width & 0x000000ff;
    header[19] = (width >> 8) & 0x000000ff;
    header[20] = (width >> 16) & 0x000000ff;
    header[21] = (width >> 24) & 0x000000ff;

    long height = imageHeight;
    header[22] = height & 0x000000ff;
    header[23] = (height >> 8) & 0x000000ff;
    header[24] = (height >> 16) & 0x000000ff;
    header[25] = (height >> 24) & 0x000000ff;

    char fname_bmp[128];
    sprintf(fname_bmp, "%s.bmp", filename);

    FILE *fp;
    if (!(fp = fopen(fname_bmp, "wb")))
        return -1;

    fwrite(header, sizeof(unsigned char), 54, fp);
    fwrite(image, sizeof(unsigned char), (size_t) (long) imageWidth * imageHeight * 4, fp);

    fclose(fp);
    return 0;
}

//直接调用这个函数就行了，返回值最好是int64_t，long long应该也可以
int64_t getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);    //该函数在sys/time.h头文件中
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

void *bmp_write_thread(void *arg) {
    while (isBmpWriteThreadStart) {
        if (needWriteBMP && tmpImage != nullptr) {
            LOGD("write bmp begin");
            char tmpPath;
            sprintf(&tmpPath, "/sdcard/DCIM/Camera/tmpbmp_%d.bmp", (int) getCurrentTime());
            LOGD("write bmp tmp path is %s", &tmpPath);
//            bmp_write(tmpImage, w, h, &tmpPath);
            TEUtils::writeBMP2File(&tmpPath, tmpImage, w, h, 4);
            free(tmpImage);
            tmpImage = nullptr;
            needWriteBMP = false;
            LOGD("write bmp finish");
        }
    }
    return NULL;
}

