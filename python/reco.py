import os
import socket
import sympy
import numpy as np
from PIL import ImageEnhance, Image
from cnn.neural_network import CNN
from keras.utils import np_utils
from skimage import io
import matplotlib.pyplot as plt

#忽视警告(省的老是有人说---报错了hhhh)
import warnings
warnings.filterwarnings("ignore")

#计算微分
def calculDiff(strExpr):
    x = sympy.Symbol(strExpr[0])
    val = int(strExpr[1])
    str_ = strExpr[2:]
    ans = sympy.diff(str_, x, val)
    print(ans)
    newSocket.send(bytes(str(ans), encoding='utf-8'))

# 图像格式转化
def convert_img(img_file, path_to_img='data/'):
    if img_file.endswith('.jpg') or img_file.endswith('.png') or img_file.endswith('.bmp'):
        # print(img_file + ' is being processed')
        image = Image.open(os.path.join(path_to_img, img_file))
        if image.width != 32 or image.height != 32:
            image = image.resize((32, 32), resample=Image.LANCZOS)
        if image.mode != '1' or image.mode != 'L':
            image = ImageEnhance.Brightness(image).enhance(3.0)
            image = ImageEnhance.Contrast(image).enhance(10.0)
            image = ImageEnhance.Sharpness(image).enhance(5.0)
        if image.mode != 'RGB':
            image = image.convert('RGB')
        # print image_path + "isn't 1"
        image_path = os.path.join(path_to_img, img_file)
        if image_path.endswith('.jpg'):
            image_path = image_path.strip('.jpg')
            image.save('.' + image_path + '_conv.png')
        if image_path.endswith('.png'):
            image_path = image_path.strip('.png')
            image.save('.' + image_path + '_conv.png')
        if image_path.endswith('.bmp'):
            image_path = image_path.strip('.bmp')
            image.save('.' + image_path + '_conv.png')


if __name__ == "__main__":
    print('CNN model is ready to work')
    path = 'data/'
    all_images = []
    all_labels = []
    pic_names = []
    i = 0

    model_labels = []
    with open("yuxi_37_tag.txt") as file:  # change labels file name  E:\\CoreMath\SNH\python\Pretre\
        for line in file:
            line = line.strip().split()[0]
            model_labels.append(line)
    print('333')

    clf = CNN.build(width=32, height=32, depth=1, total_classes=len(model_labels), input_shape=(32, 32, 1),
                    Saved_Weights_Path='yuxi_37_lr=01_decay=1e-6_momentum=08.hdf5')  # change model file name

    # for file in sorted(os.listdir(path)):
    # if not file.startswith("."):
    # if not file.endswith('_conv.png'):
    # convert_img(file)
    print('444')
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    address = ('localhost', 9998)
    sock.bind(address)
    sock.listen()
    print('222')

    while True:
        all_images = []
        all_labels = []
        pic_names = []
        i = 0
        newSocket, clientAddr = sock.accept()
        recvData = str(newSocket.recv(1024), encoding='utf-8')
        # print(recvData)
        if (recvData.find("derivative") != -1):
            try:
                calculDiff(recvData.split(':')[1])
            except:
                print("I can't calculate it yet!")
                newSocket.send(bytes(str("I can't calculate it yet!"), encoding='utf-8'))

        else:
            # here we can put our code
            for file in os.listdir(path):
                if not file.startswith("."):

                    img = io.imread(os.path.join(path, file), as_gray=True)
                    # plt.imshow(img)
                    # plt.show()
                    img = img.reshape([32, 32, 1])
                    all_images.append(img)
                    all_labels.append(i)
                    i += 1
                    pic_names.append(file)
            input_img = np.array(all_images)
            input_labels = np_utils.to_categorical(all_labels, i)

            for num in range(0, len(input_labels)):
                probs = clf.predict(input_img[np.newaxis, num])
                prediction = probs.argmax(axis=1)

                print('LABEL:\t', int(prediction[0]))
                print('PROBS:\t', probs[0][int(prediction[0])])  # probs.argmax(axis=1))
                print('IT IS:\t', model_labels[int(prediction[0])] + '\n')  # + '-' + probs[int(prediction[0])])

                newSocket.send(bytes(model_labels[int(prediction[0])] + "\n", encoding='utf-8'))
                newSocket.send(bytes(str(probs[0][int(prediction[0])]) + "\n", encoding='utf-8'))

        # newSocket.send(bytes('thank you\n', encoding='utf-8'))
        newSocket.close()
        print('111')