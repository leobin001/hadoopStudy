package com.kkb.mr.keyValueInput;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class KeyValueInput {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration config = new Configuration();
        config.set("key.value.separator.in.input.line", "@zolen@");
        Job job = Job.getInstance(config);

        job.setInputFormatClass(KeyValueTextInputFormat.class);
        KeyValueTextInputFormat.addInputPath(job, new Path("file:///G:\\kaikeba\\上课资料\\第八章\\3节mapreduce1\\MR课前资料_mr与yarn\\3、第三天\\2、keyValueTextInputFormat\\数据"));

        job.setMapperClass(KeyValueMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(KeyValueReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("C:\\Users\\leo\\Desktop\\keyValue"));

        boolean b = job.waitForCompletion(true);
        System.exit(0);
    }

    public static class KeyValueMapper extends Mapper<Text, Text, Text, LongWritable> {
        LongWritable outValue = new LongWritable(1);

        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, outValue);
        }
    }

    public static class KeyValueReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long result = 0;
            for (LongWritable value : values) {
                long l = value.get();
                result += l;
            }
            context.write(key, new LongWritable(result));
        }
    }
}
