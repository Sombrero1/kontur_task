package com.example.kontur_task;

import com.example.kontur_task.exceptions.NotPossibleToPerformSuchConversion;
import com.example.kontur_task.exceptions.UnknownUnitsOfMeasurement;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


@Component
public class Converter {
    private String path;
    private HashMap<String,Double> buffer;
    private HashSet<String> units;

    public Converter() throws IOException {
        this.units = new HashSet<>();
        this.path = KonturTaskApplication.getArgs()[0];
        this.buffer = readCSV();
        searchDependence();
    }

    private HashMap <String,Double> readCSV() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
        Scanner scanner  = null;
        String line = null;

        HashMap<String,Double> buffer = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter(",");
            String S = scanner.next();
            String T = scanner.next();
            String key = ""+S +","+ T;
            Double val = Double.valueOf(scanner.next());
            if (!buffer.keySet().contains(key)) buffer.put(key, val);
            key = ""+ T +","+ S;
            if (!buffer.keySet().contains(key) ) buffer.put(key, 1/val);
            units.add(T);
            units.add(S);
        }

        reader.close();

        return buffer;
    }
    
    private void searchDependence(){
        HashMap<String,Double> update = new HashMap<>();
        for (String key1: buffer.keySet()
             ) {
                String part_forw_key1 = key1.split(",")[0];
                String part_back_key1 = key1.split(",")[1];
            for (String key2: buffer.keySet()
                 ) {
                String part_forw_key2 = key2.split(",")[0];
                String part_back_key2 = key2.split(",")[1];
                if (part_forw_key1.equals(part_forw_key2) && !part_back_key1.equals(part_back_key2)){
                    if(!buffer.containsKey(part_back_key1 +","+ part_back_key2)) {
                        update.put(part_back_key1 + "," + part_back_key2, (buffer.get(key2) / buffer.get(key1)));
                    }

                }

            }

        }
        for (String key:update.keySet()
             ) {
            buffer.merge(key, update.get(key), (oldV, newV) -> oldV);
        }

        if(update.size() != 0) {
            searchDependence();
        }


    }


    private void hasUnit(String u) throws UnknownUnitsOfMeasurement {
            if (units.contains(u))  return;
        throw new UnknownUnitsOfMeasurement();
    }

    private Double getCoef(String un1, String un2) throws NotPossibleToPerformSuchConversion {
        String search1 = un1+","+un2;

        if (buffer.containsKey(search1)){
            return buffer.get(search1);
        }
        throw new NotPossibleToPerformSuchConversion();
    }


    public double getK(String from, String to) throws UnknownUnitsOfMeasurement, NotPossibleToPerformSuchConversion {

        String [] fromMass = from.split(" ");
        String [] toMass = to.split(" ");


        Double num = 1.0;
        Double den = 1.0;
        int i;
        for (i = 0; i < fromMass.length && !fromMass[i].equals("/"); i++) {
            if (!fromMass[i].equals("*") && !toMass[i].equals("*")){
                hasUnit(fromMass[i]);
                hasUnit(toMass[i]);
                if (!fromMass[i].equals(toMass[i])) num *= getCoef(fromMass[i], toMass[i]);
            }
        }
        i++;
        for (; i < fromMass.length; i++) {
            if (!fromMass[i].equals("*") && !toMass[i].equals("*")) {
                hasUnit(fromMass[i]);
                hasUnit(toMass[i]);
                if (!fromMass[i].equals(toMass[i])) den *= getCoef(fromMass[i], toMass[i]);
            }
        }

        return num/den;
    }
}
