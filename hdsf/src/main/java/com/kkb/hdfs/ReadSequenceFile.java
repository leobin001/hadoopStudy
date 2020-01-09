package com.kkb.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;


public class ReadSequenceFile {

    public static void main(String[] args) throws IOException {
        String uri = "hdfs://node01:8020/writeSequenceFile";
        Configuration config = new Configuration();
        Path path = new Path(uri);

        SequenceFile.Reader reader = null;

        try {
            SequenceFile.Reader.Option pathOption = SequenceFile.Reader.file(path);

            reader = new SequenceFile.Reader(config, pathOption);

            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), config);
            Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), config);

            long position = reader.getPosition();
            System.out.println(position);

            while (reader.next(key, value)) {
                String syncSeen = reader.syncSeen() ? "*" : ";";
                System.out.printf("[%s%s]\t%s\t%s\n", position, syncSeen, key, value);
                position = reader.getPosition();
            }

        } finally {
            IOUtils.closeStream(reader);
        }
    }

}
