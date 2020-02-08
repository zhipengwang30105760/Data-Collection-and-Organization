import os
textFile = open("D:/Papers/rename/result.txt", "w")
count = 0
print('Current working directory path: ', os.getcwd())
#for filename in os.listdir(r"C:\Users\zhipe\Desktop\rename\txt"):
directory = "D:/Papers/rename/txt/"
for filename in os.listdir("D:/Papers/rename/txt/"):
    newfilename = "doc" + str(count) + ".txt"
    newContent = filename + "------------->" + newfilename
    textFile.write(newContent)
    textFile.write("\n")
    os.rename(os.path.join(directory, filename), os.path.join(directory, newfilename))
    count = count + 1
