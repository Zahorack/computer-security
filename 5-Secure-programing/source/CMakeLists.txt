cmake_minimum_required(VERSION 3.17)
project(source C)

set(CMAKE_C_STANDARD 99)

add_executable(source buffer_overflow.c format_string.c sprintf_vulnerability.c file_openning.c)