package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.mapper.JobDependMapper;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hejian on 15/9/22.
 */
@Service
public class JobDependService {
    @Autowired
    JobDependMapper jobDependMapper;

    /**
     * 获取所有依赖于此job的job
     * */
    public JSONObject getTreeDependedOnJob(JobSearchVo jobSearchVo){
        JSONObject jsonObject=new JSONObject();
        JobDependVo jobDependVo=jobDependMapper.getJobById(jobSearchVo.getJobId());

        if(jobDependVo==null){
            return jsonObject;
        }
        JSONObject jsonState =new JSONObject();
        jsonState.put("opened", true);

        List<JobDependVo> jobDependVoChildrenList=getChildren(jobDependVo);
        jobDependVo.setChildren(jobDependVoChildrenList);

        jsonObject=(JSONObject)JSON.toJSON(jobDependVo);
        jsonObject.put("state",jsonState);

        return jsonObject;
    }

    /**
     * 获取所有依赖于此job的job
     * */
    public JSONObject getTwoDirectionTreeDependedOnJob(JobSearchVo jobSearchVo){
        JSONObject jsonObject=new JSONObject();
        JobDependVo jobDependVo=jobDependMapper.getJobById(jobSearchVo.getJobId());
        jobDependVo.setName(jobDependVo.getText());
        jobDependVo.setValue(jobDependVo.getId());

        if(jobDependVo==null){
            return jsonObject;
        }
        JSONObject jsonState =new JSONObject();
        jsonState.put("opened", true);

        List<JobDependVo> jobDependVoChildrenList=getChildren(jobDependVo);
        jobDependVo.setChildren(jobDependVoChildrenList);


        List<JobDependVo> jobDependVoParentList=getParents(jobDependVo);
        jobDependVo.setParents(jobDependVoParentList);

        jsonObject=(JSONObject)JSON.toJSON(jobDependVo);
        jsonObject.put("state",jsonState);

        return jsonObject;
    }

    /**
     * 递归获取所有子节点
     * */
    public List<JobDependVo> getChildren(JobDependVo jobDependVo){
        List<JobDependVo> jobChildren=jobDependMapper.getChildrenById(jobDependVo.getId());
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("opened",true);
        if(jobChildren!=null&&jobChildren.size()>0){
            for(JobDependVo childJob:jobChildren){
                childJob.setState(jsonObject);
                childJob.setName(childJob.getText());
                childJob.setValue(childJob.getId());
                childJob.setChildren(getChildren(childJob));
            }
        }

        return jobChildren;
    }
    /**
     * 递归获取所有父节点
     * */
    public List<JobDependVo> getParents(JobDependVo jobDependVo){
        List<JobDependVo> jobParents=jobDependMapper.getParentById(jobDependVo.getId());
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("opened",true);
        if(jobParents!=null&&jobParents.size()>0){
            for(JobDependVo parentJob:jobParents){
                parentJob.setState(jsonObject);
                parentJob.setParentFlag(true);
                parentJob.setName(parentJob.getText());
                parentJob.setValue(parentJob.getId());
                parentJob.setParents(getParents(parentJob));
            }
        }

        return jobParents;
    }
    /**
    * 获取最近父节点
    * */
    public List<JobDependVo> getParentById(Long jobId){
        return jobDependMapper.getParentById(jobId);
    }
}
