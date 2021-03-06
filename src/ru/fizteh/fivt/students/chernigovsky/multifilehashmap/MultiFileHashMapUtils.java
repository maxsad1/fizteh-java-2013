package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

public class MultiFileHashMapUtils {

    public static void readTable(File tableFolder, State state) throws IOException {

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File directory = new File(tableFolder, directoryNumber.toString() + ".dir");
            if (!directory.exists()) {
                continue;
            }
            if (!directory.isDirectory()) {
                throw new IOException("Corrupted database");
            }

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                File file = new File(directory, fileNumber.toString() + ".dat");
                if (!file.exists()) {
                    continue;
                }
                if (!file.isFile()) {
                    throw new IOException("Corrupted database");
                }

                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                try {
                    while (true) {
                        int keyLength;
                        int valueLength;

                        try {
                            keyLength = dataInputStream.readInt();
                        } catch (EOFException ex) {
                            break;
                        }
                        valueLength = dataInputStream.readInt();

                        if (keyLength <= 0 || valueLength <= 0 || keyLength > 1048576 || valueLength > 1048576) {
                            throw new IOException("Wrong string size");
                        }
                        byte[] keyBytes = new byte[keyLength];
                        byte[] valueBytes = new byte[valueLength];

                        dataInputStream.readFully(keyBytes);
                        dataInputStream.readFully(valueBytes);

                        if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                            throw new IOException("Corrupted database");
                        }
                        if (Math.abs(keyBytes[0]) % 16 != directoryNumber || Math.abs(keyBytes[0]) / 16 % 16 != fileNumber) {
                            throw new IOException("Corrupted database");
                        }

                        String key = new String(keyBytes, "UTF-8");
                        String value = new String(valueBytes, "UTF-8");
                        state.put(key, value);
                    }
                } finally {
                    dataInputStream.close();
                }

            }

        }
    }

    public static void writeTable(File tableFolder, State state) throws IOException {

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                HashMap<String, String> currentMap = new HashMap<String, String>();
                for (Map.Entry<String, String> entry : state.getEntrySet()) {
                    if (Math.abs(entry.getKey().getBytes("UTF-8")[0]) % 16 == directoryNumber && Math.abs(entry.getKey().getBytes("UTF-8")[0]) / 16 % 16 == fileNumber) {
                        currentMap.put(entry.getKey(), entry.getValue());
                    }
                }

                File dir = new File(tableFolder, directoryNumber.toString() + ".dir");
                File file = new File(dir, fileNumber.toString() + ".dat");

                if (currentMap.size() == 0) {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException("Delete error");
                        }
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.getChannel().truncate(0); // Clear file
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    for (Map.Entry<String, String> entry : currentMap.entrySet()) {
                        dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                        dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                        dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                        dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
                    }
                } finally {
                    dataOutputStream.close();
                }

            }
        }

    }
}
