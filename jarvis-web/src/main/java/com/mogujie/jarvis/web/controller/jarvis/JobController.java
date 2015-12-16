package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.qo.AppQo;
import com.mogujie.jarvis.web.entity.vo.*;
import com.mogujie.jarvis.web.service.AppService;
import com.mogujie.jarvis.web.service.JobDependService;
import com.mogujie.jarvis.web.service.JobService;
import com.mogujie.jarvis.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    AppService appService;
    @Autowired
    JobDependService jobDependService;

    /*
    * job任务管理首页
    * @author hejian
    * */
    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.job)
    public String index(ModelMap modelMap) {
        List<String> submitUsers = jobService.getSubmitUsers();

        List<AppVo> appVoList = appService.getAppList(new AppQo());
        List<WorkerGroupVo> workerGroupVoList = workerService.getAllWorkerGroup();

        modelMap.put("submitUsers", submitUsers);
        modelMap.put("appVoList", appVoList);
        modelMap.put("workerGroupVoList", workerGroupVoList);
        return "job/index";
    }

    /*
    * job任务新增或编辑页
    * @author hejian
    * */
    @RequestMapping(value = "addOrEdit")
    @JarvisPassport(authTypes = JarvisAuthType.job, isMenu = false)
    public String addOrEdit(ModelMap modelMap, Long jobId) {
        AppQo appQo = new AppQo();
        appQo.setStatus(1);
        List<AppVo> appVoList = appService.getAppList(appQo);

        if (jobId != null) {
            JobVo jobVo = jobService.getJobById(jobId);
            modelMap.put("jobVo", jobVo);

            Integer appId = jobVo.getAppId();
            AppVo appVo = appService.getAppById(appId);
            if (appVo != null && appVo.getStatus() == 0) {
                appVoList.add(appVo);
            }

            List<JobDependVo> jobDependVoList = jobDependService.getParentById(jobId);
            List<String> parentIds = new ArrayList<String>();
            Map<String,Object> map = new HashMap<String, Object>();
            for (JobDependVo jobDependVo : jobDependVoList) {
                parentIds.add(jobDependVo.getId().toString());
                map.put(jobDependVo.getId().toString(), jobDependVo);
            }
            String ids = JsonHelper.toJson(parentIds);
            modelMap.put("dependIds", ids);
            modelMap.put("dependJobs",JsonHelper.toJson(map));
        }


        List<WorkerGroupVo> WorkerGroupVoList = workerService.getAllWorkerGroup();

        List<JobVo> jobVoList = jobService.getAllJobs(1);

        modelMap.put("WorkerGroupVoList", WorkerGroupVoList);
        modelMap.put("appVoList", appVoList);
        modelMap.put("jobVoList", jobVoList);
        return "job/addOrEdit";
    }

    /*
    * 检查job名字是否重复
    * @author hejian
    * */
    @RequestMapping("checkJobName")
    @ResponseBody
    public Map<String,Object> checkJobName(Long jobId, String jobName) {
        Map<String,Object> result = new HashMap<String, Object>();

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
