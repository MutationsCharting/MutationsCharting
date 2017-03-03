cd hgmd_html/ProcessHgmdHtml/src/

# Compile code to get processed hgmd html file
javac FileProcesser.java Main.java

java Main

# Clean files
rm FileProcesser.class Main.class

echo "Output files succesfully generated"
