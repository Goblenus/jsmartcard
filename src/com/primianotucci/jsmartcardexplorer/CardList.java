
package com.primianotucci.jsmartcardexplorer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import javax.smartcardio.ATR;

/**
 *
 * @author Primiano Tucci - http://www.primianotucci.com/
 */
public class CardList {
    static Map<ATR,String> cards = new Hashtable<ATR,String>();
    
    static{
        loadCardList();
    }
    
    public static String lookupByAtr(byte[] iAtr){
        String name = cards.get(new ATR(iAtr));
        if(name == null)
            return "Unknown card";
        else
            return name;
    }
    
    static void loadCardList(){
        try{
            BufferedReader bi = new BufferedReader(new InputStreamReader(CardList.class.getResourceAsStream("resources/cardlist.txt")));
            String line="";
            
            byte[] lastATR=null;
            String lastName = "";
            
            while((line = bi.readLine())!=null){
                if(line.startsWith("#")) continue;
                try{
                    if(line.length()<2){
                        cards.put(new ATR(lastATR), lastName);
                        lastATR=null;
                    }else{
                        if(lastATR==null)
                            lastATR = StringUtil.stringToByteArr(line);
                        else
                            lastName = line;
                    }
                }catch(Exception ex){}
                
            }   
            bi.close();
            //System.out.println(cards.size()+" cards loaded");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
}

