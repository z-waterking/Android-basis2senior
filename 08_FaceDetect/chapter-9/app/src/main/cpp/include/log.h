//
// Created by Steven on 2019/1/7.
//

#ifndef CAMERARECORDERDEMOAPP_LOG_H
#define CAMERARECORDERDEMOAPP_LOG_H

#include <android/log.h>

#define LOG_TAG "CameraRecorderDemoApp"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //CAMERARECORDERDEMOAPP_LOG_H
