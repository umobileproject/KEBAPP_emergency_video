# -*- Mode:python; c-file-style:"gnu"; indent-tabs-mode:nil -*- */
#
# Copyright (C) 2014-2018 Regents of the University of California.
# Author: Jeff Thompson <jefft0@remap.ucla.edu>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
# A copy of the GNU Lesser General Public License is in the file COPYING.

import sys
import time
from pyndn import Name
from pyndn import Face
from pyndn import Interest

def dump(*list):
    result = ""
    for element in list:
        result += (element if type(element) is str else str(element)) + " "
    print(result)

class Counter(object):
    def __init__(self):
        self._callbackCount = 0

    def onData(self, interest, data):
        self._callbackCount += 1
        dump("Got data packet with name", data.getName().toUri())
        # Use join to convert each byte to chr.
        #dump(data.getContent().toRawStr())
        f = open (data.getName().get(2).toEscapedString()+"_received.mp4","w")
        print(data.getContent().size())
        f.write(data.getContent().toBytes())
        f.close()

    def onTimeout(self, interest):
        self._callbackCount += 1
        dump("Time out for interest", interest.getName().toUri())

def main():
    # The default Face will connect using a Unix socket, or to "localhost".
    face = Face()

    counter = Counter()

    #if sys.version_info[0] <= 2:
    #    word = raw_input("Enter a video name: ")
    #else:
    #    word = input("Enter a video name: ")

    name = Name("/kebapp/video/video")
    #name.append(word)
    dump("Express name ", name.toUri())
    interest = Interest(name)
    #interest.setInterestLifeTimeMilliseconds(30000)
    interest.setInterestLifetimeMilliseconds(30000)
    face.expressInterest(interest,counter.onData,counter.onTimeout)
	 #face.expressInterest(name, counter.onData, counter.onTimeout)

    while counter._callbackCount < 1:
        face.processEvents()
        # We need to sleep for a few milliseconds so we don't use 100% of the CPU.
        time.sleep(0.01)

    face.shutdown()

main()
