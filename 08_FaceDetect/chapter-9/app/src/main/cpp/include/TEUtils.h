//
// Created by 郭鹤 on 2018/1/4.
//

#pragma once

class TEUtils {
public:

    static bool
    writeBMP2File(const char *c_strFilename, const void *pData, const int w, const int h,
                  const int nBytesPerPixel);
};
