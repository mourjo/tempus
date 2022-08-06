package me.mourjo.tempus.utils;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    public static List<String[]> readCSVFile(String path) throws IOException, CsvException {
        var inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        Objects.requireNonNull(inputStream);

        return new CSVReaderBuilder(new BufferedReader(new InputStreamReader(inputStream))).build().readAll();
    }
}
