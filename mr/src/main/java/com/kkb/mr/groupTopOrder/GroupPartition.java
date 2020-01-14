package com.kkb.mr.groupTopOrder;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class GroupPartition extends Partitioner<OrderBean, DoubleWritable> {
    @Override
    public int getPartition(OrderBean orderBean, DoubleWritable doubleWritable, int numReduceTasks) {
        return (orderBean.getOrderId().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
}
