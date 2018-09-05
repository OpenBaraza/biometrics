package com.dewcis.biometrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;

public class base64Decoder {
    public FileOutputStream image=null;
    public char oldChar = '\\';
    public char newChar = '/';
    public String projDir = System.getProperty("user.dir");
    public String results = projDir.replace(oldChar, newChar);
    public FileOutputStream decode(String imputimage,String user_id){
    String imageString=imputimage;
    
    try {
            
            //Decoding Base64 encoded Byte Array to Image Byte array
            byte[] base64DecodedByteArray = Base64.decodeBase64(imageString);
              
            image = new FileOutputStream(""+results+"/finger print images/"+user_id+".PNG");
            image.write(base64DecodedByteArray);
            image.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException ex) {
        }
        return image;
    }
}
