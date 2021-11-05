import ex_blue_sound
from multiprocessing import Process
import ex_blue_distance
from bluetooth import *
import ex_blue_face

#def face_send():
#    socket = BluetoothSocket(RFCOMM)

if __name__ == '__main__':
    try:
        socket = BluetoothSocket(RFCOMM)
        socket.connect(("98:D3:71:FD:BF:91", 1))
        p1 = Process(target=ex_blue_sound.a_sensor, args=(socket,))
        p2 = Process(target=ex_blue_distance.a_sensor, args=(socket,))
        p3 = Process(target=ex_blue_face.face, args=(socket,))
        p1.start()
        print("sound 시작")
        p2.start()
        print("distance 시작")
        p3.start()
        print("face 시작")
        p1.join()
        p2.join()
        p3.join()
#    ex_blue_face.face(socket)
#    ex_blue_sound.a_sensor(socket)
#    ex_blue_distance.a_sensor(socket)
    except KeyboardInterrupt:
        socket.close()
