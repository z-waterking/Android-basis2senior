// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#ifndef _BYTED_EFFECT_BASE_DEFINE_H_
#define _BYTED_EFFECT_BASE_DEFINE_H_

#define _EFFECT_SDK_EXPORTS_

#	ifdef __cplusplus
#		ifdef _EFFECT_SDK_EXPORTS_
#			define BEF_SDK_API extern "C" __attribute__((visibility ("default")))
#		else
#			define BEF_SDK_API extern "C"
#		endif
#	else
#		ifdef _EFFECT_SDK_EXPORTS_
#			define BEF_SDK_API __attribute__((visibility ("default")))
#		else
#			define BEF_SDK_API
#		endif
#	endif


// define effect handle
typedef void* byted_effect_handle_t;

// define effect result
typedef int byted_effect_result_t;

// define return value
#define BEF_RESULT_SUC                       0  // 成功返回
#define BEF_RESULT_FAIL                     -1  // 内部错误
#define BEF_RESULT_FILE_NOT_FIND            -2  // 文件没找到
#define BEF_RESULT_FAIL_DATA_ERROR          -3  // 数据错误
#define BEF_RESULT_INVALID_HANDLE           -4  // 无效的句柄
#define BEF_RESULT_INVALID_LICENSE          -6  // 无效的授权
#define BEF_RESULT_INVALID_IMAGE_FORMAT     -7  // 无效的图片格式
#define BEF_RESULT_INVALID_PARAM_TYPE       -8  // 无效的参数类型
#define BEF_RESULT_INVALID_PARAM            -9  // 无效的参数

// define image rotate type
typedef enum {
  BEF_CLOCKWISE_ROTATE_0   = 0, // 图像不需要旋转，图像已为正图
  BEF_CLOCKWISE_ROTATE_90  = 1, // 图像需要顺时针旋转90度，使图像转正
  BEF_CLOCKWISE_ROTATE_180 = 2, // 图像需要顺时针旋转180度，使图像转正
  BEF_CLOCKWISE_ROTATE_270 = 3  // 图像需要顺时针旋转270度，使图像转正
} bef_rotate_type;


// define pixel format
typedef enum {
  BEF_PIX_FMT_RGBA8888, // RGBA 8:8:8:8 32bpp ( 4通道32bit RGBA 像素 )
  BEF_PIX_FMT_BGRA8888, // BGRA 8:8:8:8 32bpp ( 4通道32bit RGBA 像素 )
  BEF_PIX_FMT_BGR888,   // BGR 8:8:8 24bpp ( 3通道32bit RGB 像素 )
  BEF_PIX_FMT_RGB888,   // RGB 8:8:8 24bpp ( 3通道32bit RGB 像素 )
} bef_pixel_format;


typedef struct bef_fpoint_t {
  float x;
  float y;
} bef_fpoint;

typedef struct bef_rect_t {
  int left;   // Left most coordinate in rectangle. 矩形最左边的坐标
  int top;    // Top coordinate in rectangle.  矩形最上边的坐标
  int right;  // Right most coordinate in rectangle.  矩形最右边的坐标
  int bottom; // Bottom coordinate in rectangle. 矩形最下边的坐标
} bef_rect;

// Same definiation as bef_rect, but in float type
// 和bef_rect一样的定义，类型为单精度浮点
typedef struct bef_rectf_t {
  float left;
  float top;
  float right;
  float bottom;
} bef_rectf;


// 人脸相关定义

#define BEF_MAX_FACE_NUM  10

// 眼睛, 眉毛, 嘴唇更详细的关键点检测结果
// NOTE: 目前为保留字段, 无实现
typedef struct bef_face_ext_info_t {
  int eye_count;                  // 检测到眼睛数量
  int eyebrow_count;              // 检测到眉毛数量
  int lips_count;                 // 检测到嘴唇数量
  int iris_count;                 // 检测到虹膜数量

  bef_fpoint eye_left[22];        // 左眼关键点
  bef_fpoint eye_right[22];       // 右眼关键点
  bef_fpoint eyebrow_left[13];    // 左眉毛关键点
  bef_fpoint eyebrow_right[13];   // 右眉毛关键点
  bef_fpoint lips[64];            // 嘴唇关键点
  bef_fpoint left_iris[20];       // 左虹膜关键点
  bef_fpoint right_iris[20];      // 右虹膜关键点
} bef_face_ext_info;


// 供106点使用
typedef struct bef_face_106_st {
  bef_rect rect;                // 代表面部的矩形区域
  float score;                  // 置信度
  bef_fpoint points_array[106]; // 人脸106关键点的数组
  float visibility_array[106];  // 对应点的能见度，点未被遮挡1.0, 被遮挡0.0
  float yaw;                    // 水平转角,真实度量的左负右正
  float pitch;                  // 俯仰角,真实度量的上负下正
  float roll;                   // 旋转角,真实度量的左负右正
  float eye_dist;               // 两眼间距
  int ID;                       // 每个检测到的人脸拥有唯一的 faceID, 人脸跟踪丢失以后重新被检测到,会有一个新的faceID
  unsigned int action;          // 动作, 定义在 byted_effect_face_detect.h 里
} bef_face_106, *p_bef_face_106;

// 人脸检测结果
typedef struct bef_face_info_st {
  bef_face_106      base_infos[BEF_MAX_FACE_NUM];     // 检测到的人脸信息
  bef_face_ext_info extra_infos[BEF_MAX_FACE_NUM];    // 眼睛，眉毛，嘴唇关键点等额外的信息
  int face_count;                                     // 检测到的人脸数目
} bef_face_info, *p_bef_face_info;

#endif // _BYTED_EFFECT_BASE_DEFINE_H_
