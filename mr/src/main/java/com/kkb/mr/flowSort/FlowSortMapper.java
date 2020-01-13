package com.kkb.mr.flowSort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowSortMapper extends Mapper<LongWritable, Text, FlowSortBean, NullWritable> {
    private FlowSortBean flowSortBean;

    @Override
    protected void setup(Context context) {
        flowSortBean = new FlowSortBean();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split("\t");
        flowSortBean.setPhone(split[1]);
        flowSortBean.setUpFlow(Integer.parseInt(split[6]));
        flowSortBean.setDownFlow(Integer.parseInt(split[7]));
        flowSortBean.setUpCountFlow(Integer.parseInt(split[8]));
        flowSortBean.setDownCountFlow(Integer.parseInt(split[9]));
        context.write(flowSortBean, NullWritable.get());
    }
}
