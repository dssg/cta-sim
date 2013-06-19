
#Usage: awk -f <Name of AWK file> <.csv in> <.csv out>

BEGIN { 

        FS = ","    # input field seperator 
        OFS = ","   # output field seperator
}

NR > 1 { # If number of rows is larger than one the do:
	
	# Name tags = column number
    STOP_ID  = $5
    PASSENGERS_ON = $16
    PASSENGERS_OFF = $17


    # This prints the columns in the new order. The commas tell Awk to use the character set in OFS
    print STOP_ID, PASSENGERS_ON, PASSENGERS_OFF
}
