# remove quotes from csv and drops header line
#Usage: awk -v outputdir=path/to/outputdir -f unquote.awk path/to/file.csv

BEGIN {
    FS = ","   # input field seperator
    OFS = ","
    RS = "\r\n"
    ORS = "\n"
}

{
    error = 0
    for(i=1;i<=NF;i++) {
        if($i !~ /^\"[^,]*\"$/)
        {
            print $i
            error = 1
            break
        }
    }
    if(error) {
        print "ERROR line " NR
        exit 1
    }
    gsub(/"/,"")
    if(NR > 1) {
        print
    }
}
