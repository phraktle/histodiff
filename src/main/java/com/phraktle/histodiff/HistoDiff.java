package com.phraktle.histodiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoDiff {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out
                    .println("Usage: HistoDiff file1/url1 file2/url2 [sortBy] [threshold]");
            System.exit(1);
        }
        File file1 = parseFileArg(args, 0);
        File file2 = parseFileArg(args, 1);
        int sortBy = parseArg(args, 2);
        int threshold = parseArg(args, 3);

        dump(sort(sortBy,
                filter(sortBy, threshold, diff(parse(file1), parse(file2)))));
    }

    static File parseFileArg(String[] args, int idx) throws IOException {
        String location = args[idx];
        if (location.startsWith("http://") || location.startsWith("https://")) {
            File file = File.createTempFile("histodiff", ".tmp");
            file.deleteOnExit();
            URL url = new URL(location);
            try (InputStream in = url.openStream()) {
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return file;
        } else {
            return new File(location);
        }
    }

    static int parseArg(String[] args, int idx) {
        return args.length <= idx ? 0 : Integer.parseInt(args[idx]);
    }

    static List<Entry<String, long[]>> filter(int byIndex, int threshold,
            List<Entry<String, long[]>> histo) {
        List<Entry<String, long[]>> filtered = new ArrayList<>();
        for (Entry<String, long[]> e : histo) {
            if (Math.abs(e.getValue()[byIndex]) > threshold) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    static List<Entry<String, long[]>> sort(final int byIndex,
            List<Entry<String, long[]>> histo) {
        Collections.sort(histo, new Comparator<Entry<String, long[]>>() {
            public int compare(Entry<String, long[]> a, Entry<String, long[]> b) {
                return -Long.compare(a.getValue()[byIndex],
                        b.getValue()[byIndex]);
            }

        });
        return histo;
    }

    static void dump(List<Entry<String, long[]>> diff) {
        System.out.println("  #instances       #bytes  class name");
        System.out.println("-------------------------------------");
        for (Entry<String, long[]> e : diff) {
            long[] v = e.getValue();
            System.out.printf("%+12d %+12d  %s%n", v[0], v[1], e.getKey());
        }
    }

    static Map<String, long[]> parse(File file) throws IOException {
        Pattern p = Pattern.compile(" *(\\d+): +([\\d]+) +([\\d]+) +(.+)");
        Map<String, long[]> map = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                Matcher matcher = p.matcher(line);
                if (matcher.matches()) {

                    long instances = Long.parseLong(matcher.group(2));
                    long bytes = Long.parseLong(matcher.group(3));
                    String className = matcher.group(4);
                    map.put(className, new long[] { instances, bytes });

                }
            }
        }
        return map;
    }

    static List<Entry<String, long[]>> diff(Map<String, long[]> histo1,
            Map<String, long[]> histo2) {

        List<Entry<String, long[]>> diff = new ArrayList<>();

        for (Entry<String, long[]> e : histo1.entrySet()) {
            String c = e.getKey();
            long[] a = e.getValue();
            long[] b = histo2.remove(c);
            diff.add(newEntry(c, diff(a, b)));
        }

        // remaining in histo2 were not in histo1
        for (Entry<String, long[]> e : histo2.entrySet()) {
            String c = e.getKey();
            long[] b = e.getValue();
            diff.add(newEntry(c, diff(null, b)));
        }

        return diff;
    }

    static Entry<String, long[]> newEntry(String c, long[] diff) {
        return new AbstractMap.SimpleEntry<>(c, diff);
    }

    static long[] diff(long[] a, long[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            b = new long[a.length];
        }
        long[] diff = new long[a.length];
        for (int i = 0; i < a.length; i++) {
            diff[i] = b[i] - a[i];
        }
        return diff;
    }

}
