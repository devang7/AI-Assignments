import sys
from shutil import copyfile #Lib for copying
import shutil
sys.path.append("homework.java")
wrong = []
for i in range(1, 4999):
	source = "/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/testcases-5000/INPUT/"+ str(i) + ".in"
	source2 = "/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/testcases-5000/OUTPUT/" + str(i) + ".out"
	copyfile(source, "/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/input.txt")
	copyfile(source2, "/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/correct_output.txt") 
	#import homework
	import os
	os.system('javac homework.java')
	os.system('java homework')
	with open('output.txt', 'r') as file1:
	    with open('correct_output.txt', 'r') as file2:
	    	if os.stat('output.txt').st_size == 0:
	    		print(str(i) +" Found Empty")
	    		wrong.append(i)
	    	else:
	    		flag = 0
	    		str1 = file1.readline().strip()
		    	while(str1):
		    		if(str1 != file2.readline().strip()):
		    			print(str(i) +' False')
		    			wrong.append(i)
		    			flag = 1
		    			break
		    		else:
		    			str1 = file1.readline().strip()
		    	if(flag == 0):
		    		print(str(i) + " True")
		        

	#input("Press Enter to continue...")
	os.remove('input.txt')
	os.remove('output.txt')
	os.remove('correct_output.txt')
print(wrong)