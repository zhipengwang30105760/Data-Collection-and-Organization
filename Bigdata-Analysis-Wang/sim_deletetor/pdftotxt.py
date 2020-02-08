from io import StringIO
from pdfminer3.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer3.converter import TextConverter
from pdfminer3.layout import LAParams
from pdfminer3.pdfpage import PDFPage
import os
import sys, getopt
import time
import threading
# converts pdf, returns its text content as a string

def convert(fname, pages=None):
    if not pages:
        pagenums = set()
    else:
        pagenums = set(pages)

    output = StringIO()
    manager = PDFResourceManager()
    converter = TextConverter(manager, output, laparams=LAParams())
    interpreter = PDFPageInterpreter(manager, converter)

    infile = open(fname, 'rb')
    for page in PDFPage.get_pages(infile, pagenums):
        interpreter.process_page(page)
    infile.close()
    converter.close()
    text = output.getvalue()
    output.close
    return text

# create txt file
def createfile(filename, text):
    with open(filename, "w", encoding="utf-8") as f:
        f.write(text)
        f.close()
    #textFile = open(filename, "w")  # make text file
    #textFile.write(text)  # write text to text file
    #textFile.close()

# converts all pdfs in directory pdfDir, saves all resulting txt files to txtdir
def convertMultiple(pdfDir, txtDir):
    for pdf in os.listdir(pdfDir):
        if pdf.endswith('.pdf'):
            pdfFilename = os.path.join(pdfDir, pdf)
            text_name = pdf.replace('.pdf', '.txt')
            try:
                text = convert(pdfFilename)  # get string of text content of pdf
            except:
                print ("Error: ", pdf)
            text_save_path = os.path.join(txtDir, text_name)
            createfile(text_save_path, text)
            print("Convert " + text_name + " successfully")


# pdfDir = sys.argv[1]
# txtDir = sys.argv[2]
print("Start Converting===========================================================================================")
t1 = threading.Thread(target = convertMultiple(r"C:\Users\zhipe\Desktop\sim_deletetor\dir1\pdf1", r"C:\Users\zhipe\Desktop\sim_deletetor\dir1\txt1"), args = (10, ), name = "thread1")
t2 = threading.Thread(target = convertMultiple(r"C:\Users\zhipe\Desktop\sim_deletetor\dir2\pdf2", r"C:\Users\zhipe\Desktop\sim_deletetor\dir2\txt2"), args = (10, ), name = "thread2")
t3 = threading.Thread(target = convertMultiple(r"C:\Users\zhipe\Desktop\sim_deletetor\dir3\pdf3", r"C:\Users\zhipe\Desktop\sim_deletetor\dir3\txt3"), args = (10, ), name = "thread3")
t4 = threading.Thread(target = convertMultiple(r"C:\Users\zhipe\Desktop\sim_deletetor\dir4\pdf4", r"C:\Users\zhipe\Desktop\sim_deletetor\dir4\txt4"), args = (10, ), name = "thread4")
t5 = threading.Thread(target = convertMultiple(r"C:\Users\zhipe\Desktop\sim_deletetor\dir5\pdf5", r"C:\Users\zhipe\Desktop\sim_deletetor\dir5\txt5"), args = (10, ), name = "thread5")
start_time = time.time()
#thread start
t1.start()
t2.start()
t3.start()
t4.start()
t5.start()
#thread join 
t1.join()
t2.join()
t3.join()
t4.join()
t5.join()
# convertMultiple(pdfDir, txtDir)
print ('Running Time for converting: ', (time.time() - start_time), 'seconds')
