package com.kkb.mr.myOutputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class MyOutputFormat extends FileOutputFormat<Text, NullWritable> {

    @Override
    public RecordWriter<Text, NullWritable> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Configuration config = new Configuration(taskAttemptContext.getConfiguration());
        FileSystem fs = FileSystem.get(config);
        Path goodCommentPath = new Path("C:\\Users\\leo\\Desktop\\goodComment\\1.txt");
        Path badCommentPath = new Path("C:\\Users\\leo\\Desktop\\badComment\\2.txt");
        FSDataOutputStream goodOutputStream = fs.create(goodCommentPath);
        FSDataOutputStream badOutputStream = fs.create(badCommentPath);
        return new MyRecordWriter(goodOutputStream, badOutputStream);
    }


}
