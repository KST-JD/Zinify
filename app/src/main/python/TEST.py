import numpy as np
#import imageio
#import scipy.ndimage
#import scipy
#import matplotlib.pyplot as plt
#import matplotlib as plt
import cv2
#from PIL import Image
from PIL import Image
import base64
#import requires.io
import io

def main (data):
    decode_data = base64.b64decode(data)
    np_data = np.fromstring(decode_data,np.uint8)
    img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
    g = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    pil_im = Image.fromarray(g)

    buff = io.BytesIO()
    pil_im.save(buff,format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return ""+str(img_str, 'utf-8')




