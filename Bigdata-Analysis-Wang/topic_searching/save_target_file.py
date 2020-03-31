import pathlib, os, shutil
from difflib import SequenceMatcher

matched_path = "./matched_folder/"
not_matched_path = "./not_matched_folder/"
original_path = "/home/kwoksuncheng/workspace/topicmodeling/dataset/bigdata50000"



def main():
    #save all the content in the list
    lines = []
    with open("./keyword_list.txt") as file:
        for line in file: 
            line = line.strip() #or some other preprocessing
            lines.append(line)
    #remove the duplicate name
    new_lines = set(lines)
    not_list = ["", ".", ",", "x", " ", "2"]
    #open the directory
    for filename in os.listdir(original_path):
        if filename.endswith('.txt'):
            src = os.path.join(original_path, filename)
            current_file = open(src, "rb")
            content = current_file.read()
            current_file.close()
            for keyword_name in new_lines:
                if (keyword_name in str(content)) and not (keyword_name in not_list):
                    print(filename + " has matched keywords: " + keyword_name)
                    new_name = matched_path + filename
                    shutil.copy(src, new_name)
                    #break
                # else:
                #     #shutil.move(output_path, not_matched_path)
                #     new_name = not_matched_path + filename
                #     shutil.copy(src, new_name)
                #     print(keyword_name + " has not matched paper!!!!!!!!!!!!!!!!!!!!!!!!!!")
    # for path in pathlib.Path(original_path).iterdir():
    #     if path.is_file() and not(str(path) == 'folder/.DS_Store'):
    #         current_file = open(path, "rb")
    #         #print(current_file)
    #         content = current_file.read()
    #         output_path = "./" + str(path)
    #         for keyword_name in new_lines:
    #             #print(SequenceMatcher(None, keyword_name, str(content)).ratio())
    #             if keyword_name in str(content):
    #                 print(keyword_name + " has matched paper")
    #                 new_name = matched_path + str(path)
    #                 shutil.copy(output_path, new_name)
    #                 break
    #             else:
    #                 if not(str(not_matched_path) == './not_matched_folder/.DS_Store'):
    #                     #shutil.move(output_path, not_matched_path)
    #                     shutil.copy(output_path, not_matched_path)
    #                     print(keyword_name + " has not matched paper!!!!!!!!!!!!!!!!!!!!!!!!!!")       
    #         current_file.close()


if __name__ == "__main__":
    main()
