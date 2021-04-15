import sys


ret = ""

ret+= "Python Here! "
s = sys.stdin.readline().strip()
while s not in ['break', 'quit']:
    
    tokens = s.split()
    if tokens[0] == 'start':
        sum = 0
        for i in range(1,len(tokens)):
            num = int(tokens[i])
            sum += num
        ret += str(sum) + " "
    # ret += s.upper()
        sys.stdout.write(ret + '\n')
        sys.stdout.flush()
        ret = ""

    # s = sys.stdin.readline().strip()

ret += "Exiting Python"
sys.stdout.write(ret + '\n')
sys.stdout.flush()
