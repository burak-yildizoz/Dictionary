#include <iostream>
#include <locale>
#include "Dictionary.hpp"
#include "general.hpp"

int main()
{
    std::locale::global(std::locale("tr_TR.UTF-8"));
    bool is_tr = false;
    while (true)
    {
        std::cout << (is_tr ? "Aranacak kelimeyi girin" : "Submit the word to be searched") << ": ";
        std::string str;
        std::cin >> str;
        if (str == "q")
            break;
        if (str == "tr")
        {
            std::cout << "Türkçe dili seçildi." << std::endl;
            is_tr = true;
            continue;
        }
        if (str == "en")
        {
            std::cout << "Switched to English." << std::endl;
            is_tr = false;
            continue;
        }
        std::vector<std::string> meanings = dict::get_meanings(str, is_tr);
        for (const std::string& meaning : meanings)
            std::cout << meaning << "\n";
        std::cout << std::endl;
        std::cout << (is_tr ? (str + " ile başlayan kelimeler")
                            : ("Words that start with " + str)) << ":\n";
        std::vector<std::string> similars = dict::get_words_with_initials(str, is_tr);
        for (const std::string& similar : similars)
            std::cout << similar << "\n";
        std::cout << std::endl;
    }
    return 0;
}
