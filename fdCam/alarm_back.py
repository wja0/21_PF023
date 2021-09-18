
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

Flag = False
Alarm = False

while True:
    ret, img = cap.read()
    img = cv2.flip(img, -1) # 상하반전
    img = cv2.flip(img, 1) # 좌우반전
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
	if Flag == False: # 얼굴 인식 시작 초기화
		Sec = 0
		Min = 0
	Flag = True # 인식 함
        Sec += 1
        time.sleep(1)
        if Sec == 60:
            Sec = 0
            Min += 1
            print("Face Detection : " + str(Min) + " Mins")
	if Sec == 20: # 자고 있다고 간주
		print("아기가 잠들었어요!")
		Alarm = True

    if len(faces) == 0:
	if Flag == True: # 인식 하고 돌아옴
		Flag = False
		Sec = 0
		Min = 0
	if Alarm == True: # 자다가 얼굴인식이 안됨
		if Sec == 30 :
			print("아기가 잘 자고 있나요?")
	Sec += 1
	time.sleep(1)
	if Sec == 60:
		Sec = 0
		Min += 1
		print("No Face Detection Time : " + str(Min) + " Mins")

    cv2.imshow('Face Detection',img) # Face Detection라는 이름으로 출력
    k = cv2.waitKey(30) & 0xff
    if k == 27: # press 'ESC' to quit # ESC를 누르면 종료
        break
cap.release()
cv2.destroyAllWindows()
