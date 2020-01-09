package com.kkb.mr.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int result = 0;
        for (IntWritable value : values) {
            result += value.get();
        }

        IntWritable intWritable = new IntWritable(result);
        context.write(key, intWritable);
    }
}
