""" Python script that merges two multilevel dictionaries
BEFORE EXECUTION:   if you have a dictionary file: do nothing 
                    if you are creating a new dictionary:
                    touch a file that will be the output_file
IN: input_file with a json dictionary in each line
OUT: output_file 

USAGE: python param_dict_join.py input_file output_file
"""
# Needed libraries
import json
import os 
import sys

# Input and output files
input_file = sys.argv[1]
output_file = sys.argv[2]

print "Input files:"
print input_file
print output_file

# Function to merge different dictionaries, it explores every branch of the dictionary
def merge(a, b, path=None):
    "merges b into a"
    if path is None: path = []
    for key in b:
        if key in a:
            if isinstance(a[key], dict) and isinstance(b[key], dict):
                merge(a[key], b[key], path + [str(key)])
            elif a[key] == b[key]:
                pass # same leaf value
            else:
                # If a key in the dictionary has two different values => exception
                raise Exception('Conflict at %s' % '.'.join(path + [str(key)]))
        else:
            a[key] = b[key]
    return a

# Reading the dictionaries from file
dictionaries = []
with open(input_file) as f:
	for line in f:
		dictionaries.append(json.loads(line)) # appending to a json object

# Merging the dictionaries
my_dict = {}
for i in range(0,len(dictionaries)):
	my_dict=merge(my_dict, dictionaries[i])

# Creating the output dictionary
outer_dict={}
# If a dictionary already exists => merge
if os.path.getsize(output_file) > 0:
	with open(output_file) as f:
		for line in f:
			outer_dict=json.loads(line)
	final_dict=merge(outer_dict, my_dict)
	print "Merged with existing dictionary."
# If this is a new dictionary => create it
else:
	outer_dict=open(output_file,'w')
	final_dict= my_dict
	print "Created new dictionary."

# Write the output_file
dict_file=open(output_file,'w')
json.dump(final_dict,dict_file)
dict_file.close
