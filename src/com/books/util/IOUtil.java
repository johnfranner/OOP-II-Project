package com.books.util;

import com.books.model.Shipment;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public final class IOUtil {
    private IOUtil(){}

    public static Path writeReport(List<String> lines) throws IOException {
        Path outDir = Path.of("out");
        Files.createDirectories(outDir);
        Path report = outDir.resolve("report.txt");
        Files.write(report, lines);
        return report;
    }

    public static void printFile(Path p) throws IOException {
        Files.lines(p).forEach(System.out::println);
    }

    // CSV format: id,originTz,destTz,departureISO_LOCAL_DATE_TIME,HH:MM
    public static List<Shipment> readShipments(Path csv) throws IOException {
        if (!Files.exists(csv)) return List.of();
        return Files.lines(csv)
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(IOUtil::parseShipment)
                .collect(Collectors.toList());
    }

    private static Shipment parseShipment(String line) {
        String[] parts = line.split(",", -1);
        String id = parts[0].trim();
        String originTz = parts[1].trim();
        String destTz = parts[2].trim();
        LocalDateTime dep = LocalDateTime.parse(parts[3].trim());
        String[] hm = parts[4].trim().split(":");
        Duration dur = Duration.ofHours(Integer.parseInt(hm[0])).plusMinutes(Integer.parseInt(hm[1]));
        return new Shipment(id, originTz, destTz, dep, dur);
    }

    public static void writeShipments(Path csv, List<Shipment> shipments) throws IOException {
        Files.createDirectories(csv.getParent());
        var sb = new StringBuilder();
        sb.append("id,originTz,destTz,departureISO_LOCAL_DATE_TIME,HH:MM\n");
        for (Shipment s : shipments) {
            long minutes = s.transit().toMinutes();
            long hh = minutes / 60;
            long mm = minutes % 60;
            sb.append(String.join(",",
                    s.id(),
                    s.originTz(),
                    s.destTz(),
                    s.departureLocal().toString(),
                    String.format("%02d:%02d", hh, mm)
            ));
            sb.append("\n");
        }
        Files.writeString(csv, sb.toString());
    }

    public static ZonedDateTime eta(String originTz, String destTz,
                                    LocalDateTime departLocal, Duration transit) {
        ZonedDateTime depart = ZonedDateTime.of(departLocal, ZoneId.of(originTz));
        return depart.plus(transit).withZoneSameInstant(ZoneId.of(destTz));
    }
}
