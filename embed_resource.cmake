# https://gist.github.com/amir-saniyan/de99cee82fa9d8d615bb69f3f53b6004

####################################################################################################
# This function converts any file into C/C++ source code.
# Example:
# - input file: data.dat
# - output file: data.h
# - variable name declared in output file: DATA
# - data length: sizeof(DATA)
# embed_resource("data.dat" "data.h" "DATA")
####################################################################################################

function(embed_resource resource_file_name source_file_name variable_name)
    message(STATUS "Creating ${source_file_name}")
    file(READ ${resource_file_name} hex_content HEX)
    string(REPEAT "[0-9a-f]" 32 column_pattern)
    string(REGEX REPLACE "(${column_pattern})" "\\1\n" content "${hex_content}")
    string(REGEX REPLACE "([0-9a-f][0-9a-f])" "0x\\1, " content "${content}")
    string(REGEX REPLACE ", $" "" content "${content}")
    set(array_definition "static const unsigned char ${variable_name}[] =\n{\n${content}\n};")
    set(source "// Auto generated file.\n${array_definition}\n")
    file(WRITE "${source_file_name}" "${source}")
endfunction()

function(embed_resource_once resource_file_name source_file_name variable_name)
    if(NOT EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/${source_file_name}")
        embed_resource(${resource_file_name} ${source_file_name} ${variable_name})
    else()
        message(STATUS "Auto-generated file exists: ${source_file_name}")
    endif()
endfunction()
