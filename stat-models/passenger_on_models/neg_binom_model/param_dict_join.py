import json
import os 
# Function to merge different dictionaries
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
                raise Exception('Conflict at %s' % '.'.join(path + [str(key)]))
        else:
            a[key] = b[key]
    return a

# Reading the dictionaries from file
dictionaries = []
with open('route6params.json') as f:
	for line in f:
		dictionaries.append(json.loads(line))

# Merging the dictionaries
my_dict = {}
for i in range(0,len(dictionaries)):
	my_dict=merge(my_dict, dictionaries[i])


outer_dict={}
#print mydict
if os.path.getsize("final_params.json") > 0:
	with open('final_params.json') as f:
		for line in f:
			outer_dict=json.loads(line)
	final_dict=merge(outer_dict, my_dict)
	print 1
else:
	outer_dict=open("final_params.json",'w')
	final_dict= my_dict
	print 2

dict_file=open('final_params.json','w')
json.dump(final_dict,dict_file)
dict_file.close