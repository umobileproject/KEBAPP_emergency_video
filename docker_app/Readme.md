UMOBILE Project KEBAPP Emergency video app
===========================================
This app is the Emergency video KEBAPP sample  Hotspot application developed within the UMOBILE project. This is a docker image that can run over any platform

For more infomation about UMOBILE project, visit: [UMOBILE Project](http://www.umobile-project.eu/).

Build docker image
------------------
#docker build -t kebapp .

Run docker image
-----------------
#docker run -d --network=host kebapp


Prerequisites 
-------------
Before this application can be used, the NFD (Networking Forwarding Daemon) has to be installed and running on the hotspot.

How to test it locally
----------------------

Run the kebapp docker image first, install PyNDN2 locally and run the client test python script

#nfd-start

#docker run -d --network=host kebapp

#cd PyNDN2 && python setup.py install && cd .. 

#python kebapp_consumer.py

You should receive the video file named as video_received.mp4


