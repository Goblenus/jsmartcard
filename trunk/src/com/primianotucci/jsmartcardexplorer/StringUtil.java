
package com.primianotucci.jsmartcardexplorer;

/**
 *
 * @author Primiano Tucci - http://www.primianotucci.com/
 */
public class StringUtil {
    public static Integer parseHex(String iStr){
        try{
            return (Integer.parseInt(iStr,16) & 0xFF);
        }catch(Exception ex){
            return null;
        }
    }
    
    public static String byteArrToString(byte[] iBytes,String iSeparator){
        StringBuffer sb = new StringBuffer();
        for(byte b : iBytes){
            sb.append(byteToHex(b));
            sb.append(iSeparator);
        }

        if(iSeparator.length()>0 && sb.length()>=iSeparator.length())
            sb.deleteCharAt(sb.length()-iSeparator.length());
        return sb.toString();
    }
    
    public static char byteToPrintableChar(byte iB){
        if(iB>= 33 && iB<=126)
            return (char)iB;
        else
            return '.';
    }
    
    public static String byteArrToPrintableString(byte[] iData){
       StringBuffer sb = new StringBuffer(iData.length);
       for(int i=0; i<iData.length; i++)
           sb.append(byteToPrintableChar(iData[i]));
       return sb.toString();      
    }
    
    public static String hexDump(byte[] iData, int iRowSize){
        StringBuffer sb = new StringBuffer();
        
        int rows = (int) Math.ceil((float)iData.length / iRowSize);
        for(int row=0; row < rows; row++){
            int offset = iRowSize*row;
            StringBuffer hexpart = new StringBuffer();
            StringBuffer strpart = new StringBuffer();
            
            for(int i=0; i<iRowSize; i++){
                if(offset + i >= iData.length)
                    break;
                byte b = iData[offset + i];
                hexpart.append(byteToHex(b));
                hexpart.append(" ");
                strpart.append(byteToPrintableChar(b));
            }
            int expectedLen = iRowSize * 3;
            if(hexpart.length() < expectedLen)
                hexpart.append(spaces(expectedLen - hexpart.length()));
            sb.append(hexpart);
            sb.append("     ");
            sb.append(strpart);
            sb.append("\n");
        }
        
        
        return sb.toString();
    }
    
    public static String byteToHex(int iVal){
         String hx = Integer.toHexString(((int)iVal)&0xFF).toUpperCase();
        if(hx.length() == 2)
            return hx;
        else
            return "0"+hx;
    }
       
    
    public static byte[] stringToByteArr(String iStr){
        iStr = iStr.replace(" ","");
        byte[] outArr = new byte[iStr.length()/2];
        for(int i=0; i<iStr.length(); i+=2){
            String hex = iStr.substring(i, i+2);
            byte b = (byte) ((int)Integer.parseInt(hex, 16) & 0xFF);
            outArr[i/2] = b;
        }
        return outArr;
    }
    
    public static String spaces(int iLen){
        StringBuffer buf = new StringBuffer(iLen);
        while(iLen-- > 0)
            buf.append(" ");
        return buf.toString();
    }
}
