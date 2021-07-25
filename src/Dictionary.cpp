#include <algorithm>
#include "Dictionary.hpp"
#include "general.hpp"

namespace dict {

std::vector<std::string> get_words_with_initials(std::string initials, bool is_tr)
{
    std::vector<std::string> content;
    if (initials.length() < 2)
    {
        return content;
    }
    std::stringstream ss = get_dictionary(is_tr);
    while (ss)
    {
        std::string word;
        ss >> word;
        if ((word.length() >= initials.length()) && (initials == word.substr(0, initials.length())))
            content.push_back(word);
        std::getline(ss, word);
    }
    return content;
}

std::vector<std::string> get_words_at_line(std::stringstream& ss)
{
    std::string str;
    std::getline(ss, str);
    std::stringstream ss_line(str);
    std::vector<std::string> words;
    while (std::getline(ss_line, str, '\t'))
        words.push_back(str);
    return words;
}

std::vector<std::string> get_meanings(std::string word, bool is_tr)
{
    std::vector<std::string> content;
    std::stringstream ss = get_dictionary(is_tr);
    while (ss)
    {
        std::string str;
        ss >> str;
        if (word == str)
        {
            content = get_words_at_line(ss);
            break;
        }
        std::getline(ss, str);
    }
    return content;
}

} // namespace dict
