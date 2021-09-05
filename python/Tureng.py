from bs4 import BeautifulSoup
from urllib.parse import quote
from urllib.request import urlopen
from shutil import copyfile
import time
import os.path
from statistics import mean
import copy

class Average:
    def __init__(self, N=None):
        self._N = N
        self.values = []

    def add(self, x):
        self.values.append(x)
        if self._N is not None and len(self.values) > self._N:
            self.values.pop()

    def avg(self):
        return mean(self.values)


def get_soup_file(filename):
    soup = BeautifulSoup(open(filename), 'html.parser')
    return soup


def get_soup_url(urlname):
    page = urlopen(urlname)
    soup = BeautifulSoup(page, 'html.parser')
    return soup


def lower_tr(word):
    for key, value in {
        u"I": u"ı",
        u"İ": u"i",
    }.items():
        word = word.replace(key, value)
    return word.lower()


def sort_file_content(filename, is_tr):
    origname = filename + '.orig'
    if not os.path.isfile(origname):
        copyfile(filename, origname)
        print('Copied %s to %s' % (filename, origname))
    with open(origname, mode='r', encoding='utf-8') as f:
        lines = f.read().splitlines()
    lines = list(map(lower_tr if is_tr else str.lower, lines))
    lines.sort()
    with open(filename, mode='w', encoding='utf-8') as f:
        for line in lines:
            f.write('%s\n' % line)
    print('Sorted %s' % filename)


# https://stackoverflow.com/questions/12214801/print-a-string-as-hexadecimal-bytes
def str_as_hex(s):
    return ':'.join('{:02x}'.format(ord(c)) for c in s)


def get_meaning(word, is_tr):
    assert type(is_tr) is bool
    url = 'https://tureng.com/tr/turkce-ingilizce/'
    classes = ['en tm', 'tr ts']
    languages = ['İngilizce', 'Türkçe']
    soup = get_soup_url(url + quote(word))
    meanings = []
    for table in soup.body.find_all('table', id='englishResultsTable'):
        use_table = False
        for th in copy.copy(table.find_all('th')):
            if th['class'][0] == 'c2':
                if th.get_text(strip=True) == languages[is_tr]:
                    use_table = True
                break
        if not use_table:
            continue
        for tr in table.find_all('tr'):
            td1 = tr.find('td', attrs={'class': classes[is_tr]})
            if td1 is not None:
                w = td1.a.get_text(strip=True)
                if td1.a.get_text(strip=True) == word:
                    td2 = tr.find('td', attrs={'class': classes[~is_tr]})
                    meanings.append(td2.a.get_text(strip=True))
    return meanings


def pretty_remaining_time(t):
    # return '%.0f sec' % t
    import arrow
    return arrow.utcnow().shift(seconds=t).humanize()


def fetch_translation(is_tr):
    assert type(is_tr) is bool
    word_files = ['en.txt', 'tr.txt']
    translate_files = ['en_tr.txt', 'tr_en.txt']
    # read all words
    sort_file_content(word_files[is_tr], is_tr)
    with open(word_files[is_tr], mode='r', encoding='utf-8') as f:
        lines = f.read().splitlines()
    # load last translated word
    last_word, num_words = lines[0], 0
    try:
        with open(translate_files[is_tr], mode='r', encoding='utf-8') as f:
            tmp_lines = f.read().splitlines()
            if len(tmp_lines):
                last_word = tmp_lines[-1]
                num_words = len(tmp_lines)
            del tmp_lines
    except IOError:
        pass
    # continue translating the words
    with open(translate_files[is_tr], mode='a', encoding='utf-8') as f:
        time_avg = Average(20)
        for i, word in enumerate(lines):
            # skip to the last word
            if word <= last_word:
                # print('%d/%d  Passing %s' % (i + 1, len(lines), word))
                continue
            # fetch data and measure time
            t = time.time()
            meanings = get_meaning(word, is_tr)
            t = time.time() - t
            time_avg.add(t)
            # write data if the word is valid
            if len(meanings):
                f.write(word)
                for meaning in meanings:
                    f.write('\t%s' % meaning)
                f.write('\n')
                num_words += 1
            print('%d/%d (%.0f%%) (total words: %d) (ETA: %s)  %s has %d meanings' % (i + 1, len(lines), 100 * ((i + 1) /
                  len(lines)), num_words, pretty_remaining_time((len(lines) - i) * time_avg.avg()), word, len(meanings)), flush=True)


# English words (~168k)
# https://raw.githubusercontent.com/mb2g17/PyLex/master/resources/words1.txt
# English words (~467k)
# https://raw.githubusercontent.com/dwyl/english-words/master/words.txt
# Turkish words (~64k)
# https://raw.githubusercontent.com/mertemin/turkish-word-list/master/words.txt
if __name__ == '__main__':
    # print(get_meaning('word', is_tr=False))
    # sort_file_content('tr.txt', is_tr=True)
    while True:
        restart = False
        try:
            fetch_translation(is_tr=True)
        except:
            time.sleep(1)
            restart = True
        if not restart:
            break
