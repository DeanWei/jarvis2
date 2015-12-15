/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月28日 下午4:43:25
 */

package com.mogujie.jarvis.tasks.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.exeception.TaskException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.worker.TaskContext;

public class JavaTask extends ShellTask {

    private static final String HDFS_ROOT_PATH = ConfigUtils.getWorkerConfig().getString("hdfs.jar.root.path");
    private static final String LOCAL_ROOT_PATH = ConfigUtils.getWorkerConfig().getString("local.jar.root.path");
    private static final Logger LOGGER = LogManager.getLogger();

    private TaskDetail taskDetail;
    private String hdfsDir;
    private String localDir;

    private String mainClass;
    private String args;
    private String jar;
    private String classpath;

    public JavaTask(TaskContext taskContext) {
        super(taskContext);
        taskDetail = getTaskContext().getTaskDetail();
        long jobId = IdUtils.parse(taskDetail.getFullId(), IdType.JOB_ID);
        hdfsDir = HDFS_ROOT_PATH + "/" + taskDetail.getUser() + "/" + jobId + "/";
        localDir = LOCAL_ROOT_PATH + "/" + taskDetail.getUser() + "/" + jobId + "/";
        mainClass = taskDetail.getParameters().get("mainClass").toString();
        args = taskDetail.getParameters().get("args").toString();
        jar = taskDetail.getParameters().get("jar").toString();
        classpath = taskDetail.getParameters().get("classpath").toString();
    }

    private void downloadJarFromHDFS(String filename) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path p1 = new Path("hdfs://" + hdfsDir);
        Path p2 = new Path("file://" + localDir);

        if (!fs.exists(p1)) {
            throw new FileNotFoundException(hdfsDir);
        }

        long hdfsJarModificationTime = fs.getFileLinkStatus(p1).getModificationTime();
        File localFile = new File(localDir);
        File parentFile = new File(localFile.getParent());
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            if (!result) {
                throw new IOException("mkdirs failed: " + parentFile.getPath());
            }
        }

        if (!localFile.exists() || hdfsJarModificationTime > localFile.lastModified()) {
            fs.copyToLocalFile(p1, p2);
            boolean result = localFile.setLastModified(hdfsJarModificationTime);
            if (!result) {
                LOGGER.error("File [{}] modified failed", localFile.getPath());
            }
            fs.close();
        }
    }

    @Override
    public void preExecute() throws TaskException {
        try {
            downloadJarFromHDFS(jar);
            List<String> cps = Arrays.asList(classpath.split(","));
            for (String cp : cps) {
                downloadJarFromHDFS(cp);
            }

            File file = new File(localDir);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    String filename = f.getName();
                    if (!filename.equals(jar) && !cps.contains(filename)) {
                        boolean deleted = new File(localDir + "/" + filename).delete();
                        if (!deleted) {
                            throw new TaskException("File[" + f.getPath() + "] delete failed");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    protected String getCmd(String jar, List<String> classpath, String mainClass, String args) {
        classpath.add(jar);
        return "java -cp " + Joiner.on(":").join(classpath) + " " + mainClass + " " + args;
    }

    @Override
    public String getCommand() {
        List<String> list = new ArrayList<>();
        for (String cp : classpath.split(",")) {
            list.add(localDir + "/" + cp);
        }
        return getCmd(localDir + "/" + jar, list, mainClass, args);
    }

}
