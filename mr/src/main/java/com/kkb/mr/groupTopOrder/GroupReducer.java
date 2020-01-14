package com.kkb.mr.groupTopOrder;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class GroupReducer extends Reducer<OrderBean, DoubleWritable, OrderBean, DoubleWritable> {

    @Override
    protected void reduce(OrderBean key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        int i = 0;
        for (DoubleWritable value : values) {
            if (i < 2) {
                context.write(key, value);
                i++;
            } else {
                break;
            }
        }
    }
}
