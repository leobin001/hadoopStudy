package com.kkb.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.net.URI;

public class WriteSequenceFile {

    //模拟数据源
    private static final String[] DATA = {
            "The Apache Hadoop software library is a framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models.",
            "It is designed to scale up from single servers to thousands of machines, each offering local computation and storage.",
            "Rather than rely on hardware to deliver high-availability, the library itself is designed to detect and handle failures at the application layer",
            "o delivering a highly-available service on top of a cluster of computers, each of which may be prone to failures.",
            "Hadoop Common: The common utilities that support the other Hadoop modules."
    };

    public static void main(String[] args) throws IOException {
        String uri = "hdfs://node01:8020/writeSequenceFile";

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), config);

        Path path = new Path(uri);
        IntWritable key = new IntWritable();
        Text value = new Text();

        SequenceFile.Writer.Option pathOption = SequenceFile.Writer.file(path);
        SequenceFile.Writer.Option keyOption = SequenceFile.Writer.keyClass(IntWritable.class);
        SequenceFile.Writer.Option valueOption = SequenceFile.Writer.valueClass(Text.class);

        SequenceFile.Writer.Option compressionOption = SequenceFile.Writer.compression(SequenceFile.CompressionType.RECORD);
        SequenceFile.Writer writer = SequenceFile.createWriter(config, pathOption, keyOption, valueOption, compressionOption);

        for (int i = 0; i < 100000; i++) {
            key.set(100 - i);
            value.set(DATA[i % DATA.length]);
            System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key, value);
            writer.append(key, value);
        }

        IOUtils.closeStream(writer);
    }
}
