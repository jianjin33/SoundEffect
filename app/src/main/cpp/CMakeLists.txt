# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


SET(PROJECT_ROOT_PATH "${CMAKE_SOURCE_DIR}/../../../../app")

add_library(effect-lib
        SHARED
        effect-lib.cpp
        effects.cpp
        common.cpp
        common_platform.cpp)

include_directories(src/main/cpp/inc)

# 添加libfmod动态库
add_library(fmod SHARED IMPORTED)
set_target_properties(fmod
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_ROOT_PATH}/libs/${ANDROID_ABI}/libfmod.so)

add_library(fmodl SHARED IMPORTED)
set_target_properties(fmodl
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_ROOT_PATH}/libs/${ANDROID_ABI}/libfmodL.so)

find_library(log-lib
        log)

target_link_libraries(effect-lib
        fmod
        fmodl
        ${log-lib})


