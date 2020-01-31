import time
import socket
#import board
#import neopixel
import pyaudio,wave,queue,threading
import numpy

CHUNK = 1024
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
RECORD_SECONDS = 5
WAVE_OUTPUT_FILENAME = "output.wav"

data =[]
frames=[]

def audio_callback(in_data, frame_count, time_info, status):
    global ad_rdy_ev

    q.put(in_data)
    ad_rdy_ev.set()
    if counter <= 0:
        return (None,pyaudio.paComplete)
    else:
        return (None,pyaudio.paContinue)

#pixels = neopixel.NeoPixel(board.D18, 30)
p = pyaudio.PyAudio()
q = queue.Queue()
stream = p.open(format=FORMAT,
        channels=CHANNELS,
        rate=RATE,
        input=True,
        output=False,
        frames_per_buffer=CHUNK,
        stream_callback=audio_callback)

wf = wave.open(WAVE_OUTPUT_FILENAME, 'wb')
wf.setnchannels(CHANNELS)
wf.setsampwidth(p.get_sample_size(FORMAT))
wf.setframerate(RATE)

print("Start Recording")
stream.start_stream()

#线程函数，会产生ad_rdy_ev事件
def read_audio_thead(q,stream,frames,ad_rdy_ev):
    global rt_data
    global fft_data

    while stream.is_active():
        ad_rdy_ev.wait(timeout=1000)
        if not q.empty():
            #process audio data here
            data=q.get()
            while not q.empty():
                q.get()
            rt_data = numpy.frombuffer(data,numpy.dtype('<i2'))
            rt_data = rt_data * window
            fft_temp_data=fftpack.fft(rt_data,rt_data.size,overwrite_x=True)
            fft_data=np.abs(fft_temp_data)[0:fft_temp_data.size/2+1]
            if Recording :
                frames.append(data)
        ad_rdy_ev.clear()

ad_rdy_ev=threading.Event()
t=threading.Thread(target=read_audio_thead,args=(q,stream,frames,ad_rdy_ev))

t.daemon=True
t.start()

savebuffer = ''
while True:
    data = input_stream.read(1000)
    savebuffer+=data
    if(len(savebuffer)>=8000):
        print('saved')
        #在此要将savebuffer转换为可视化图案

