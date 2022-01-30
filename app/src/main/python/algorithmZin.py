import numpy as np
import imageio
import scipy.ndimage
#import scipy
#import matplotlib.pyplot as plt
import matplotlib as plt
import cv2
from PIL import Image
#from Pillow import Image
import base64
import io
#import requires.io

def dodge(front,back):
    result=front*255/(255-back) 
    result[result>255]=255          
    result[back==255]=255
    return result.astype('uint8')


def grayscale(rgb):   #skala szarości
    return np.dot(rgb[...,:3], [0.299, 0.587, 0.114])

def main (data):
    decode_data = base64.b64decode(data)
    np_data = np.fromstring(decode_data,np.uint8)
    img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)

    #img_grey = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    #pil_im = Image.fromarray(img_gray)

    
    #wczytywanie obrazu
    #img = 'JP2.jpg' #z dysku
    #img ="https://upload.wikimedia.org/wikipedia/commons/0/01/2014_Nysa%2C_zesp%C3%B3%C5%82_ko%C5%9Bcio%C5%82a_%C5%9Bw._Jakuba_Starszego_i_%C5%9Bw._Agnieszki.JPG" #z internetu

   # s = imageio.imread(img)
    g=grayscale(img)  #zastosowanie skali szarości i zmiana obrazu na czarno biały

    i = 255-g   #odwracanie obrazu

    b = scipy.ndimage.filters.gaussian_filter(i,sigma=150)  #rozmycie obrazu za sprawą filtra gaussowskiego
    r= dodge(b,g) #dodawanie i łączenie
 
    pil_im = Image.fromarray(r)
    buff = io.BytesIO()
    pil_im.save(buff,format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return ""+str(img_str, 'utf-8')

    #plt.imshow(r, cmap="gray")
    #plt.imsave('dodawanie i mieszanie.png', r, cmap='gray', vmin=0, vmax=255) #zapisywanie


