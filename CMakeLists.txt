project(Dictionary)
cmake_minimum_required(VERSION 2.8)

include(embed_resource.cmake)
embed_resource_once("res/en_tr.txt" "include/en_tr.h" "EN_TR")
embed_resource_once("res/tr_en.txt" "include/tr_en.h" "TR_EN")

include_directories("include")
set(SOURCES "src/Dictionary.cpp" "src/get_dictionary.cpp")

add_executable(Dictionary main.cpp ${SOURCES})
target_link_libraries(Dictionary)
