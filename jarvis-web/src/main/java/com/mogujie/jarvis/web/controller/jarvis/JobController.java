package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.qo.AppQo;
import com.mogujie.jarvis.web.entity.vo.*;
import com.mogujie.jarvis.web.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/job")
public class JobController extends BaseController {

    @Autowired
    JobService jobService;
    @Autowired
    WorkerService workerService;
    @Autowired
    WorkerGroupService workerGroupService;
    static AppVo app = new AppVo();
    Logger logger = Logger.getLogger(JobController.class);

    static {
        try {
            InputStream inputStream = JobController.class.getClassLoader().getResourceAsStream("app.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            app.setAppId(Integer.parseInt(properties.getProperty("app.id")));
            app.setAppName(properties.getProperty("app.name"));
            app.setAppKey(properties.getProperty("app.key"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * job任务管理首页
    * @author hejian
    * */
    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.job)
    public String index(ModelMap modelMap) {
        modelMap.put("app", app);
        return "job/index";
    }

    /*
    * job任务新增或编辑页
    * @author hejian
    * */
    @RequestMapping(value = "addOrEdit")
    @JarvisPassport(authTypes = JarvisAuthType.job, isMenu = false)
    public String addOrEdit(ModelMap modelMap, Long jobId) {
        modelMap.put("app", app);
        modelMap.put("jobId", jobId);
        return "job/addOrEdit";
    }

    /*
    * 检查job名字是否重复
    * @author hejian
    * */
    @RequestMapping("checkJobName")
    @ResponseBody
    public Map<String, Object> checkJobName(Long jobId, String jobName) {
        Map<String, Object> result = new HashMap<String, Object>();

        JobVo jobVo = jobService.getJobByName(jobName);
        //新增job时校验
        if (jobId == null) {
            //已经存在此名字job
            if (jobVo != null) {
                result.put("code", 1);
                result.put("msg", "已存在此名字任务:" + jobName + ",不能新增");
            } else {
                result.put("code", 0);
                result.put("msg", "不存在此名字任务:" + jobName + ",可以新增");
            }
        }
        //已存在job的情况下校验
        else {
            if (jobVo != null) {
                if (jobVo.getJobId().equals(jobId)) {
                    result.put("code", 0);
                    result.put("msg", "任务名为本身，没修改:" + jobName + ",可以更新");
                } else {
                    result.put("code", 1);
                    result.put("msg", "已存在此名字任务:" + jobName + ",不能更新");
                }
            } else {
                result.put("code", 0);
                result.put("msg", "不存在此名字任务:" + jobName + ",可以更新");
            }
        }

        return result;
    }

    /*
    * job任务依赖页面
    * @author hejian
    * */
    @RequestMapping(value = "dependency")
    @JarvisPassport(authTypes = JarvisAuthType.job, isMenu = false)
    public String dependency(ModelMap modelMap, Long jobId) {
        JobVo jobVo = jobService.getJobById(jobId);

        if (jobVo == null) {
            jobVo = new JobVo();
        }

        modelMap.put("jobVo", JsonHelper.toJson(jobVo));
        return "job/dependency";
    }


}
