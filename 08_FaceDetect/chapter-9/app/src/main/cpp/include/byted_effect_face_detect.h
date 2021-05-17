// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#ifndef _BYTED_EFFECT_FACE_DETECT_H_
#define _BYTED_EFFECT_FACE_DETECT_H_

#include "byted_effect_base_define.h"


// 设置检测模式, 在调用 byted_effect_face_detect 函数的时候设定
#define BEF_DETECT_MODE_VIDEO  0x00020000  // video detect, 视频检测
#define BEF_DETECT_MODE_IMAGE  0x00040000  // image detect, 图片检测

// 检测行为定义, 在调用 byted_effect_face_detect 函数的时候设定
// 当前版本张嘴、摇头、点头、挑眉默认都开启，设置相关的位不生效，眨眼和嘟嘴需要显示进行设置
// 注意：眨眼和嘟嘴功能只有在 create 的时候开启了，detect 的时候才能使用，
//      如果 create 没开启则 detect 无法使用，如果强制使用，会造成崩溃 
#define BEF_FACE_DETECT 0x00000001  // 106 key points face detect, 106 点人脸检测

#define BEF_EYE_BLINK   0x00000002  // eye blink, 眨眼
#define BEF_MOUTH_AH    0x00000004  // mouth open, 嘴巴大张
#define BEF_HEAD_YAW    0x00000008  // shake head, 摇头
#define BEF_HEAD_PITCH  0x00000010  // nod, 点头
#define BEF_BROW_JUMP   0x00000020  // wiggle eyebrow, 眉毛挑动
#define BEF_MOUTH_POUT  0x00000040  // pout, 嘴巴嘟嘴

#define BEF_DETECT_FULL 0x0000007F  // 检测上面所有的动作

/**
 * @brief 创建人脸检测的句柄
 * @param [in] config     人脸检测算法的配置，设置需要开启的动作特征
 *                        如 BEF_FACE_DETECT 或 BEF_DETECT_FULL 或 BEF_FACE_DETECT | BEF_MOUTH_POUT 等
 * @param [in] model_path 模型文件路径
 * @param [out] handle    Created face detect handle
 *                        创建的人脸检测句柄
 * @return If succeed return BEF_RESULT_SUC, other value please see byted_effect_base_define.h
 *         成功返回 BEF_RESULT_SUC, 失败返回相应错误码, 具体请参考 byted_effect_base_define.h
 */
BEF_SDK_API byted_effect_result_t
byted_effect_face_detect_create(
  unsigned long long config,
  const char *model_path,
  byted_effect_handle_t *handle
);

/**
 * @brief 人脸检测
 * @param [in] handle Created face detect handle
 *                    已创建的人脸检测句柄
 * @param [in] image Image base address
 *                   输入图片的数据指针
 * @param [in] pixel_format Pixel format of input image
 *                          输入图片的格式
 * @param [in] image_width  Image width
 *                          输入图像的宽度 (以像素为单位)
 * @param [in] image_height Image height
 *                          输入图像的高度 (以像素为单位)
 * @param [in] image_stride Image stride in each row
 *                          输入图像每一行的步长 (以像素为单位)
 * @param [in] orientation  Image orientation
 *                          输入图像的转向，具体请参考 byted_effect_base_define.h 中的 bef_rotate_type
 * @param [in] detect_config 人脸检测相关的配置, 如 BEF_DETECT_MODE_VIDEO | BEF_FACE_DETECT
 *                           如果要检测是否有眨眼或嘟嘴动作，需要在 byted_effect_face_detect_create 函数
 *                           的 config 中指定，否则会崩溃
 * @return If succeed return BEF_RESULT_SUC, other value please see byted_effect_base_define.h
 *         成功返回 BEF_RESULT_SUC, 失败返回相应错误码, 具体请参考 byted_effect_base_define.h
 */
BEF_SDK_API byted_effect_result_t
byted_effect_face_detect(
  byted_effect_handle_t handle,
  const unsigned char *image,
  bef_pixel_format pixel_format,
  int image_width,
  int image_height,
  int image_stride,
  bef_rotate_type orientation,
  unsigned long long detect_config,
  bef_face_info *p_face_info
);

typedef enum {
  // 设置tracker每多少帧进行一次detect(默认值有人脸时24, 无人脸时24/3=10), 值越大, cpu占用率越低, 但检测出新人脸的时间越长.
  BEF_FACE_PARAM_FACE_DETECT_INTERVAL = 1, // default 24
  // 设置检测到的最大人脸数目N (默认值10), 持续跟踪已检测到的N个人脸直到人脸数小于N再继续做检测. 值越大, 但相应耗时越长.
  BEF_FACE_PARAM_MAX_FACE_NUM = 2, // default 5
} bef_face_detect_type;


/**
 * @brief Set face detect parameter based on type 设置人脸检测的相关参数
 * @param [in] handle Created face detect handle
 *                    已创建的人脸检测句柄
 * @param [in] type Face detect type that needs to be set, check bef_face_detect_type for the detailed
 *                  需要设置的人体检测类型，可参考 bef_face_detect_type
 * @param [in] value Type value, check bef_face_detect_type for the detailed
 *                   参数值, 具体请参数 bef_face_detect_type 枚举中的说明
 * @return If succeed return BEF_RESULT_SUC, other value please refer byted_effect_base_define.h
 *         成功返回 BEF_RESULT_SUC, 失败返回相应错误码, 具体请参考 byted_effect_base_define.h
 */
BEF_SDK_API byted_effect_result_t
byted_effect_face_detect_setparam(
  byted_effect_handle_t handle,
  bef_face_detect_type type,
  float value
);

/**
 * @param [in] handle Destroy the created face detect handle
 *                    销毁创建的人脸检测句柄
 */
BEF_SDK_API void
byted_effect_face_detect_destroy(
  byted_effect_handle_t handle
);

/**
 * @brief 人脸检测授权
 * @param [in] handle Created face detect handle
 *                    已创建的人脸检测句柄
 * @param [in] license 授权文件字符串
 * @param [in] length  授权文件字符串长度
 * @return If succeed return BEF_RESULT_SUC, other value please refer byted_effect_base_define.h
 *         成功返回 BEF_RESULT_SUC, 授权码非法返回BEF_RESULT_INVALID_LICENSE，其它失败返回相应错误码, 具体请参考 byted_effect_base_define.h
 */
BEF_SDK_API byted_effect_result_t
byted_effect_face_check_license(
  byted_effect_handle_t handle,
  const char *license,
  int length
);


#endif // _BYTED_EFFECT_FACE_DETECT_H_
