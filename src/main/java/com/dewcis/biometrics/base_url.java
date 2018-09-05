package com.dewcis.biometrics;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class base_url {

    public Map<String, String> base_url(){
        String delimiter = "=";
        Map<String, String> map = new HashMap<>();

        try(Stream<String> lines = Files.lines(Paths.get("config.txt"))){
            lines.filter(line -> line.contains(delimiter)).forEach(line -> map.putIfAbsent(line.split(delimiter)[0], line.split(delimiter)[1]));
        } catch (IOException ex) {
            Logger.getLogger(base_url.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }
}
