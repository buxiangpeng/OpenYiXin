package yixin.com.OpenYiXin.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 图片处理类
 * @author 卜祥鹏
 *
 */
public class ImageUtil {
	
	 //链接url下载图片  
    public static void downloadPicture(String imageUrl,String savePath) {  
        URL url = null;  
        try {  
            url = new URL(imageUrl);  
            DataInputStream dataInputStream = new DataInputStream(url.openStream());  
            FileOutputStream fileOutputStream = new FileOutputStream(new File(savePath));  
            ByteArrayOutputStream output = new ByteArrayOutputStream();  
            byte[] buffer = new byte[1024];  
            int length;  
            while ((length = dataInputStream.read(buffer)) > 0) {  
                output.write(buffer, 0, length);  
            }  
            fileOutputStream.write(output.toByteArray());  
            dataInputStream.close();  
            fileOutputStream.close();  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
