package Hivemind.master;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileManager {

    public static byte[] getFile(String path) throws IOException {
        return getFile(new File(path));
    }

    public static byte[] getFile(File file) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        byte[] buffer = new byte[(int)file.length()];
        in.read(buffer);
        in.close();
        return buffer;
    }

    public static boolean tryGetFile(File file, byte[] buffer){
        DataInputStream in = null;
        try{
            in = new DataInputStream(new FileInputStream(file));
            in.read(buffer);
            in.close();
        }catch(IOException e){
            e.printStackTrace();
            try{in.close();}catch(IOException ee){}
            return false;
        }
        return true;
    }

    public static String getExtension(File file) {
        String fileName = file.getName();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

}