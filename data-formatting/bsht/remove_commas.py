#!/usr/bin/python

import argparse
import re
import sys
from collections import deque

parser = argparse.ArgumentParser(description='Remove problematic commas from unquoted csv')
parser.add_argument('input', metavar='INPUT', type=str, help='the file to read')
parser.add_argument('nfields', metavar='NF', type=int, help='the number of fields in each row')
parser.add_argument('nchars', metavar='NC', type=int, help='the number of characters in VARCHAR2 fields')
parser.add_argument('accfields', metavar='AF', type=int, nargs='+', help='a field which should greedily accumulate commas')

args = parser.parse_args()
nf = args.nfields
nc = args.nchars
accfields = sorted(args.accfields)

pstr_reg = '^([^,]*,){%i}[^,]*$' % (nf-1)
pattern_reg = re.compile(pstr_reg)

q = deque(accfields)
p_regfield = '([^,]*)'
p_accfield = '(.{0,%i})' % nc
pstr_acc = '^'
for i in range(1,nf):
    if not q or q[0] != i:
        pstr_acc += p_regfield
    else:
        pstr_acc += p_accfield
        q.popleft()
    pstr_acc += ','
if not q:
    pstr_acc += p_regfield
else:
    pstr_acc += p_accfield
pstr_acc += '$'
pattern_acc = re.compile(pstr_acc)

with open(args.input,'r') as input:
    for line in input:
        match_reg = pattern_reg.match(line)
        if not match_reg:
            match_acc = pattern_acc.match(line)
            groups = [match_acc.group(i+1) for i in range(nf)]
            for af in accfields:
                groups[af-1] = groups[af-1].replace(',',' ')
            line = ','.join(groups)
        print line.strip()
