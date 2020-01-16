package com.kkb.mr.reduceJoin;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class ReduceJoinMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String valueString = value.toString();
        String[] split = valueString.split(",");
        if (valueString.startsWith("p")) {
            context.write(new Text(split[0]), value);
        } else {
            context.write(new Text(split[2]), value);
        }
    }
}
