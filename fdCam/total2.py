# -*- coding: utf-8 -*-
import numpy as np
import cv2
import time
import RPi.GPIO as GPIO
import serial
import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials, db
from flask import Flask, render_template, Response
from bluetooth import *

# FCM
from pyfcm import FCMNotification

APIKEY = "AAAAp7fGgNA:APA91bG3w58cfBLHhG4cRykjIS9O3rib5wIh-A67k8VeXWhVp_4vCWzNDnwtjot0vo18Zz2BcHk4vC9k8ArAqIEpWUg8v1j_PzsCKCT_PXHcV3biJqbDCbZx9piipb1uJc7D5w_gW2vt"
#자영
#TOKEN = "e2kvzJhvTl2fwt2znfYdb7:APA91bF_IuhorxGqmbaPJaa__lMovMF6zUyyBlAvcq6KsyWMLf0pSjVZ2clz2SdqZakQ1CL_-HXjuzH04lF-GgNepYiRLIsvJh3Q0TRkEySublKO6si_w3Er5WaetR-evdSXtG_ARjV9"
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
firebase_admin.initialize_app(cred, {
                'databaseURL': 'https://probono-da97e-default-rtdb.firebaseio.com/'
})

ref1 = db.reference('alarm/alarm_01')
ref2 = db.reference('alarm/alarm_02')
ref3 = db.reference('alarm/alarm_03')

# bluetooth
#socket = BluetoothSocket(RFCOMM)
#socket.connect(("98:D3:71:FD:BF:91", 1))

# Cascades 디렉토리의 haarcascade_frontalface_default.xml 파일을 Classifier로 사용
faceCascade = cv2.CascadeClassifier('/home/pi/fdCam/haarcascades/haarcascade_frontalface_default.xml')

app = Flask(__name__)

cap = cv2.VideoCapture(0)
cap.set(3,640)
cap.set(4,480)

def measure(GPIO_TRIGGER, GPIO_ECHO):
    GPIO.output(GPIO_TRIGGER, True)
    time.sleep(0.00001)
    GPIO.output(GPIO_TRIGGER, False)
    start = time.time()

    while GPIO.input(GPIO_ECHO)==0:
        start = time.time()

    while GPIO.input(GPIO_ECHO)==1:
        stop = time.time()

    elapsed = stop-start
    distance = (elapsed * 34300)/2

    return distance

def measure_average(GPIO_TRIGGER, GPIO_ECHO):
    distance1=measure(GPIO_TRIGGER, GPIO_ECHO)
    time.sleep(0.1)
    distance2=measure(GPIO_TRIGGER, GPIO_ECHO)
    time.sleep(0.1)
    distance3=measure(GPIO_TRIGGER, GPIO_ECHO)
    distance = distance1 + distance2 + distance3
    distance = distance / 3

    return distance

def gen_frames():  # generate frame by frame from camera
    cnt1 = 0
    cnt2 = 0

    Flag = False
    Alarm = False

    GPIO.setmode(GPIO.BCM)

    # 초음파1
    GPIO_TRIGGER1 = 23
    GPIO_ECHO1 = 24
    # 초음파2
    GPIO_TRIGGER2 = 17
    GPIO_ECHO2 = 27
    print("Ultrasonic Measurement")

    GPIO.setup(GPIO_TRIGGER1, GPIO.OUT)
    GPIO.setup(GPIO_ECHO1, GPIO.IN)
    GPIO.setup(GPIO_TRIGGER2, GPIO.OUT)
    GPIO.setup(GPIO_ECHO2, GPIO.IN)

    GPIO.output(GPIO_TRIGGER1, False)
    GPIO.output(GPIO_TRIGGER2, False)

    Flag = False
    print("거리감지시작")
    port = '/dev/ttyACM0'
    brate = 9600 #boudrate
    cmd = 'temp'

    seri = serial.Serial(port, baudrate = brate, timeout = None)
    print("소리감지시작", seri.name)

    seri.write(cmd.encode())

    while True:
        # Capture frame-by-frame
        success, frame = cap.read()  # read the camera frame
        #ret, img = cap.read()
        # Face Detection
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
                        ref1.delete()
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
                        ref1.update({'title' : '질식사 위험 감지'})
                        ref1.update({'comment' : '아기가 잘 자고 있나요?'})
                        ref1.update({'time'  :  "%04d/%02d/%02d %02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min)})
			# Bluetooth
#                       socket.send('1')
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
            # ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
                  # concat frame one by one and show result

        # 소리감지 및 거리감지
        distance1 = measure_average(GPIO_TRIGGER1, GPIO_ECHO1)
        distance2 = measure_average(GPIO_TRIGGER2, GPIO_ECHO2)
        time.sleep(1)

        if distance1 < 45 or distance2 < 45: # 아기 머리가 가까움
            if Flag == False :
               print("Distance Detection : %.1f" % distance1)
               print("Distance Detection : %.1f" % distance2)
               now = time.localtime()
               ref2.update({'title' : '낙상 위험 감지'})
               ref2.update({'comment' : '아기가 잘 누워 있나요?'})
               ref2.update({'time' : "%04d/%02d/%02d %02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min)})
               # Bluetooth
#               socket.send('2')
               # FCM
               sendMessage("낙상 위험 감지", "아기가 떨어질 것 같아요!!")
               Flag = True
        elif distance1 >= 45 or distance2 >= 45: # 아기 머리가 멀어짐
            if Flag == True :
               print("Safe : %.1f" % distance1)
               print("Safe : %.1f" % distance2)
               ref2.delete()
               Flag = False

        if seri.in_waiting != 0 :
            content = seri.readline()
            print("소리감지", content.decode(), end='')
            if(float(content.decode()) == -1):
                ref3.delete()
            else:
                now = time.localtime()
                ref3.update({'title' : '울음 소리 감지'})
                ref3.update({'comment': '아기가 울고 있어요!'})
                ref3.update({'time' : "%04d/%02d/%02d %02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min)})
                sendMessage("울음 소리 감지", "아기 상태를 확인해주세요!!")
                # Bluetooth
#                socket.send('3')


@app.route('/video_feed')
def video_feed():
    #Video streaming route. Put this in the src attribute of an img tag
    return Response(gen_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')


@app.route('/')
def index():
    """Video streaming home page."""
    return render_template('index.html')


if __name__ == '__main__':
    try :
           app.run(host='0.0.0.0', port = 8090)
    except KeyboardInterrupt:
           print('강제종료') # 안뜸;;
#           socket.close()
