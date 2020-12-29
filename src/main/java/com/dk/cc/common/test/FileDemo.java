package com.dk.cc.common.test;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class FileDemo {
    public static void main(String[] args) {
        try {
            BufferedReader bis = new BufferedReader(new FileReader("D:/迅雷下载/b.txt"));
            String len = null;
/*            FileInputStream fis = new FileInputStream("D:/迅雷下载/b.txt");
            FileOutputStream fos = new FileOutputStream("D:/迅雷下载/b.txt",true);
            byte[] bytes = new byte[1024];
            int len =0;
            System.out.println(len);
            System.out.println(Arrays.toString(bytes));
            System.out.println(new String(bytes));
            while ((len = fis.read(bytes))!=-1){
                System.out.println(new String(bytes));
            }
            while (fis.read()!=-1){
                System.out.print((char)fis.read());
            }

            //释放资源
            fos.close();
            fis.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
