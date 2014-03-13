from __future__ import division
from collections import OrderedDict

stopword = []
fobj = open('/Users/SanDomingo/Workspace/tryout/html-slicer/src/main/resources/stopwords.txt')
for line in fobj:
    line = line.strip()
    stopword.append(line)

def sort_dic_byvalue(adic, rev=False):
    return OrderedDict(sorted(adic.iteritems(), key=lambda d:d[1], reverse=rev))

def wordcount(filename):
    '''
    word count(except stop words)
    file format:
    # Name
    Bios (may across multi line)
    # Name 2
    '''
    fobj = open(filename)
    bios = {}
    name = text = ''
    while True:
        line = fobj.readline()
        if line == '':
            break
        line = line.strip()
        if line.find('#') == 0: # find head
            name = line[1:].strip()
            bios[name] = ''
        else:
            if line != '':
                bios[name] = bios[name] + line
    wcmap = {}
    totallen = 0;
    for v in bios.values():
        words = v.split(' ')
        totallen += len(words)
        for w in words:
            w = w.strip()
            if w != '' and w not in stopword:
                if w in wcmap:
                    wcmap[w] = wcmap[w]+1
                else :
                    wcmap[w] = 1
    # sort map
    wcmap = sort_dic_byvalue(wcmap, True)
    print wcmap
    print 'avg bio len: ', totallen / len(bios)
    return wcmap

if __name__ == "__main__":
    filename = '/Users/SanDomingo/Workspace/tryout/html-slicer/artnetminer/bio.txt'
    adic = wordcount(filename)
    out = open('/Users/SanDomingo/Workspace/tryout/html-slicer/src/main/resources/biowords.txt', 'w')
    countdown = 200
    for k,v in adic.items():
        out.write(k)
        out.write('\n')
        countdown -= 1
        if countdown == 0:
            break
    out.close()




