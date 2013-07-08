#Usage: awk -v outdir=path -f unquote.awk input.csv
# undo the quoting of fields and convert from Windows to Unix line endings

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
    else {
        print > "$outdir/header_" FILENAME
    }
}
