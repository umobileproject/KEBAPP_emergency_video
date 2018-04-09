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

import time
from pyndn import Name
from pyndn import Data
from pyndn import Face
from pyndn.security import KeyChain
from pyndn.security.identity import IdentityManager
from pyndn.security.identity import MemoryIdentityStorage, MemoryPrivateKeyStorage
from pyndn.security.policy import NoVerifyPolicyManager
from pyndn.util.blob import Blob

def dump(*list):
    result = ""
    for element in list:
        result += (element if type(element) is str else str(element)) + " "
    print(result)

class UbiCDN(object):
    def __init__(self, keyChain, certificateName):
        self._keyChain = keyChain
        self._certificateName = certificateName
        #self._responseCount = 0

    def onInterest(self, prefix, interest, face, interestFilterId, filter):
      #  self._responseCount += 1

        # Make and sign a Data packet.
        data = Data(interest.getName())
        content = interest.getName().get(2).toEscapedString()
        print("Echo "+content+".mp4.bin")
        file = open("./"+content+".mp4","r")
        data.setContent(file.read())
        #data.wireDecode(Blob(file.read()))
        file.close()
        #self._keyChain.sign(data, self._certificateName)

        dump("Sent content", content)
        face.putData(data)

    def onRegisterFailed(self, prefix):
        self._responseCount += 1
        dump("Register failed for prefix", prefix.toUri())

def main():
    # The default Face will connect using a Unix socket, or to "localhost".
    face = Face()

    # Use the system default key chain and certificate name to sign commands.
    #print("key1")
    #keyChain = KeyChain()
    #print("key2")
    identityStorage = MemoryIdentityStorage()
    privateKeyStorage = MemoryPrivateKeyStorage()
    keyChain = KeyChain(
        IdentityManager(identityStorage, privateKeyStorage),
        NoVerifyPolicyManager())
    identityName = Name("TestProducer")
    certificateName = keyChain.createIdentityAndCertificate(identityName)
    keyChain.getIdentityManager().setDefaultIdentity(identityName)

    face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName())

    # Also use the default certificate name to sign data packets.
    ubicdn = UbiCDN(keyChain, certificateName)
    prefix = Name("/kebapp/video")
    dump("Register prefix", prefix.toUri())
    face.registerPrefix(prefix, ubicdn.onInterest, ubicdn.onRegisterFailed)

    while 1:
    #while ubicdn._responseCount < 1:
        face.processEvents()
        # We need to sleep for a few milliseconds so we don't use 100% of the CPU.
        time.sleep(0.1)

    face.shutdown()

main()
