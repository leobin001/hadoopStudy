package com.kkb.mr.reduceJoin;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;

public class ReduceJoinReducer extends Reducer<Text, Text, Text, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        ArrayList<String> firstPartList = new ArrayList<>();
        String secondPart = "";

        for (Text value : values) {
            String valueString = value.toString();
            if (valueString.startsWith("p")) {
                secondPart = valueString;
            } else {
                firstPartList.add(valueString);
            }
        }

        for (String firstPart : firstPartList) {
            context.write(new Text(firstPart + "\t" + secondPart), NullWritable.get());
        }
    }
}
