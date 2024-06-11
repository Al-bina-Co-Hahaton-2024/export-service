package ru.albina.export.component;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FileComponent {

    public File getTempFile(){
        try {
            return File.createTempFile("temp","e");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
