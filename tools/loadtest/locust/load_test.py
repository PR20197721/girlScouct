import os
from locust import HttpLocust, TaskSet

def makeFunc(obj, uri):
    task = lambda obj: obj.client.get(uri)
    return task

def formatString(str):
    return str.replace("\r","").replace("\n","").strip()

def formatInt(str):
    return int(formatString(str))

def convertLinesToTuples(file):
    uriList = list()
    for line in file: 
        try:
            lineArr = line.split(',')
            uriList.append((formatString(lineArr[0]),formatInt(lineArr[1])))
        except:
            print('Error converting line in url file to a tuple.', line)
    return uriList

def readFile(filename):
    file = open(filename, 'r')
    return convertLinesToTuples(file)

def load_urls(websiteUser):
    filename = 'uri_list.csv'
    if os.environ.get("locust.url.file"):
        filename = os.environ.get("locust.url.file")
    for item in readFile(filename):
        weight = 1
        try:
            if item[1] > 1:
                weight = item[1]
        except:
            print('Error determining weight of a task. Will default to 1.', weight)
        
        for n in range (0, weight):
            websiteUser.tasks.append(makeFunc(websiteUser, item[0]))

class GSCouncil(TaskSet):
    tasks = list()

    def on_start(self):
        load_urls(self) 

class WebsiteUser(HttpLocust):
    task_set = GSCouncil
    min_wait = 500
    max_wait = 10000