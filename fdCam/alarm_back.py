
# -*- coding: utf-8 -*- 
import numpy as np
import cv2
import time

# Cascades 디렉토리의 haarcascade_frontalface_default.xml 파일을 Classifier로 사용
faceCascade = cv2.CascadeClassifier('haarcascades/haarcascade_frontalface_default.xml')
cap = cv2.VideoCapture(0)
cap.set(3,640) # set Width
cap.set(4,480) # set Height

Sec = 0
Min = 0

while True:
    ret, img = cap.read()
    img = cv2.flip(img, -1) # 상하반전
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.2,
        minNeighbors=5,
        minSize=(20, 20)
    )
    for (x,y,w,h) in faces:
        cv2.rectangle(img,(x,y),(x+w,y+h),(255,0,0),2)
        roi_gray = gray[y:y+h, x:x+w]
        roi_color = img[y:y+h, x:x+w]
    if len(faces) > 0:  

        Sec += 1
        print(str(Min) + " Mins " + str(Sec) + " Sec ")

        cv2.putText(img, "Time: " + str(Min) + " Mins " + str(Sec) + " Sec ", (0,img.shape[0] -30), cv2.FONT_HERSHEY_TRIPLEX, 0.5,  (0,0,255), 1)
        cv2.putText(img, "Number of faces detected: " + str(faces.shape[0]), (0,img.shape[0] -10), cv2.FONT_HERSHEY_TRIPLEX, 0.5,  (0,0,255), 1)    

        time.sleep(1)
        if Sec == 60:
            Sec = 0
            Min += 1
            print(str(Min) + " Minute")                

    if len(faces) == 0:

#        print('No face detected')
#        cv2.putText(img, "No face detected ", (0,img.shape[0] -10), cv2.FONT_HERSHEY_TRIPLEX, 0.5,  (0,0,255), 1)        
        Sec = 0
        Min = 0
    
    cv2.imshow('video',img) # video라는 이름으로 출력
    k = cv2.waitKey(30) & 0xff
    if k == 27: # press 'ESC' to quit # ESC를 누르면 종료
        break
cap.release()
cv2.destroyAllWindows()
