# 在app标注文件里添加frequency
import json
import os
import copy
import operator
ORIGIN_DIR = "originFiles"
OUTPUT_DIR = "outputFiles"

def getFre():
    while True:
        res = input('float >= 0: ')
        try:
            if float(res) >= 0:
                return float(res)
        except:
            pass

def addFrequency(fileName, outputFile):
    print(fileName, outputFile)
    alreadyFinished = {}
    with open(fileName, encoding = 'utf-8') as f:
        originJson = json.loads(f.read())
        newJson = copy.deepcopy(originJson)
        print(originJson['meta']['appName'])
        for pageIndex,page in enumerate(originJson['data']):
            if page['pageId'] not in alreadyFinished:
                print(page['pageName'])
                res = getFre()
                newJson['data'][pageIndex]['frequency'] = res
                alreadyFinished[page['pageId']] = res
            else:
                newJson['data'][pageIndex]['frequency'] = alreadyFinished[page['pageId']]
            for buttonIndex,button in enumerate(page['buttons']):
                if (button['target'] not in alreadyFinished):
                    print(page['pageId'] + '::' + button['name'])
                    res = getFre()
                    newJson['data'][pageIndex]['buttons'][buttonIndex]['frequency'] = res
                    alreadyFinished[button['target']] = res
                else:
                    newJson['data'][pageIndex]['buttons'][buttonIndex]['frequency'] = alreadyFinished[button['target']]
        with open(outputFile, 'w', encoding = 'utf-8') as fout:
            fout.write(json.dumps(newJson, indent=2, ensure_ascii=False))
def transferEncoding(fileName):
    with open(fileName) as f:
        origin = json.loads(f.read())
        with open(fileName, 'w') as fout:
            fout.write(json.dumps(origin, indent=2, ensure_ascii=False))

def showAllCommands(dirName):
    allCommands = []
    for file in os.listdir(dirName):
        with open(os.path.join(dirName, file)) as f:
            data = json.loads(f.read())
            for page in data['data']:
                for button in page['buttons']:
                    allCommands.append([button['target'] + '-' + data['meta']['appName'], button['frequency']])
    allCommands.sort(key=operator.itemgetter(1), reverse=True)
    print(len(allCommands))
    print(allCommands)
if __name__ == "__main__":
    if False:
        complete = os.listdir(OUTPUT_DIR)
        for i in os.listdir(ORIGIN_DIR):
            if (i not in complete):
                addFrequency(os.path.join(ORIGIN_DIR, i), os.path.join(OUTPUT_DIR, i))
    showAllCommands('apps')