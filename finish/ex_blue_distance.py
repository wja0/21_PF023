# -*- coding: utf-8 -*- 

from __future__ import print_function
import time
import RPi.GPIO as GPIO
import time
import serial
import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials, db
from bluetooth import *

#
import sys
sys.path.append("/home/pi/baby_cry_detection-master/baby_cry_detection/prediction_simulation")
from prediction_simulation import *

sys.path.append("/home/pi")
from mictest import *
#

# FCM
from pyfcm import FCMNotification

APIKEY = "AAAAp7fGgNA:APA91bG3w58cfBLHhG4cRykjIS9O3rib5wIh-A67k8VeXWhVp_4vCWzNDnwtjot0vo18Zz2BcHk4vC9k8ArAqIEpWUg8v1j_PzsCKCT_PXHcV3biJqbDCbZx9piipb1uJc7D5w_gW2vt"
# 자영
#TOKEN = "e2kvzJhvTl2fwt2znfYdb7:APA91bF_IuhorxGqmbaPJaa__lMovMF6zUyyBlAvcq6KsyWMLf0pSjVZ2clz2SdqZakQ1CL_-HXjuzH04lF-GgNepYiRLIsvJh3Q0TRkEySublKO6si_w3Er5WaetR-evdSXtG_ARjV9"
#수현
#TOKEN = "ewf8khSsQR2lK9bw37ylJ_:APA91bF4npdqkLFVM3sqk85paNFINFPSTAz-RP2UZ2XbzrpvMU7Tl7uPev1q_HfkwNwPyDUZAewhi94Abquo10pwdAHfKL-uz7MtCeJmk2M_Q5Bx7Djmv3TsK0jhd6bSBGca9DKYtkeq"
#TOKEN = "ddjRrk3TSD63cXRZYv25Hi:APA91bGrKdmoIvQaZFXZcij2IDuqfVqDWyQMbD31yFKtY0sZMyj2Xra9hEbJ4GfOJnlG-GQ_KcZQIyRRilx2WMHiIYX3mjeJOSZ5aUoEgrtLk8vxCLOZ53SPWGhu8cFAuXXxMHt88K_c"
# 자영 갤럭시
TOKEN = "d_iv2xl2TCK6xbVW6j0aUS:APA91bH4HtEtYXs7ZqK3AICbdqXD0gS2s-c0SR9YIfABKuSo1eHIZtEBnwbpGL5RLkDQdUN_2R38Z9c8d-8ECKmsITOLmKuKTcQjVHRb4iPdjNO8TZGG0Yq77D6iclyJ05MaOcqKJzu4"

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


ref1 = db.reference('alarm/alarm_02')
#ref2 = db.reference('alarm/alarm_03')

# bluetooth
#socket = BluetoothSocket(RFCOMM)
#socket.connect(("98:D3:71:FD:BF:91", 1))


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

def total(socket):
#def total():
    GPIO.setmode(GPIO.BCM)

    # 초음파1
    GPIO_TRIGGER1 = 23
    GPIO_ECHO1 = 24
    # 초음파2
    GPIO_TRIGGER2 = 17
    GPIO_ECHO2 = 27

    GPIO.setup(GPIO_TRIGGER1, GPIO.OUT)
    GPIO.setup(GPIO_ECHO1, GPIO.IN)
    GPIO.setup(GPIO_TRIGGER2, GPIO.OUT)
    GPIO.setup(GPIO_ECHO2, GPIO.IN)

    GPIO.output(GPIO_TRIGGER1, False)
    GPIO.output(GPIO_TRIGGER2, False)

    Flag = False
    print("거리감지시작")

    while True:
        distance1 = measure_average(GPIO_TRIGGER1, GPIO_ECHO1)
        distance2 = measure_average(GPIO_TRIGGER2, GPIO_ECHO2)
        time.sleep(1)

        if distance1 < 43 or distance2 < 43: # 아기 머리가 가까움
            if Flag == False :
               print("Distance Detection : %.1f" % distance1)
               print("Distance Detection : %.1f" % distance2)
               now = time.localtime()
               ref1.update({'title' : '낙상 위험 감지'})
               ref1.update({'comment' : '아기가 잘 누워 있나요?'})
               ref1.update({'time' : "%04d/%02d/%02d %02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min)})
               # Bluetooth
               socket.send('2')
               # FCM
               sendMessage("낙상 위험 감지", "아기가 떨어질 것 같아요!!")
               Flag = True
        elif distance1 >= 45 or distance2 >= 45: # 아기 머리가 멀어짐
            if Flag == True :
               print("Safe : %.1f" % distance1)
               print("Safe : %.1f" % distance2)
               ref1.delete()
               Flag = False
#def a_sensor():
def a_sensor(socket):
    try:
#        total()
        total(socket)
    except KeyboardInterrupt:
        GPIO.cleanup()
        socket.close()
        print("강제종료")

#a_sensor()
