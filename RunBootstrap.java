package com.jiehang.run;

import java.io.*;
import java.util.Properties;

public class RunBootstrap {

    public static void main(String[] args) {
        try {
            if (args.length != 1)
                System.exit(-1);
            String commond = getPath(args[0]);
            commond = commond.charAt(0) == '\"' && commond.charAt(commond.length() - 1) == '\"' ? " \"\" " + commond : commond;
            System.out.println(commond);
            Runtime run = Runtime.getRuntime();
            run.exec("cmd /C start " + commond);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPath(String key) throws IOException {
        InputStream inputStream = new FileInputStream(new File(System.getProperty("user.home") + File.separator + "run.properties"));
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties.getProperty(key);
    }

}
