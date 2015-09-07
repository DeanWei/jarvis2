/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午1:18:35
 */
package com.mogujie.jarvis.jobs.util;

import com.mogujie.jarvis.core.exeception.ShellException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuya
 */
public class YarnUtils {

    /**
     * APP状态
     */
    public enum AppStatus {
        ACTIVE, //活跃（除了运行结束的与删除以外）
        ALL     //全部
    }

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 删除APP_通过APP名称
     *
     * @param applicationName   ：APP名称
     * @throws InvalidParameterException
     * @throws ShellException
     */
    public static void killApplicationByName(String applicationName)
            throws InvalidParameterException, ShellException {

        //获取appIDs
        Set<String> applicationIdSet = getApplicationIds(applicationName, AppStatus.ACTIVE);
        if (applicationIdSet == null) {
            return;
        }

        //删除apps
        killApplicationByIds(applicationIdSet);

    }

    /**
     * 删除APP_通过AppIDs
     *
     * @param ids   : appIDs
     * @throws ShellException
     */
    public static void killApplicationByIds(Set<String> ids)
            throws ShellException {

        if (ids == null || ids.isEmpty()) {
            return;
        }

        // kill掉yarn application
        StringBuilder sb = new StringBuilder();
        for (String applicationId : ids) {
            sb.append("yarn application -kill ").append(applicationId).append(";");
        }

        ShellUtils.executeShell(sb.toString());

    }


    /**
     * 根据app名称获取appIDs
     *
     * @param applicationName ：app名称
     * @param appStatus       ：app状态
     * @return appIds
     */
    public static  Set<String> getApplicationIds(String applicationName, AppStatus appStatus)
            throws InvalidParameterException, ShellException {

        final String cmd = "yarn application -list -appStates NEW,NEW_SAVING,SUBMITTED,ACCEPTED,RUNNING";

        if (applicationName == null || applicationName.isEmpty()) {
            throw new InvalidParameterException("application name is empty");
        }

        Set<String> applicationIdSet = new HashSet<>();
        BufferedReader br = ShellUtils.executeShell(cmd);

        try {

            Pattern APPLICATION_ID_PATTERN = Pattern.compile("(application_\\d+_\\d+)\\s+" + applicationName + "\\s+");
            String line;
            while ((line = br.readLine()) != null) {

                // 从输出日志中提取application id
                Matcher m = APPLICATION_ID_PATTERN.matcher(line);
                if (m.find()) {
                    applicationIdSet.add(m.group(1));
                }
            }
        } catch (IOException e) {
            throw new ShellException(e);
        }

        return applicationIdSet;

    }


}
