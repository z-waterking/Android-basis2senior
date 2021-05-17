# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(CC_IGNORE_OPTIONS "-Wno-unused-variable -Wno-unused-parameter -Wno-unused-function -Wno-unused-private-field")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -UNDEBUG -frtti -fexceptions -std=c++11 -g -fpermissive -pedantic -D__STDC_FORMAT_MACROS -Wall ${CC_IGNORE_OPTIONS} -Wreturn-type")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DGL_GLEXT_PROTOTYPES")

add_library( libeffect
        SHARED
        IMPORTED )
set_target_properties( # Specifies the target library.
        libeffect
        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION
        # Provides the path to the library you want to import.
        "${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libeffect.so")

add_library( libyuv
        SHARED
        IMPORTED )
set_target_properties( # Specifies the target library.
        libyuv
        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION
        # Provides the path to the library you want to import.
        "${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libyuv.so")

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
        src/main/cpp/TEUtils.cpp
        src/main/cpp/facedetect/FaceDetectHelper.cpp)

include_directories(
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include/libyuv
        ${CMAKE_SOURCE_DIR}/src/main/cpp/facedetect
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        libyuv libeffect ${log-lib})