#include <jni.h>
#include <string>
#include <cstdlib>
#include "include/log.h"
#include "FaceDetectHelper.h"
#include "include/libyuv/convert_argb.h"

using namespace std;

static jclass faceDetectHelperClass;
static jobject mObj;
static jmethodID detectFaceCallbackMethod = NULL;

static FaceDetectHelper *mFaceDetectHelper = NULL;

static JavaVM *mJavaVM;
static pthread_key_t mThreadKey;

//
static void Android_JNI_ThreadDestroyed(void *value);

//
//static int g_sensetimeGenCodeReturn = 0;
//
JNIEnv *Android_JNI_GetEnv(void);

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    mJavaVM = vm;
//    LOGI("===== JNI_OnLoad =====");
    if (mJavaVM->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
//        LOGE("Failed to get the environment using GetEnv()");
        return -1;
    }

    if (pthread_key_create(&mThreadKey, Android_JNI_ThreadDestroyed) != JNI_OK) {
//        LOGE("Error initializing pthread key");
    }

    Android_JNI_GetEnv();

    return JNI_VERSION_1_4;
}

static void Android_JNI_ThreadDestroyed(void *value) {
    JNIEnv *env = (JNIEnv *) value;
    if (env != NULL) {
        mJavaVM->DetachCurrentThread();
        pthread_setspecific(mThreadKey, NULL);
    }
}

JNIEnv *Android_JNI_GetEnv(void) {
    JNIEnv *env;
    int status = mJavaVM->AttachCurrentThread(&env, NULL);
    if (status < 0) {
//        LOGE("failed to attach current thread");
        return 0;
    }

    pthread_setspecific(mThreadKey, (void *) env);

    return env;
}

std::string jstring2string(JNIEnv *env, jstring src) {
    if (src == nullptr) {
        return std::string();
    }

    const char *charString = env->GetStringUTFChars(src, 0);
    std::string result(charString);
    env->ReleaseStringUTFChars(src, charString);
    return result;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeInit(JNIEnv *env,
                                                                       jobject instance) {
//    Android_JNI_GetEnv();

    faceDetectHelperClass = env->GetObjectClass(instance);

    if (faceDetectHelperClass != NULL) {
        //TODO zsf 一个I指的是一个int
        detectFaceCallbackMethod = env->GetStaticMethodID(faceDetectHelperClass,
                                                          "nativeOnFaceDetectedCallback", "(IIIII)V");
        if (detectFaceCallbackMethod == NULL) {
            LOGE("detectFaceCallbackMethod NULL");
        } else {
            LOGE("detectFaceCallbackMethod success");
        }
    }

    mObj = env->NewGlobalRef(faceDetectHelperClass);

    if (mFaceDetectHelper == NULL) {
        mFaceDetectHelper = new FaceDetectHelper();
        //TODO zsf4:回调函数
        mFaceDetectHelper->setDetectFaceCallback([](int ret, int top, int bottom, int left, int right) {
            JNIEnv *_env = Android_JNI_GetEnv();
            if (_env != NULL && detectFaceCallbackMethod && mObj != NULL) {
                LOGD("jni detectFaceCallbackMethod ret : %d", ret);
                //TODO zsf3:调用java的函数
                _env->CallStaticVoidMethod((jclass) mObj, detectFaceCallbackMethod, ret, top, bottom, left,
                        right);
            }
        });
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeDetectFace(JNIEnv *env,
                                                                             jobject instance,
                                                                             jbyteArray imageByteArr,
                                                                             jint pixelFormat,
                                                                             jint width,
                                                                             jint height,
                                                                             jint stride) {
    jboolean copy = 1;
    unsigned char *data = (unsigned char *) env->GetByteArrayElements(imageByteArr, &copy);

    int length = width * height * 4;
    unsigned char *rgbBuf = (unsigned char *) malloc(length);

    libyuv::NV21ToARGB(data, width, data + width * height, width, rgbBuf, width * 4, width, height);

//    for (int iHeight = 0; iHeight < height; iHeight++) {
//        for (int iWidth = 0; iWidth < width; iWidth++) {
//            unsigned char yValue = data[width * iHeight + iWidth];
//            int index = iWidth % 2 == 0 ? iWidth : iWidth - 1;
//            unsigned char vValue = data[width * height + width * (iHeight / 2) + index];
//            unsigned char uValue = data[width * height + width * (iHeight / 2) + index + 1];
//
//            double r = yValue + (1.370705 * (vValue - 128));
//            double g = yValue - (0.698001 * (vValue - 128)) - (0.337633 * (uValue - 128));
//            double b = yValue + (1.732446 * (uValue - 128));
//
//            r = r < 0 ? 0 : (r > 255 ? 255 : r);
//            g = g < 0 ? 0 : (g > 255 ? 255 : g);
//            b = b < 0 ? 0 : (b > 255 ? 255 : b);
//
//            rgbBuf[width * iHeight * 4 + iWidth * 4 + 0] = (unsigned char) r;
//            rgbBuf[width * iHeight * 4 + iWidth * 4 + 1] = (unsigned char) g;
//            rgbBuf[width * iHeight * 4 + iWidth * 4 + 2] = (unsigned char) b;
//            rgbBuf[width * iHeight * 4 + iWidth * 4 + 3] = 255;
//        }
//    }

    if (mFaceDetectHelper != NULL) {
        mFaceDetectHelper->detectFace(rgbBuf, pixelFormat, width, height, stride);
    }
    free(rgbBuf);
    env->ReleaseByteArrayElements(imageByteArr, (jbyte *) data, 0);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeSetFaceDetectModelPath(
        JNIEnv *env, jobject instance, jstring effectModePath_) {
    const char *effectModePath = env->GetStringUTFChars(effectModePath_, 0);

    if (mFaceDetectHelper != NULL) {
        mFaceDetectHelper->setDetectFaceModelPath(effectModePath);
    }

    env->ReleaseStringUTFChars(effectModePath_, effectModePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeSetLicense(JNIEnv *env,
                                                                             jobject instance,
                                                                             jstring license_) {
    const char *license = env->GetStringUTFChars(license_, 0);

    if (mFaceDetectHelper != NULL) {
        mFaceDetectHelper->setLicense(license);
    }

    env->ReleaseStringUTFChars(license_, license);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeUnInit(JNIEnv *env,
                                                                         jobject instance) {

    if (mFaceDetectHelper != NULL) {
        delete mFaceDetectHelper;
        mFaceDetectHelper = NULL;
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_bytedance_ies_camerarecorddemoapp_FaceDetectHelper_nativeTestWriteBmp(JNIEnv *env,
                                                                               jobject instance) {

    if (mFaceDetectHelper != NULL) {
        mFaceDetectHelper->writeBMP();
    }

}