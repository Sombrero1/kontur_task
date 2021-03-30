package com.example.kontur_task.controllers;

import com.example.kontur_task.Converter;
import com.example.kontur_task.exceptions.NotPossibleToPerformSuchConversion;
import com.example.kontur_task.exceptions.UnknownUnitsOfMeasurement;
import com.example.kontur_task.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;

@RestController
public class Controller {
    @Autowired
    private Converter converter;

    @PostMapping("/convert")
    public String convert(@RequestBody Message message) {
        double coef = 0;
        try {
            coef = converter.getK(message.getFrom(), message.getTo());
        }
        catch (NotPossibleToPerformSuchConversion e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wrong"
            );
        }  catch (UnknownUnitsOfMeasurement e){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "not exist"
            );
        }
        DecimalFormat dF = new DecimalFormat( "#.###############" );
        return  dF.format(coef);
    }

}
