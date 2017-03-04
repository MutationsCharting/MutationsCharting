# For scripts to execute files named clinvar_hg19.vcf and clinvar_hg38.vcf must exist
#	in ./hgmd_html/ directory. They can be easily obtained via official clinvar site
#	but they are too large to attach them to github repository

cd hgmd_html/FileProcessor/src/

# Compile code to get processed hgmd_html and two clinvars file
javac *.java
java ConvertToSameFormat

# Move files to tmp dir 
cd ../../
mv *_processed.txt ./temporary/

# Clean files
cd FileProcessor/src/
rm *.class

# Sort files by rs column to compare them later
cd ./../../
mkdir temporary/
cd ./temporary
sort -t $'\t' -k 5,5 -r hgmd_html_processed.txt > hgmd_html_processed_sorted.txt
sort -t $'\t' -k 5,5 -r clinvar_hg19_processed.txt > clinvar_hg19_processed_sorted.txt
sort -t $'\t' -k 5,5 -r clinvar_hg38_processed.txt > clinvar_hg38_processed_sorted.txt

echo "Output files succesfully generated"
