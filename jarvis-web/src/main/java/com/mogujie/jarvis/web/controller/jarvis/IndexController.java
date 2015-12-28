package com.mogujie.jarvis.web.controller.jarvis;

import com.atlassian.crowd.exception.*;
import com.atlassian.crowd.model.user.User;
import com.mogu.bigdata.admin.core.util.JsonReturn;
import com.mogu.bigdata.admin.passport.session.SessionHelper;
import com.mogu.bigdata.admin.passport.user.UuapService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hejian on 15/9/14.
 */
@Controller
public class IndexController extends BaseController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private UuapService uuapService;

    @RequestMapping(value = "/")
    public String index(ModelMap mp) {
        if (null == user || StringUtils.isBlank(user.get().getUname())) {
            return "index";
        } else {
            mp.clear();
            return "redirect:/plan";
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public JsonReturn login(HttpServletRequest request, HttpServletResponse response, String uname, String password){
        try {
            com.mogu.bigdata.admin.core.entity.User user = uuapService.authenticate(request, response, uname, password);
            if (null == user) {
                return new JsonReturn(4004,"登录失败");
            }
            userService.insert(user);
            return new JsonReturn(1001, "/");
        } catch (Exception e) {
            log.error(e);
            return new JsonReturn(4001, e.getMessage());
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(ModelMap mp, HttpServletRequest request, HttpServletResponse response) throws Exception{
        uuapService.logout(request, response);
        mp.clear();
        return "redirect:/";
    }
}
