# For scripts to execute files named clinvar_hg19.vcf and clinvar_hg38.vcf must exist
#	in ./hgmd_html/ directory. They can be easily obtained via official clinvar site
#	but they are too large to attach them to github repository

cd hgmd_html/
mkdir temporary/
cd FileProcessor/src/
# Compile code to get processed hgmd_html and two clinvars file
javac converter/*.java
javac separator/*.java
javac utils/*.java
java converter.ConvertToSameFormat
echo "Java code compiled and files generated"

# Sort files by rs column to compare them later
cd ./../../
cd ./temporary
sort -t $'\t' -k 5,5 -r hgmd_html_processed.txt > hgmd_html_processed_sorted.txt
sort -t $'\t' -k 5,5 -r clinvar_hg19_processed.txt > clinvar_hg19_processed_sorted.txt
sort -t $'\t' -k 5,5 -r clinvar_hg38_processed.txt > clinvar_hg38_processed_sorted.txt

echo "Got sorted files"

cd ./../FileProcessor/src/
java separator.SeparateHgmdHtmlProcessed
echo "Hgmd html separated in two"

# Clean files
rm converter/*.class
rm separator/*.class
rm utils/*.class
echo "Class files cleaned"
