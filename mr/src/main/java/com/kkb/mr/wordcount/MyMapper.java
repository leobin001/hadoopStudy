package com.kkb.mr.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class  MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    IntWritable intWritable = new IntWritable(1);
    Text text = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] split = line.split(",");

        //计数器
        /*Counter counter = context.getCounter("mycounter group", "hadoop line counter");
        if (line.contains("hadoop")) {
            counter.increment(1);
        }*/

        for (String word : split) {
            text.set(word);
            context.write(text, intWritable);
        }
    }
}
