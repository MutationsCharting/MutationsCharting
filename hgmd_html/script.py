import json
import urllib.request
import re
import argparse


parser = argparse.ArgumentParser(description="Description")

parser.add_argument('-s', '--start', help='Number of start line to downoload', required=True, type=int, metavar='NUMBER')
parser.add_argument('-e', '--end', help='Number of end line to downoload', required=True,  type=int, metavar = 'NUMBER')
parser.add_argument('-f', '--filename', help='Name of file to write', required=True, type=str, metavar='TEXTSTRING')
args = parser.parse_args()


def go(rs):
    data = urllib.request.urlopen("https://mutalyzer.nl/json/getdbSNPDescriptions?rs_id=" + rs).read()
    pattern = r"(NC[^ ]*)+"
    match = re.findall(pattern, str(data))
    return match
def go2(rs, value, index):
    rses = go(rs)
    ans = False
    patternRs = "g.(\d*)"
    for i in range(len(rses)):
        findValue = re.findall(patternRs,rses[i])
        if (findValue[0] == value):
            ans = True
            return ("OK in line {0}, rs = {1}, value  = {2}\n".format(index, rs , findValue[0]))
    return "ALLERT!"

def main(start, end, filename):
    with open(filename, "w") as writer, open("hgmd_html_processed.txt", "r") as f:
        patternRs = r"(rs\d+)"
        patternChr = r":(\d*)"
        i = 0
        for line in f:
            i = i + 1
            if (i < start):
                continue
            if (i > end):
                break
            rs = re.findall(patternRs, line)
            chr = re.findall(patternChr, line)
            if (rs != []):
                print(i)
                data = urllib.request.urlopen("https://mutalyzer.nl/json/getdbSNPDescriptions?rs_id=" + rs[0]).read()
                writer.write(str(data) + "\n")
            # rs = re.findall(patternRs, line)
            # chr = re.findall(patternChr, line)
            # if (rs != []):
            #     writer.write(go2(rs[0], chr[0], i))
                # print(go2(rs[0], chr[0]))

if __name__ == '__main__':
    main(args.start, args.end, args.filename)
