import json
import urllib.request
import re
import argparse


parser = argparse.ArgumentParser(description="Description")

parser.add_argument('-i', '--input', help='Name of file to read', required=True, type=str, metavar='TEXTSTRING')
parser.add_argument('-o', '--output', help='Name of file to write', required=True, type=str, metavar='TEXTSTRING')
args = parser.parse_args()


def stringOfNC(tuple):
    if len(tuple) == 0:
        return "NA\tNA"
    else:
        map = {}
        s = ""
        for i in range(len(tuple)):
            tup = re.findall(r"(\d+):", str(tuple[i]))
            index = int(tup[0])
            newInfo = re.findall(r"((\d*)(del|dup|ins|.>)(.*))", tuple[i])
            try:
                if int(tup[0]) in map:
                    map[index] = "{}, {}".format(map[index], newInfo[0][3][:-1])
                else:
                    map[index] = tuple[i][:-1]
            except IndexError:
                print(tup[0])
        sorted(map.keys())
        for k, v in map.items():
            s += str(v) + "\t"
        return s[:-1]


def main(fileOut, fileIn, firstData):
    with open(fileOut, "w") as writer, open(fileIn, "r") as data, open(firstData, "r") as f:
        writer.write("{}\t{}\t{}\t{}\t{}\n".format("index", "rs_coordinats", "coordinats_hgmd",
                                           "hg19","hg38"))
        patternRs = r"(rs\d+)"
        patternChr = r":(\d*)"
        i = 0
        naCount = 0
        totalCount = 0
        for line in f:
            i = i + 1
            rs = re.findall(patternRs, line)
            chr = re.findall(patternChr, line)
            if (rs != []):
                totalCount += 1
                print (i)
                stringInBase = data.readline()
                if (i != int(str(re.findall(r"^(\d*)", stringInBase)[0]))):
                    continue
                coordinates = re.findall(r"(NC[^,\]]*)+", stringInBase)
                st = "{}\t{}\t{}\t{}\n".format(i, rs[0], chr[0], stringOfNC(coordinates, chr[0]))
                if (stringOfNC(coordinates,chr[0]) == "NA\tNA"):
                    naCount += 1
                writer.write(st)
        print("all = {}, NA = {}".format(str(totalCount), str(naCount)))

if __name__ == '__main__':
    main(args.output, args.input,"temporary/hgmd_html_processed.txt")
