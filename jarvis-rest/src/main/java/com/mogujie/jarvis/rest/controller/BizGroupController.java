package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.BizGroupProtos.*;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.ValidUtils;
import com.mogujie.jarvis.rest.utils.ValidUtils.CheckMode;
import com.mogujie.jarvis.rest.vo.BizGroupResultVo;
import com.mogujie.jarvis.rest.vo.BizGroupVo;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author muming
 */
@Path("api/bizGroup")
public class BizGroupController extends AbstractController {

    /**
     * 追加——bizGroup
     */
    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("user") String user,
                          @FormParam("appToken") String appToken,
                          @FormParam("appName") String appName,
                          @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            BizGroupVo vo = JsonHelper.fromJson(parameters, BizGroupVo.class);
            ValidUtils.checkBizGroup(CheckMode.ADD, vo);

            RestCreateBizGroupRequest request = RestCreateBizGroupRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setName(vo.getName())
                    .setStatus(vo.getStatus())
                    .setOwner(vo.getOwner())
                    .build();

            ServerCreateBizGroupResponse response = (ServerCreateBizGroupResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult(new BizGroupResultVo().setId(response.getId()))
                    : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e);
        }
    }

    /**
     * 修改——bizGroup
     */
    @POST
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult set(@FormParam("user") String user,
                          @FormParam("appName") String appName,
                          @FormParam("appToken") String appToken,
                          @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            BizGroupVo vo = JsonHelper.fromJson(parameters, BizGroupVo.class);
            ValidUtils.checkBizGroup(CheckMode.EDIT, vo);

            RestModifyBizGroupRequest.Builder builder = RestModifyBizGroupRequest.newBuilder();
            builder.setAppAuth(appAuth).setUser(user).setId(vo.getId());
            if (vo.getName() != null) {
                builder.setName(vo.getName());
            }
            if (vo.getStatus() != null) {
                builder.setStatus(vo.getStatus());
            }
            if (vo.getOwner() != null) {
                builder.setOwner(vo.getOwner());
            }
            RestModifyBizGroupRequest request = builder.build();

            ServerModifyBizGroupResponse response = (ServerModifyBizGroupResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    /**
     * 删除——bizGroup
     */
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("user") String user,
                             @FormParam("appName") String appName,
                             @FormParam("appToken") String appToken,
                             @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            BizGroupVo vo = JsonHelper.fromJson(parameters, BizGroupVo.class);
            ValidUtils.checkBizGroup(CheckMode.DELETE, vo);

            RestDeleteBizGroupRequest request = RestDeleteBizGroupRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setId(vo.getId())
                    .build();

            ServerDeleteBizGroupResponse response = (ServerDeleteBizGroupResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

}
