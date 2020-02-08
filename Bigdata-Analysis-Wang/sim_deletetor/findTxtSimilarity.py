import gensim
import nltk
from nltk.tokenize import word_tokenize, sent_tokenize
import numpy as np
import time
import os
import sys
nltk.download('punkt')
def getPercentageSimilarity(file_docs, dictionary, sims):
    avg_sims = [] # array of averages
    # for line in query documents
    for line in file_docs:
        # tokenize words
        query_doc = [w.lower() for w in word_tokenize(line)]
        # create bag of words
        query_doc_bow = dictionary.doc2bow(query_doc)
        query_doc_tf_idf = tf_idf[query_doc_bow]
        sum_of_sims =(np.sum(sims[query_doc_tf_idf], dtype=np.float32))
        avg = sum_of_sims / len(file_docs)
        avg_sims.append(avg)  
    # calculate total average
    total_avg = np.sum(avg_sims, dtype=np.float)
    # round the value and multiply by 100 to format it as percentage
    percentage_of_similarity = round(float(total_avg) * 100)
    return percentage_of_similarity

def createDoc(filename):
    file_docs = []
    with open (filename) as f:
        tokens = sent_tokenize(f.read())
        for line in tokens:
            file_docs.append(line)
    return file_docs

start_time = time.time()

domain_number = 4
dir_path = sys.argv[1]
list_of_dir = os.listdir(dir_path)
for i in range(len(list_of_dir)-1):
    filename = list_of_dir[i]
    if filename.endswith('.txt'):
        # print (filename, ": ")
        fullname = os.path.join(dir_path, filename)
        if os.path.exists(fullname):
            file_docs = createDoc(fullname)
            #print("Currently working on" + file_docs)
            gen_docs = [[w.lower() for w in word_tokenize(text)] 
                        for text in file_docs]
            dictionary = gensim.corpora.Dictionary(gen_docs)
            corpus = [dictionary.doc2bow(gen_doc) for gen_doc in gen_docs]
            tf_idf = gensim.models.TfidfModel(corpus)
            sims = gensim.similarities.Similarity('workdir/',tf_idf[corpus],
                                                    num_features=len(dictionary))
            percentage_of_similarity1 = getPercentageSimilarity(file_docs, dictionary, sims)

            for j in range(len(list_of_dir)-i-1):
                comparefilename = list_of_dir[j+i+1]
                fullname2 = os.path.join(dir_path, comparefilename)
                if os.path.exists(fullname2):
                    #compare size
                    #print("The size of " + filename + " is: " + str(os.path.getsize(fullname)))
                    #print("The size of " + comparefilename + " is: " + str(os.path.getsize(fullname2)))
                    #by using file size divide number of files, we get the average difference is 147
                    sizeFileName = os.path.getsize(fullname)
                    sizeCompareFileName = os.path.getsize(fullname2)
                    if (sizeFileName - sizeCompareFileName) > 147:
                        print("Currently compared " + filename + " with " + comparefilename) 
                        file2_docs = createDoc(fullname2)
                        percentage_of_similarity2 = getPercentageSimilarity(file2_docs, dictionary, sims)
                        diff_percentage_of_similarity = percentage_of_similarity1 - percentage_of_similarity2
                        if ((diff_percentage_of_similarity <= domain_number and diff_percentage_of_similarity >= (domain_number*-1)) and percentage_of_similarity1 != 0):
                            os.remove(fullname2)
                            print (comparefilename, " is removed.")
                        print()
                    else:
                        print("It is not necessary to check: " + comparefilename)
                        print()

print ('Running Time: ', (time.time() - start_time), 'seconds')