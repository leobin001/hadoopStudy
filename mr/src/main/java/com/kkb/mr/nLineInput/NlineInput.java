package com.kkb.mr.nLineInput;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class NlineInput {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config);
        job.setJarByClass(NlineInput.class);
        NLineInputFormat.setNumLinesPerSplit(job, 3);

        job.setInputFormatClass(NLineInputFormat.class);
        NLineInputFormat.addInputPath(job, new Path("file:///G:\\kaikeba\\上课资料\\第八章\\3节mapreduce1\\MR课前资料_mr与yarn\\3、第三天\\3、NLineInputFormat\\数据"));

        job.setMapperClass(NLineMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(NLineReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("C:\\Users\\leo\\Desktop\\NLine"));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }

    public static class NLineMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] s = value.toString().split(" ");
            for (String s1 : s) {
                context.write(new Text(s1), new LongWritable(1));
            }
        }
    }

    public static class NLineReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long result = 0L;
            for (LongWritable value : values) {
                result += value.get();
            }
            context.write(key, new LongWritable(result));
        }
    }
}
