package com.cool.modules.user.proxy;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.cool.core.util.CoolPluginInvokers;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;

@Service
public class WxProxy {
    public WxMaService getWxMaService() {
        return (WxMaService)CoolPluginInvokers.invoke("wx", "getWxMaService");
    }
    public WxMpService getWxMpService() {
        return  (WxMpService)CoolPluginInvokers.invoke("wx", "getWxMpService");
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
