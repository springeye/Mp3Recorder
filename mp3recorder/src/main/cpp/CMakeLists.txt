cmake_minimum_required(VERSION 3.10.2)
PROJECT(mp3lame)
set(CMAKE_CXX_STANDARD 17)
add_definitions(-DSTDC_HEADERS)
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -s -O3 -w")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -s -O3 -w")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -w -g -fexceptions")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -w -g -fexceptions")
file(GLOB_RECURSE SOURCE_FILES
#        *.c
#        *.h
        lame-3.98.4_libmp3lame/*.c
        lame-3.98.4_libmp3lame/*.h
        )
add_library(mp3lame
        SHARED
        ${SOURCE_FILES})