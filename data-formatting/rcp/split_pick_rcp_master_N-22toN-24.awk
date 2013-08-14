#Usage: awk -v outputdir=path/to/outputdir -f split_pick_rcp_master_N-22toN-24.awk path/to/rcp_master_N-22toN-24.csv
# We received one master file for picks N-22 to N-24.  This script splits it
# into separate files.

BEGIN {
    FS = ","   # input field seperator
    OFS = ","  # output field seperator
    RS = "\r\n" # input record seperator
    ORS = "\r\n" # output record seperator
    N22File = "rcp_master_N-22.csv"
    N23File = "rcp_master_N-23.csv"
    N24File = "rcp_master_N-24.csv"
}

NR == 1 {
    print > outputdir"/"N22File
    print > outputdir"/"N23File
    print > outputdir"/"N24File
    for(i=1;i<=NF;i++) { 
        if($i ~ "SIGNUP_NAME") {
            colnum=i;
            break
        }
    }
}

NR > 1 {
    if($colnum == "\"N-22\"")
        print > outputdir"/"N22File;
    else if($colnum == "\"N-23\"")
        print > outputdir"/"N23File;
    else if($colnum == "\"N-24\"")
        print > outputdir"/"N24File;
    else print "ERROR: unexpected signup_name";
}
