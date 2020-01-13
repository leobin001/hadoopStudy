package com.kkb.mr.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CombinerClass extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int mapCombiner = 0;
        for (IntWritable value : values) {
            mapCombiner += value.get();
        }
        context.write(key, new IntWritable(mapCombiner));
    }
}
