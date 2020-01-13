package com.kkb.mr.myInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MyInputFormatMain extends Configured implements Tool {

    @Override
    public int run(String[] strings) throws Exception {

        Job job = Job.getInstance(super.getConf(), "mergeSmallFile");
        job.setInputFormatClass(MyInputFormat.class);
        MyInputFormat.addInputPath(job, new Path("file:///G:\\kaikeba\\上课资料\\第八章\\3节mapreduce1\\MR课前资料_mr与yarn\\3、第三天\\5、自定义InputFormat实现小文件合并\\文件数据"));

        job.setMapperClass(MyInputFormatMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BytesWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);

        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        SequenceFileOutputFormat.setOutputPath(job, new Path("C:\\Users\\leo\\Desktop\\out_sequence"));

        boolean b = job.waitForCompletion(true);
        return b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new MyInputFormatMain(), args);
        System.exit(run);
    }
}
