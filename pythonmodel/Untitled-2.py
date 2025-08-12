import math
x = int(input())
l = len(str(x))-1
a = 10**l
x = x/a
x2 = math.ceil(x)
x2 = x2-x
print(int(x2*a))


