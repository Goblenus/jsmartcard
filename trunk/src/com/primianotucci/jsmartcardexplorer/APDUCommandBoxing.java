
package com.primianotucci.jsmartcardexplorer;

import javax.smartcardio.CommandAPDU;

/**
 *
 * @author Primiano Tucci - http://www.primianotucci.com/
 */
 public class APDUCommandBoxing{
        private CommandAPDU apdu;
        private String mnemonic;
        
        public APDUCommandBoxing(String iLine){
            String[] arr = iLine.split("\\s+");
            if(arr.length<8) throw new IllegalArgumentException("iLine");
            mnemonic = arr[0];
            int cla= StringUtil.parseHex(arr[1]);
            int ins= StringUtil.parseHex(arr[2]);
            int p1= StringUtil.parseHex(arr[3]);
            int p2= StringUtil.parseHex(arr[4]);
            int p3= StringUtil.parseHex(arr[5]);
            byte[] data = StringUtil.stringToByteArr(arr[6].replace(',',' ').substring(1, arr[6].length()-1));
            int ne= StringUtil.parseHex(arr[7]);
            
            apdu = new CommandAPDU(cla,ins,p1,p2,data,0,p3,ne);
        }
        
        
        public APDUCommandBoxing(CommandAPDU iApdu,String iMnemonic){
            apdu = iApdu;
            mnemonic = iMnemonic;
        }
        
        public String getMnemonic() {
            return mnemonic;
        }
        
        public CommandAPDU getAPDU(){
            return apdu;
        }

        @Override
        public String toString() {
            int spaceLen = 16 - mnemonic.length();
            if(spaceLen < 0) spaceLen = 1;
            return mnemonic + StringUtil.spaces(spaceLen)
                   + StringUtil.byteToHex(apdu.getCLA()) + " " 
                   + StringUtil.byteToHex(apdu.getINS()) + " "
                   + StringUtil.byteToHex(apdu.getP1()) + " "
                   + StringUtil.byteToHex(apdu.getP2()) + " "
                   + StringUtil.byteToHex(apdu.getNc()) + " "
                   + "["+StringUtil.byteArrToString(apdu.getData(),",") + "] "
                   + StringUtil.byteToHex(apdu.getNe());
            
        }
        
    }