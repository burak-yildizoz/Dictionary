#ifndef DICTIONARY_HPP
#define DICTIONARY_HPP

#include <sstream>
#include <vector>
#include <string>

namespace dict {

// each line contains TAB separated meanings of the word
// some word\tfirst meaning\tanother meaning\n
std::stringstream get_dictionary(bool is_tr);

// returns the words that start with *initials*
// "wor" -> word, world, worm, etc.
std::vector<std::string> get_words_with_initials(std::string initials, bool is_tr);

// calls getline and parses TAB separated values
std::vector<std::string> get_words_at_line(std::stringstream& ss);

// the meanings of the given word are sorted by the popularity of the meaning
std::vector<std::string> get_meanings(std::string word, bool is_tr);

} // namespace dict

#endif // DICTIONARY_HPP
