import re
from collections import defaultdict, namedtuple
from json import load

import pandas as pd
from Bio import SeqIO

Gene = namedtuple('Gene', ['exons', 'introns'])
with open('config.json', 'r') as f:
    cfg = load(f)

ref_path = cfg['reference_path']
chr7_path = cfg['chr7_path']


def parse_gene_from_ensembl(path):  # TODO use USCS tables refseq
    df = pd.read_csv(path, encoding='cp1251')  # Hope encoding is always the same
    exons = defaultdict(dict)
    introns = defaultdict(dict)
    for row_i, row in df.iterrows():
        reg = None  # Pointer to region dict
        region = row['Exon / Intron']
        if region.startswith('ENSE'):  # Exon
            i = int(row['No.'])
            reg = exons
        elif region.startswith('Intron'):
            reg = introns

        if reg is not None:  # To discern from empty dict
            reg[i]['start'] = int(row['Start'].replace(',', ''))  # Numbers looks like 123,456,789
            reg[i]['end'] = int(row['End'].replace(',', ''))
            reg[i]['length'] = int(row['Length'].replace(',', ''))
            reg[i]['seq'] = row['Sequence'].strip()

    return Gene(exons, introns)

from Bio import SeqIO
def get_chr(ref_path, chr_n):
    for seq_record in SeqIO.parse(ref_path, 'fasta'):
        if seq_record.id == 'chr{}'.format(chr_n):
            with open('chr{}.fa'.format(chr_n), 'w') as outf:
                outf.write(str(seq_record.seq))
            break


def parse_c_dna_name(c_dna_name, left_most=True):
    """
    >>> parse_c_dna_name('c.744-10_744-3del')
    (['744', '-', '10'], 'del')

    # 'c.' may be omitted
    >>> parse_c_dna_name('1022_1023insTC')
    (['1022'], 'insTC')

    >>> parse_c_dna_name('c.(?_1767)_(2490_?)del', left_most=False)
    ([['?'], ['1767'], ['2490'], ['?']], 'del')

    :return: tuple of list(cDNA positions) and variant
    """
    pos_pat = re.compile(r'([\d+\-*?]+)')
    full_pat = re.compile(r'(?P<positions>(?:(?:[(\d+\-*?_)]+)+?)+)(?P<variation>.*)')
    positions, variation = full_pat.search(c_dna_name).groups()
    positions = pos_pat.findall(positions)  # Split by _ (intervals) and strip '()'
    parsed_positions = [list(filter(lambda s: s, re.split(r'(\D+)', p))) for p in positions]
    if not left_most:
        return parsed_positions, variation
    # One position consists of mixture of ('*-+' and numbers) or ? in str repr
    for parsed_pos in parsed_positions:  # Find leftmost numeric position
        if parsed_pos != ['?']:
            return parsed_pos, variation


def get_g_dna_pos(c_dna_pos, gene):
    """

    :param c_dna_pos: str
    :param gene: Gene from parse_gene_from_ensembl()
    :return: int position in genome coordinates
    """
    exons = gene.exons
    c_dna_pos += 132  # Difference between pos of start codon and 1 exon start
    for i in range(1, 27):
        if c_dna_pos > exons[i]['length']:
            c_dna_pos -= exons[i]['length']
        else:
            exon_i = i
            break
    else:  # Variation in the last exon
        exon_i = 27

    c_dna_pos -= 1  # Because math is awesome and this is 1-based system
    # To get the first element position one should add 0 to start position, not 1
    return exons[exon_i]['start'] + c_dna_pos


def translate_pos(position, gene):
    c_dna_pos = 0  # Position in coding sequence (ORF)
    offset = 0  # Shift into noncoding sequence
    symbols = ['', '+']  # By default all offset is into to the right side (5') of smth
    if position[0] == '?':
        return position[0]

    if len(position) == 1:  # Normal CDS c.78T>C
        c_dna_pos = int(position[0])
    elif len(position) == 2:  # Upstream of translation initiation site c.-78G>A or downstream of stop codon c.*78T>A
        c_dna_pos = int(position[1])
        symbols[0] = position[0]
    elif len(position) == 3:  # Intron 5' or 3' half c.78+45T>G c.79-45G>T
        c_dna_pos, offset = int(position[0]), int(position[2])
        symbols[1] = position[1]
    elif len(position) == 4:  # Intron in the 3'UTR c.*639-1G>A or in the 5'UTR c.-106+2T>A
        symbols = (position[1], position[3])
        c_dna_pos, offset = int(position[1]), int(position[3])

    if symbols[0] == '*':  # Downstream of stop codon
        c_dna_pos += 4443  # ORF length: 1480 aa + stop codon
    elif symbols[0] == '-':  # Upstream of first codon
        c_dna_pos = - c_dna_pos + 1  # Since there is no nucleotide c.0

    if symbols[1] == '-':  # Into the 3' half of the intron
        offset = - offset

    return get_g_dna_pos(c_dna_pos, gene) + offset


def translate_to_parsed_g_dna_name(c_dna_name, gene):
    position, variation = parse_c_dna_name(c_dna_name)
    g_dna_pos = translate_pos(position, gene)
    return g_dna_pos, variation


def translate_to_g_dna_name(c_dna_name, gene):
    g_dna_name = ''
    positions, variation = parse_c_dna_name(c_dna_name, left_most=False)
    g_dna_positions = [translate_pos(position, gene) for position in positions]
    if len(g_dna_positions) == 1:
        g_dna_name = 'g.{}{}'.format(*g_dna_positions, variation)
    elif len(g_dna_positions) == 2:
        g_dna_name = 'g.{}_{}{}'.format(*g_dna_positions, variation)
    elif len(g_dna_positions) == 4:
        g_dna_name = 'g.({}_{})_({}_{}){}'.format(*g_dna_positions, variation)
    return g_dna_name


def parse_variation(var):
    """
    >>> parse_variation('A>C')
    [('A', '>', '', 'C')]

    >>> parse_variation('delAAGTATG')
    [('', 'del', '', 'AAGTATG')]

    :return: list of tuples - parsed variants in form [(ref, type_, n, alt)]
    """
    pat = re.compile(r'([AGCT]*)(?:-)?(>|del|ins|inv|dup)(\d*)([AGCT]*)')
    return pat.findall(var)


def variation_to_vcf_format(ref, type_, n, alt, pos, chromosome):
    if type_ == '>':
        return ref, alt, pos
    elif type_ == 'del':
        ref, alt = alt, ref
        if ref == '':
            ref = chromosome[pos - 1: pos - 1 + int(n)]  # To 0-based
        pos, _, alt = create_identifier(ref, alt, pos - 2, chromosome)  # To 0-based and del includes the position
        alt = alt[0]
        ref = chromosome[pos: pos + len(ref) + 1]
    elif type_ == 'ins':
        pos, _, ref = create_identifier(ref, alt, pos - 1, chromosome)  # To 0-based and ins is right to the position
        ref = ref[0]
        alt = ref + alt
    elif type_ == 'dup':
        pos, _, ref = create_identifier(ref, alt, pos - 2, chromosome)  # To 0-based and dup includes the position
        ref = ref[0]
        alt = ref + alt
    elif type_ == 'indel':
        pos -= 2  # la
        ref = chromosome[pos: pos + len(ref) + 1]
        alt = chromosome[pos] + alt
    return ref, alt, pos + 1  # From 0-based to 1-based system


def make_variation_description(c_dna_name, gene, chromosome):
    """
    >>> make_variation_description('c.578_579+5delAAGTATG', gene, chromosome)
    ('GATGAAGT', 'G', 117174414, 'del')

    >>> make_variation_description('c.50_51insTT', gene, chromosome)
    ('C', 'CTT', 117120191, 'ins')

    >>> make_variation_description('c.100_117del18', gene, chromosome)
    ('GAATTGTCAGACATATACC', 'G', 117144350, 'del')

    >>> make_variation_description('c.174_177del4', gene, chromosome)
    ('GGATA', 'G', 117149094, 'del')

    :param c_dna_name: str
    :param gene: Gene from parse_gene_from_ensembl()
    :param chromosome: str chromosome sequence
    :return: tuple of ref(str), alt(str), pos(int), variation type(str)
    """
    g_dna_pos, var = translate_to_parsed_g_dna_name(c_dna_name, gene)
    variation = parse_variation(var)
    ref, alt, pos = variation_to_vcf_format(*variation[0], g_dna_pos, chromosome)
    if len(variation) == 2:  # ins following del
        ref, alt, pos = variation_to_vcf_format(variation[0][-1], 'indel', 0, variation[1][-1], g_dna_pos, chromosome)
    return ref, alt, pos, variation[0][1]


def create_identifier(ref, alt, coord, reference):
    la = coord  # Left anchor - pos of the base preceding the changed fragment
    ra = coord + len(ref) + 1   # Right anchor - pos of the base after the changed fragment
    if len(alt) == 0:  # Deletion
        la, ra = expand_borders(ref, la, ra, reference)
    elif len(ref) == 0:  # Insertion
        la, ra = expand_borders(alt, la, ra, reference)
    return la, ra, reference[la: ra]


def expand_borders(seq, la, ra, reference):
    seq_len = len(seq)
    ref_len = len(reference)
    i = la
    j = ra
    while i > 0 and seq[seq_len - ((ra - i - 1) % seq_len + 1)] == reference[i]:
        i -= 1
    while j < ref_len and seq[(j - la - 1) % seq_len] == reference[j]:
        j += 1
    return i, j


if __name__ == '__main__':
    import doctest
    with open(chr7_path, 'r') as inf:
        chromosome = inf.read()
    gene = parse_gene_from_ensembl('ExonsSpreadsheet-Homo_sapiens_Transcript_Exons_ENST00000003084.csv')
    doctest.testmod()
