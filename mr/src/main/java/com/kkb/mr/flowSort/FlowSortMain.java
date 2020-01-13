package com.kkb.mr.flowSort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class FlowSortMain extends Configured implements Tool {
    @Override
    public int run (String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Job job = Job.getInstance(super.getConf(), "flowSort");
        job.setJarByClass(FlowSortBean.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path("G:\\kaikeba\\上课资料\\第八章\\3节mapreduce1\\MR课前资料_mr与yarn\\3、第三天\\2、hadoop的序列化\\数据\\input"));

        job.setMapperClass(FlowSortMapper.class);
        job.setMapOutputKeyClass(FlowSortBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(FlowSortReducer.class);
        job.setOutputKeyClass(FlowSortBean.class);
        job.setOutputValueClass(NullWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("C:\\Users\\leo\\Desktop\\order"));

        boolean b = job.waitForCompletion(true);
        return b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        int run = ToolRunner.run(config, new FlowSortMain(), args);
        System.exit(run);

    }
}