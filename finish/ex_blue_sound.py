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
firebase_admin.initialize_app(cred, {
                'databaseURL': 'https://probono-da97e-default-rtdb.firebaseio.com/' 
})


#ref1 = db.reference('alarm/alarm_02')
ref2 = db.reference('alarm/alarm_03')

# bluetooth
#socket = BluetoothSocket(RFCOMM)
#socket.connect(("98:D3:71:FD:BF:91", 1))

def total(socket):

    port = '/dev/ttyACM0'
    brate = 9600 #boudrate
    cmd = 'temp'

    seri = serial.Serial(port, baudrate = brate, timeout = None)
    print("소리감지시작", seri.name)

    seri.write(cmd.encode())


    while True:
        if seri.in_waiting != 0 :
            content = seri.readline()
            print("소리감지", content.decode(), end='') 
            if(float(content.decode()) == -1):
                ref2.delete()
            else:
#                audioRecord()
                main()
                now = time.localtime()
                ref2.update({'title' : '울음 소리 감지'})
                ref2.update({'comment': '아기가 울고 있어요!'})
                ref2.update({'time' : "%04d/%02d/%02d %02d:%02d"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour,now.tm_min)})
                sendMessage("울음 소리 감지", "아기 상태를 확인해주세요!!")
                # Bluetooth
                socket.send('3')

def a_sensor(socket):
    try:
        total(socket)
    except KeyboardInterrupt:
        GPIO.cleanup()
        socket.close()
        print("강제종료")

#a_sensor()
