#include "Dictionary.hpp"

namespace dict {

#include "en_tr.h"
#include "tr_en.h"

std::stringstream get_dictionary(bool is_tr)
{
    std::stringstream ss;
    ss << (is_tr ? TR_EN : EN_TR);
    return ss;
}

} // namespace dict
