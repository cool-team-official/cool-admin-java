package com.cool.modules.user.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.util.ObjUtil;
import com.cool.core.util.CoolPluginInvokers;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;

@Service
public class WxService {
    private WxMaService wxMaService;

    private WxMpService wxMpService;
    private WxMaService getWxMaService() {
        if (ObjUtil.isNotEmpty(wxMaService)) {
            return wxMaService;
        }
        wxMaService = (WxMaService)CoolPluginInvokers.invoke("wx", "getWxMaService");
        return wxMaService;
    }
    private WxMpService getWxMpService() {
        if (ObjUtil.isNotEmpty(wxMpService)) {
            return wxMpService;
        }
        wxMpService = (WxMpService)CoolPluginInvokers.invoke("wx", "getWxMpService");
        return wxMpService;
    }

    public WxMaJscode2SessionResult getSessionInfo(String jsCode) throws WxErrorException {
        return getWxMaService().getUserService()
            .getSessionInfo(jsCode);
    }

    public WxMaPhoneNumberInfo getPhoneNumber(String jsCode) throws WxErrorException {
        return getWxMaService().getUserService()
            .getPhoneNumber(jsCode);
    }

    public WxMaUserInfo getUserInfo(String sessionKey, String encryptedData, String ivStr) throws WxErrorException {
        return getWxMaService().getUserService()
            .getUserInfo(sessionKey, encryptedData, ivStr);
    }

}
