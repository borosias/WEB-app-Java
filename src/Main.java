import jdk.dynalink.StandardOperation;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


public class Main {

    private static final int NUM_THREADS = 20;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    private static final String PHONE_REGEX = "[0-9]{3}-[0-9]{3}-[0-9]{4}";
    private static final String NAME_REGEX = "[KS][a-zA-Z]+";
    private static final String OUTPUT_FILE = "output.txt";


    public static void main(String[] args) throws IOException {
        String directory = "E:\\WebJavaLab-1\\test\\";
        File root = new File(directory);
        processDirectory(root);
        executor.shutdown();
        List<String> list = Files.readAllLines(Path.of(OUTPUT_FILE));
        for (String str : list) {
            System.out.println(str);
        }

    }

    private static void processDirectory(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    executor.execute(() -> {
                        try {
                            processDirectory(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (file.getName().endsWith("txt")) {
                    executor.execute(() -> {
                        try {
                            processFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
                executor.shutdown();

        }
    }


    private static void processFile(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        String regex = NAME_REGEX + " - " + PHONE_REGEX;
        List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
        for (String line : lines
        ) {
            if (Pattern.matches(regex, line)) {
                String text = file.getName() + ": " + line + "\n";
                Files.write(Paths.get(OUTPUT_FILE), text.getBytes(), StandardOpenOption.APPEND);
                System.out.print(text);
            }
        }
    }

}



