# -*- coding: utf-8 -*-
import numpy as np
import cv2
import time

import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials, db

from flask import Flask, render_template, Response
from bluetooth import *

# FCM
from pyfcm import FCMNotification

APIKEY = "AAAAp7fGgNA:APA91bG3w58cfBLHhG4cRykjIS9O3rib5wIh-A67k8VeXWhVp_4vCWzNDnwtjot0vo18Zz2BcHk4vC9k8ArAqIEpWUg8v1j_PzsCKCT_PXHcV3biJqbDCbZx9piipb1uJc7D5w_gW2vt"
#자영 AVD
#TOKEN = "e2kvzJhvTl2fwt2znfYdb7:APA91bF_IuhorxGqmbaPJaa__lMovMF6zUyyBlAvcq6KsyWMLf0pSjVZ2clz2SdqZakQ1CL_-HXjuzH04lF-GgNepYiRLIsvJh3Q0TRkEySublKO6si_w3Er5WaetR-evdSXtG_ARjV9"
#자영 갤럭시
#TOKEN = "d_iv2xl2TCK6xbVW6j0aUS:APA91bH4HtEtYXs7ZqK3AICbdqXD0gS2s-c0SR9YIfABKuSo1eHIZtEBnwbpGL5RLkDQdUN_2R38Z9c8d-8ECKmsITOLmKuKTcQjVHRb4iPdjNO8TZGG0Yq77D6iclyJ05MaOcqKJzu4"
#수현
#TOKEN = "ewf8khSsQR2lK9bw37ylJ_:APA91bF4npdqkLFVM3sqk85paNFINFPSTAz-RP2UZ2XbzrpvMU7Tl7uPev1q_HfkwNwPyDUZAewhi94Abquo10pwdAHfKL-uz7MtCeJmk2M_Q5Bx7Djmv3TsK0jhd6bSBGca9DKYtkeq"
TOKEN = "ddjRrk3TSD63cXRZYv25Hi:APA91bGrKdmoIvQaZFXZcij2IDuqfVqDWyQMbD31yFKtY0sZMyj2Xra9hEbJ4GfOJnlG-GQ_KcZQIyRRilx2WMHiIYX3mjeJOSZ5aUoEgrtLk8vxCLOZ53SPWGhu8cFAuXXxMHt88K_c"
push_service = FCMNotification(APIKEY)

def sendMessage(title, body):
    data_message = {
        "body" : body,
        "title": title
    }
    result = push_service.single_device_data_message(registration_id=TOKEN, data_message=data_message)
    print(result)

cred = credentials.Certificate("/home/pi/probono-da97e-firebase-adminsdk-23ap0-dce234980c.json")
#firebase_admin.initialize_app(cred, {
#                'databaseURL': 'https://probono-da97e-default-rtdb.firebaseio.com/' 
#})


ref = db.reference('alarm/alarm_01')

# bluetooth
#socket = BluetoothSocket(RFCOMM)
#socket.connect(("98:D3:71:FD:BF:91", 2))
#socket

# Cascades 디렉토리의 haarcascade_frontalface_default.xml 파일을 Classifier로 사용
faceCascade = cv2.CascadeClassifier('/home/pi/fdCam/haarcascades/haarcascade_frontalface_default.xml')

app = Flask(__name__)

cap = cv2.VideoCapture(0)
cap.set(3,640)
cap.set(4,480)
def gen_frames(socket):
    cnt1 = 0
    cnt2 = 0

    Flag = False
    Alarm = False
    while True:
        success, frame = cap.read()
        frame = cv2.flip(frame, -1)
        frame = cv2.flip(frame, 1)
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces = faceCascade.detectMultiScale(
            gray,
            scaleFactor=1.2,
            minNeighbors=5,
            minSize=(20, 20)
        )
        for (x,y,w,h) in faces:
            cv2.rectangle(frame,(x,y),(x+w,y+h),(255,0,0),2)
            roi_gray = gray[y:y+h, x:x+w]
            roi_color = frame[y:y+h, x:x+w]

        # 얼굴인식 시간 추가
        if len(faces) > 0:
            if Flag == False:
                    cnt1 = 0
                    cnt2 = 0
            Flag = True # 인식 함
            if Alarm == False:
                if cnt1 == 20: # 자고 있다고 간주
                        print("아기가 잠들었어요!")
                        Alarm = True
                        ref.delete()
                else :
                        cnt1 += 1
                        print(cnt1)

        if len(faces) == 0:
            Flag = False
            if Alarm == True: # 자고 있는데 얼굴인식 안됨
                if cnt2 == 30 :
                        print("아기가 잘 자고 있나요?")
                        Alarm = False
                        now = time.localtime()
                        ref.update({'title' : '질식사 위험 감지'})
                        ref.update({'comment' : '아기가 잘 자고 있나요?'})
                        ref.update({'time'  :  "%04d/%02d/%02d %02d:%02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min, now.tm_sec)})
			# Bluetooth
                        socket.send('1')
			# FCM
                        sendMessage("질식사 위험 감지", "아기를 잘 눕혀 주세요!!")
                        cnt2 = 0
                else :
                        cnt2 += 1
                        print(cnt2)

        if not success:
            break
        else:
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    #Video streaming route. Put this in the src attribute of an img tag
    return Response(gen_frames(socket), mimetype='multipart/x-mixed-replace; boundary=frame')


@app.route('/')
def index():
    """Video streaming home page."""
    return render_template('index.html')

def face(socket1):
    global socket
    socket = socket1
    try :
       app.run(host='0.0.0.0', port = 8090)
       '''
       @app.route('/video_feed')
       def video_feed(socket):
          #Video streaming route. Put this in the src attribute of an img tag
          return Response(gen_frames(socket), mimetype='multipart/x-mixed-replace; boundary=frame')


       @app.route('/')
       def index():
          """Video streaming home page."""
          return render_template('index.html')
       '''

    except KeyboardInterrupt:
       print('강제종료') # 안뜸;;
       socket.close()
