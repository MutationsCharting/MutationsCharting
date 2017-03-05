import urllib.request
import re
import argparse

path_mutalyizer = "dataBaseFromMutalyzer.txt"
path_clinvar = "temporary/clinvar_hg19_processed_sorted.txt"
off = 0

parser = argparse.ArgumentParser(description="Description")


parser.add_argument('-s', '--start', help='Number of start line to downoload', required=True, type=int, metavar='NUMBER')
parser.add_argument('-e', '--end', help='Number of end line to downoload', required=True,  type=int, metavar = 'NUMBER')
parser.add_argument('-f', '--flag', help='0 for analiz by mutalyizer, 1 for analiz by clinvar', required=True, type=int, metavar='NUMBER')
args = parser.parse_args()

def offline(rs, file_name):
    with open(file_name, "r") as file:
        for line in file:
            if rs in line:
                return line
    return False

def offline_mutalyizer(rs):
    global path_mutalyizer
    return offline(rs, path_mutalyizer)

def online_mutalyizer(rs):
    data = urllib.request.urlopen("https://mutalyzer.nl/json/getdbSNPDescriptions?rs_id=" + rs).read()
    pattern = r"(NC[^ ]*)+"
    match = re.findall(pattern, str(data))
    return match


def mutalyizer_search(rs):
    global off
    if offline_mutalyizer(rs):
        print("Offline success")
        off += 1
        return True
    else:
        # print("Online request {}".format(rs))
        if online_mutalyizer(rs):
            return True
    return False


def clinvar_search(rs):
    global path_clinvar
    return offline(rs, path_clinvar)


def getRs(line):
    return re.search(r"(rs\d+)",line)


def search_by_clinvar(high_val, start):
    global path_clinvar
    global off
    unsuccess = 0
    with open(path_clinvar, "r") as file:
        i = 0
        for line in file:
            i += 1
            try:
                if i < start:
                    continue
                if i >= high_val:
                    break
                if (i % 50 == 0):
                    print(i)
                if getRs(line):
                    if not mutalyizer_search(getRs(line).group(0)):
                        # print(getRs(line).group(0))
                        unsuccess += 1
                else:
                    print("WOW {}".format(line))
            except Exception:
                print("catch Exception")
    print("In the range {} - {} was only {} bad call, {} was downloaded yet.\nPersent = {} ".format(start, high_val, unsuccess, off, unsuccess/high_val))
    return unsuccess/high_val

def clinvar_optimalized_search(rs):
    global path_clinvar
    with open(path_clinvar, "r") as file:
        i = 0
        for line in file:
            i += 1
            if rs in line:
                return line
            else:
                if i > 100:
                    this_rs = getRs(line)
                    if this_rs.group(0) < rs:
                        return False
    return False


def search_by_mut(high_val, start):
    global path_mutalyizer
    unsuccess = 0
    with open(path_mutalyizer, "r") as file:
        i = 0
        for line in file:
            i += 1
            try:
                if i < start:
                    continue
                if i >= high_val:
                    break
                if (i % 500 == 0):
                    print(i)
                if getRs(line):
                        if not clinvar_search(getRs(line).group(0)):
                            # print(getRs(line).group(0))
                            unsuccess += 1
                # else:
                    # print("WOW {}".format(line))
            except Exception:
                print("catch Exception")
    print("In the range {} - {} was only {} bad call.\nPersent = {} ".format(start, high_val, unsuccess, unsuccess/high_val))
    return unsuccess/i


def main(flag, start, stop):
        if flag == 0:
            search_by_mut(stop, start)
        else:
            search_by_clinvar(stop,start)


if __name__ == '__main__':
    main(args.flag, args.start, args.end)