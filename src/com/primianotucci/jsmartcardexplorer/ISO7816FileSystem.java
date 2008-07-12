
package com.primianotucci.jsmartcardexplorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;



/**
 *
 * @author Primiano Tucci - http://www.primianotucci.com/
 */
public class ISO7816FileSystem {
    public static final int MF_ID = 0x3F00;
    private DF masterFile;
    
            
    public static ISO7816FileSystem.DF parseStructure(byte[] iData, int iDF) throws IOException{
        ByteArrayInputStream in = new ByteArrayInputStream(iData);
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        
        int state = 0;
        int curFile = 0;
        int readlen = 0;
        ISO7816FileSystem.DF DF = new ISO7816FileSystem.DF(iDF);
        boolean isConstructedDO = false;
        
        while(in.available() > 0){
            int b = in.read() & 0xFF; 
            
            switch(state){
                case 0:
                    /*    * B6=0 introduces a primitive data object
                          * B6=1 introduces a constructed data object
                     */
                    isConstructedDO = ((b & 0x20)!=0);
                    /*Otherwise (B5-B1 set to 1 in the leading byte), the tag field shall continue on one or more subsequent bytes. */
                    if((b & 0x1F) == 0x1F){
                        int nextB = in.read() & 0xFF;
                        b = (b << 8) | nextB;
                    }
                    curFile = b;
                    state = 1;
                    break;
                case 1:
                    //if B7 is 1, b contains the number of bytes forming the length
                    if((b & 0x80) == 0x80){
                        int bytes = b & 0x7F;
                        readlen = 0;
                        while(bytes-- > 0){
                            readlen = readlen << 8;
                            b = in.read() & 0xFF;
                            readlen |= b;
                        }
                    }else{
                        readlen = b;
                    }
                    
                    if(isConstructedDO)
                        state = 3;
                    else
                        state = 2;
                    buff.reset();
                    break;
                case 2:
                    buff.write(b);
                    if(--readlen == 0){
                        ISO7816FileSystem.EF  ef = new ISO7816FileSystem.EF(curFile);
                        ef.setData(buff.toByteArray());
                        DF.addSubEF(ef);
                        buff.reset();
                        state = 0;
                    }
                    break;
                case 3:
                    byte[] subData = new byte[readlen];
                    subData[0] = (byte)b;
                    in.read(subData,1,readlen-1);
                    ISO7816FileSystem.DF subDF = parseStructure(subData,curFile);
                    DF.addSubDF(subDF);
                    state = 0;
                    break;
            }
        }
        
        return DF;     
        
    }
    
    public ISO7816FileSystem(DF iMasterFile){
        masterFile = iMasterFile;
    }
    
    public DF getMasterFile(){
        return masterFile;
    }

    @Override
    public String toString() {
        return masterFile.toString();
    }
        
    
    public static class EF {
        protected int id;
        protected byte[] data = new byte[]{};
        
        void addByte(byte iByte){
            data = Arrays.copyOf(data, data.length+1);
            data[data.length-1] = iByte;
        }
        
        void setData(byte[] iData){
            data = iData;
        }

        void clearData(){
            data = new byte[]{};
        }
        
        public EF(int iId){
            id = iId;
        }
        
        public int getID(){
            return id;
        }
        
        public byte[] getData(){
            return data;
        }
        
        public int getSize(){
            return data.length;
        }

        String getString(int iIndent){
            StringBuffer sb = new StringBuffer();
            for(int i=0; i < iIndent; i++)
                sb.append("  ");
            sb.append(Integer.toHexString(id).toUpperCase() + " ("+getSize()+")");
            return sb.toString();                
        }
        
        @Override
        public String toString() {
            return getString(0);
        }

        
    }

    public static class DF extends EF {   
        TreeMap<Integer,EF> subFiles = new TreeMap<Integer,EF>();
        TreeMap<Integer,DF> subDirs = new TreeMap<Integer,DF>();
        
        public DF(int iId){
            super(iId);
        }
        
        void addSubEF(EF iEf){
            subFiles.put(iEf.getID(),iEf);
        }
        
        void addSubDF(DF iDf){
            subDirs.put(iDf.getID(),iDf);
        }
        
        public Collection<EF> getSubEFs(){
            return subFiles.values();
        }
        
        public Collection<DF> getSubDFs(){
            return subDirs.values();
        }
        
        public EF getSubEF(int iID){
            return subFiles.get(iID);
        }
        
        public DF getSubDF(int iID){
            return subDirs.get(iID);
        }
                
        
        @Override
        public int getSize(){
            int size = 0;
            for(EF ef : subFiles.values())
                size += ef.getSize();
            for(DF df : subDirs.values())
                size += df.getSize();
            
            return size;
        }

        @Override
        public String toString() {
            return getString(0);
        }        
        
    }
}
