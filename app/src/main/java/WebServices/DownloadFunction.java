package WebServices;

import java.io.File;
import java.io.PrintStream;

public class DownloadFunction {

    //byte构建mp3文件
    public boolean buildMP3(byte[] bytes, String filePath){
        try{
            File file = new File(filePath);
            file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.write(bytes,0,bytes.length);
            ps.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
