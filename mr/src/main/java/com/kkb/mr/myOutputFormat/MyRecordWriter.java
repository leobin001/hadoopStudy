package com.kkb.mr.myOutputFormat;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class MyRecordWriter extends RecordWriter<Text, NullWritable> {
    FSDataOutputStream goodStream = null;
    FSDataOutputStream badStream = null;

    public MyRecordWriter(FSDataOutputStream goodOutputStream, FSDataOutputStream badOutputStream) {
        this.goodStream = goodOutputStream;
        this.badStream = badOutputStream;
    }

    @Override
    public void write(Text text, NullWritable nullWritable) throws IOException, InterruptedException {
        String[] split = text.toString().split("\t");
        byte[] writeBytes = text.toString().getBytes();
        byte[] nextLine = "\r\n".getBytes();
        if (split[9].equals("0")) {
            goodStream.write(writeBytes);
            goodStream.write(nextLine);
        } else {
            badStream.write(writeBytes);
            badStream.write(nextLine);
        }
    }

    @Override
    public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        if (goodStream != null) goodStream.close();
        if (badStream != null) badStream.close();
    }
}
