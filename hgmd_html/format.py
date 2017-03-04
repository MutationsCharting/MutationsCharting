import json
import urllib.request
import re
import argparse


# parser = argparse.ArgumentParser(description="Description")
#
# parser.add_argument('-d', '--data', help='Name of file with data of rses', required=True, type=str, metavar='TEXTSTRING')
# parser.add_argument('-m', '--mutalyzer', help='Name of file with data from mutalyzer', required=True, type=str, metavar='TEXTSTRING')
# parser.add_argument('-o', '--output', help='Name of file to write', required=True, type=str, metavar='TEXTSTRING')
# args = parser.parse_args()

i19 = 0
i38 = 0
inan = 0
def hg(line, i):
    global i19
    global i38
    global inan
    set = line.split("\t")
    for k in range(3, len(set)):
        digits = re.findall(r"(\d+)", set[k])
        if (len(digits) > 2):
            set[k] = digits[2]
    if len(set) > 2 and set[2] == set[3]:
        i19 += 1
        return "hg19"
    if len(set) > 3 and set[2] == set[4]:
        i38 += 1
        return "hg38"
    if (i19 + i38 + inan == 0):
        return "hg"
    inan += 1
    # print(line)
    return "NA"



def main(mut):
    with open(mut, "r") as f:
        i = 0
        for line in f:
            i += 1
            hg(line, i)
        print("hg19 = {} hg38 = {} Na = {}".format(i19, i38, inan))

if __name__ == '__main__':
    main("dataBaseFromMutalyzer.txt")#, "newData.txt")

