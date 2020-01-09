package com.kkb.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFSOperate {

    @Test
    public void mkdir() throws IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fileSystem = FileSystem.get(config);
        fileSystem.mkdirs(new Path("/kkb/dir1"));
        fileSystem.close();
    }

    @Test
    public void mkdir2() throws URISyntaxException, IOException, InterruptedException {
        Configuration config = new Configuration();
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"), config, "test");
        fileSystem.mkdirs(new Path("/kkb/dir2"));
        fileSystem.close();
    }

    @Test
    public void uploadFile() throws IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fileSystem = FileSystem.get(config);
        fileSystem.copyFromLocalFile(new Path("file:///d:\\world.txt"), new Path("hdfs://node01:8020/user/hadoop"));
        fileSystem.close();
    }

    @Test
    public void downloadFile() throws IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fileSystem = FileSystem.get(config);
        fileSystem.copyToLocalFile(new Path("hdfs://node01:8020/world.txt"), new Path("file:///d:\\world.txt"));
        fileSystem.close();
    }

    @Test
    public void deleteFile() throws IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fileSystem = FileSystem.get(config);
        fileSystem.delete(new Path("hdfs://node01:8020/hello.txt"),true);
        fileSystem.close();
    }

    @Test
    public void renameFile() throws IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fileSystem = FileSystem.get(config);
        fileSystem.rename(new Path("hdfs://node01:8020/world.txt"), new Path("hdfs://node01:8020/helloWorld.txt"));
        fileSystem.close();
    }

    @Test
    public void testListFiles() throws URISyntaxException, IOException {
        Configuration config = new Configuration();
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"), config);

        RemoteIterator<LocatedFileStatus> listFiles = fileSystem.listFiles(new Path("/kkb/dir01"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus status = listFiles.next();
            System.out.println(status.getPath().getName());
            System.out.println(status.getLen());
            System.out.println(status.getPermission());
            System.out.println(status.getGroup());
            BlockLocation[] blockLocations = status.getBlockLocations();
            for (BlockLocation block :blockLocations) {
                String[] hosts = block.getHosts();
                for (String host: hosts) {
                    System.out.println(host);
                }
            }
            System.out.println("=========================================");
        }

        fileSystem.close();
    }

    @Test
    public void putFileToHDFS() throws URISyntaxException, IOException {
        Configuration config = new Configuration();
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"), config);

        FileInputStream fis = new FileInputStream(new File("D:\\world.txt"));
        FSDataOutputStream fos = fileSystem.create(new Path("hdfs://node01:8020/world.txt"));

        IOUtils.copy(fis, fos);
        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(fis);

        fileSystem.close();
    }

    @Test
    public void mergeFile() throws URISyntaxException, IOException, InterruptedException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"), new Configuration(), "hadoop");

        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("hdfs://node01:8020/bigdata.xml"));

        LocalFileSystem localFileSystem = FileSystem.getLocal(new Configuration());
        FileStatus[] fileStatuses = localFileSystem.listStatus(new Path("file:///d:\\merge"));
        for (FileStatus status : fileStatuses) {
            Path path = status.getPath();
            FSDataInputStream fsDataInputStream = localFileSystem.open(path);

            IOUtils.copy(fsDataInputStream, fsDataOutputStream);
            IOUtils.closeQuietly(fsDataInputStream);
        }

        IOUtils.closeQuietly(fsDataOutputStream);
        localFileSystem.close();
        fileSystem.close();

    }
}
