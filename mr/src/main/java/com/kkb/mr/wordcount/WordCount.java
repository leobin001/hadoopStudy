package com.kkb.mr.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCount extends Configured implements Tool {

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = super.getConf();

        //设置提交的队列
        //conf.set("mapred.job.queue.name", "hadoop");

        //压缩
        //conf.set("mapreduce.map.output.compress","true");
        //conf.set("mapreduce.map.output.compress.codec","org.apache.hadoop.io.compress.SnappyCodec");

        //conf.set("mapreduce.output.fileoutputformat.compress","true");
        //conf.set("mapreduce.output.fileoutputformat.compress.type","RECORD");
        //conf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.SnappyCodec");

        Job job = Job.getInstance(conf, "mrdemo1");
        job.setJarByClass(WordCount.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path("hdfs://node01:8020/studyData"));

        //job.setInputFormatClass(CombineTextInputFormat.class);
        //CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
        //CombineTextInputFormat.addInputPath(job, new Path("hdfs://node01:8020/studyData"));

        //规约
        //job.setCombinerClass(CombinerClass.class);

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);

        TextOutputFormat.setOutputPath(job, new Path("hdfs://node01:8020/wc"));

        job.setNumReduceTasks(3);

        boolean b = job.waitForCompletion(true);
        return b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        int run = ToolRunner.run(config, new WordCount(), args);
        System.exit(run);
    }
}
