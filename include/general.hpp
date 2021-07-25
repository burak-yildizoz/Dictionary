#ifndef GENERAL_HPP
#define GENERAL_HPP

#include <vector>
#include <algorithm>
#include <string>
#include <memory>
#include <stdexcept>

namespace general {

// https://stackoverflow.com/a/6194817/12447766
template <class T>
inline bool contains(const std::vector<T> &vec, const T &value)
{
    return std::find(vec.begin(), vec.end(), value) != vec.end();
}

// https://stackoverflow.com/a/24315631/12447766
inline void replace_all(std::string &str, const std::string& from, const std::string& to)
{
    size_t start_pos = 0;
    while ((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // Handles case where 'to' is a substring of 'from'
    }
}

// https://stackoverflow.com/a/26221725/12447766
template<typename ... Args>
inline std::string string_format(const std::string& format, Args ... args)
{
    int size_s = std::snprintf( nullptr, 0, format.c_str(), args ... ) + 1; // Extra space for '\0'
    if( size_s <= 0 ){ throw std::runtime_error( "Error during formatting." ); }
    auto size = static_cast<size_t>( size_s );
    auto buf = std::make_unique<char[]>( size );
    std::snprintf( buf.get(), size, format.c_str(), args ... );
    return std::string( buf.get(), buf.get() + size - 1 ); // We don't want the '\0' inside
}

} // namespace general

#endif // GENERAL_HPP
