import os

is_tr = False
# read words
translate_files = ['en_tr.txt', 'tr_en.txt']
with open(translate_files[is_tr], mode='r', encoding='utf-8') as f:
    lines = f.read().splitlines()
# create separate file for each the first two-letters
foldername = 'tr/' if is_tr else 'en/'
os.makedirs(foldername, exist_ok=True)
for line in lines:
    word = line.split('\t')[0]
    if len(word) < 2:
        continue
    os.makedirs(os.path.join(foldername, word[0]), exist_ok=True)
    with open(os.path.join(foldername, word[0], word[1] + '.txt'), mode='a', encoding='utf-8') as f:
        f.write('%s\n' % line)
