from flask import Flask, request, render_template, jsonify
from werkzeug.utils import secure_filename #보안
from socket import *
from image_sizeto640 import resize_image #이미지 크기 640으로 전환
from PIL import Image #이미지 크기 
import sys, os
import boto3, json
from botocore.client import Config
from http import HTTPStatus

sys.path.append('D:\test\flask\yolov5')

app = Flask(__name__)

# input = { "file_name" : "{id}_{YYYYMMDD}"}
# ex {"file_name" : "1_20240405"}
# output = { "detect" : "dectect 갯수" }
# ex {"detect" : "3"}

@app.route('/receive_img', methods=['POST'])
def receive_img():
    # request의 데이터를 json 형태로 data에 저장
    data = request.json
    # key 값이 file_name의 value 추출
    file_name = data['file_name']
    #S3의 이미지 경로를 
    input_imagefile_path =f"./source/{file_name}.jpg"
    resize_image(input_imagefile_path)

    weight_path = "./runs/train/before_addplastic/weights/best.pt"
    source_path = "./source/"

    os.system("pwd")
    terminal_command2 = f"python detect.py --weights {weight_path} --img 640 --conf 0.3 --source {source_path}"
    os.system(terminal_command2)
    # ./runs/detect/exp/ 안의 file_name.txt 파일을 불러오기
    # 파일 안 txt 파일을 확인하여 각 줄의 숫자를 카운트하여 count 변수에 저장
    detection_file_path = f"./runs/detect/exp/{file_name}.txt"
    if os.path.exists(detection_file_path):
         with open(detection_file_path, 'r') as f:
            # 파일의 줄 수를 세어 검출된 물체의 수 계산
            num_detected_objects = sum(1 for line in f)
            print(num_detected_objects)
            # 텍스트 파일 삭제
            os.system("pwd")
            os.remove(detection_file_path[:-15])
            #os.remove(file_path[:])
            return jsonify({"count": num_detected_objects, "status": HTTPStatus.OK})
    else:
            # 텍스트 파일 삭제
            os.system("rm ./runs/detect/exp")
            return jsonify({"count": 0, "status": HTTPStatus.BAD_REQUEST})

   


# 0.0.0.0 으로 모든 IP에 대한 연결을 허용해놓고 포트는 8082로 설정
if __name__ == '__main__':
    app.run('0.0.0.0', port=5001, debug=True)