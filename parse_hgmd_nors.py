from glob import glob
import re
from tqdm import tqdm


def prepare_data_for_liftover(hgmd, query_file):
    with open(hgmd, 'r') as in_f, open(query_file, 'w') as out_f:
        next(in_f)  # Skip header
        for line in in_f:
            elements = line.strip().split()
            chromosome = elements[1]
            pos = elements[3].split('-')
            if len(pos) == 1:
                pos.append(pos[0])
            # Required format
            # chrN:start-end
            out_f.write('chr{}:{}-{}\n'.format(chromosome, pos[0], pos[1]))


def convert_liftover_results_to_initial_format(liftover_results, initial_file, result_file):
    with open(liftover_results, 'r') as in_f1, open(initial_file, 'r') as in_f2, open(result_file, 'w') as out_f:
        out_f.write(next(in_f2))  # Write header
        for line1, line2 in zip(in_f1, in_f2):
            _, pos = line1.strip().split(':')
            pos = pos.split('-')
            if pos[0] == pos[1]:
                pos = pos[0]
            else:
                pos = '-'.join(pos)
            elements = line2.strip().split('\t')
            elements[-2] = elements[3]
            elements[-1] = pos
            line2 = '\t'.join(elements) + '\n'
            out_f.write(line2)


def check_coordinates(file):
    chromosomes = {}
    snp = re.compile(r'([ATGC])>[ATGC]')
    print('Loading chromosomes ...')
    for fasta in tqdm(glob('hg38_long/*1.fa')):
        with open(fasta, 'r') as in_f:
            chromosomes[fasta.lstrip('hg38_long/neschr').rstrip('.fa')] = in_f.readline()
    print(chromosomes.keys())
    with open(file, 'r') as in_f:
        next(in_f)  # Skip header
        for line in in_f:
            elements = line.strip().split()
            chromosome = elements[1]
            pos1 = int(elements[-1].split('-')[0])
            pos2 = int(elements[-2].split('-')[0])
            # print(pos)
            mutation = elements[8]
            ref = snp.findall(mutation)
            if ref and len(ref[0]) == 1:
                if chromosome in chromosomes:
                    print(ref[0].upper(), chromosomes[chromosome][pos1-10:pos1 + 10].upper())
                    print(ref[0].upper(), chromosomes[chromosome][pos2-10:pos2 + 10].upper())
                    print(chromosomes[chromosome][pos1] == ref[0])
                    print(chromosomes[chromosome][pos2] == ref[0])

if __name__ == '__main__':
    # prepare_data_for_liftover('hgmd_html/temporary/hgmd_html_processed_sorted_nors.txt',
    #                       'hgmd_html/temporary/query_file.txt')
    # convert_liftover_results_to_initial_format('hgmd_html/temporary/hglft_genome_1170_b07cb0.bed',
    #                                            'hgmd_html/temporary/hgmd_html_processed_sorted_nors.txt',
    #                                            'hgmd_html/temporary/hgmd_html_processed_sorted_nors_final.txt')
    check_coordinates('hgmd_html/temporary/hgmd_html_processed_sorted_nors_final.txt')
