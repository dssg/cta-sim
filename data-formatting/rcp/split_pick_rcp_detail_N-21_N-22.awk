#Usage: awk -f split_pick_rcp_detail_N-21_N-22.awk <.csv in>
# We received one detail file for both picks N-21 and N-22. This script
# splits it into two.

BEGIN {
    FS = ","   # input field seperator
    OFS = ","  # output field seperator
    RS = "\r\n" # input record seperator
    ORS = "\r\n" # output record seperator
    N21File = "rcp_detail_N-21.csv"
    N22File = "rcp_detail_N-22.csv"
}

NR == 1 {
    print > N21File
    print > N22File
    for(i=1;i<=NF;i++) { 
        if($i ~ "SIGNUP_NAME") {
            colnum=i;
            break
        }
    }
}

NR > 1 {
    if($colnum == "\"N-21\"")
        print > N21File;
    else if($colnum == "\"N-22\"")
        print > N22File;
    else print "ERROR: Unexpected signup_name";
}
