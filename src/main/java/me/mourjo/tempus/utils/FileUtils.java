package me.mourjo.tempus.utils;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileUtils {

    public static List<String[]> readCSVFile(String path) throws IOException, CsvException {
        var inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        Objects.requireNonNull(inputStream);
        var reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));
        return new CSVReaderBuilder(reader).build().readAll();
    }
}
