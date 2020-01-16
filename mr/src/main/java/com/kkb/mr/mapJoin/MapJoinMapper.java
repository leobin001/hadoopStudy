package com.kkb.mr.mapJoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
    private Map<String, String> pdMap;

    @Override
    protected void setup(Context context) throws IOException {
        pdMap = new HashMap<>();

        Configuration configuration = context.getConfiguration();
        URI[] cacheFiles = DistributedCache.getCacheFiles(configuration);
        URI cacheFile = cacheFiles[0];

        FileSystem fileSystem = FileSystem.get(cacheFile, configuration);
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path(cacheFile));

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fsDataInputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(",");
            pdMap.put(split[0], line);
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String valueString = value.toString();
        String[] split = valueString.split(",");
        String productString = pdMap.get(split[2]);

        context.write(new Text(value.toString() + "\t" + productString), NullWritable.get());
    }
}
