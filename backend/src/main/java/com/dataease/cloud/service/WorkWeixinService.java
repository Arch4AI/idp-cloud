package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.security.social.secret.impl.WechatworkExternalSecret;
import com.dataease.cloud.utils.JsonUtils;
import com.dataease.cloud.utils.wx.WXBizMsgCrypt;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * 企业微信解析数据
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/20 4:08 PM
 **/
@Service
@Slf4j
public class WorkWeixinService {

    @Resource
    private CloudSocialSettingMapper cloudSocialSettingMapper;

    /**
     * 验证指令回调 对应的是Get方法
     *
     * @param msgSignature   加密串
     * @param timestamp      时间戳
     * @param nonce          随机数
     * @param echostr        验证字符串
     * @param registrationId 社交配置ID
     */
    public String verfiy(String msgSignature, String timestamp, String nonce, String echostr, String registrationId) {
        CloudSocialSetting setting = cloudSocialSettingMapper.selectByPrimaryKey(registrationId);
        WechatworkExternalSecret wechatworkExternalSecret = JsonUtils.jsonToObject(setting.getAuthSecret(), WechatworkExternalSecret.class);
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(wechatworkExternalSecret.getToken(), wechatworkExternalSecret.getEncodingAESKey(), wechatworkExternalSecret.getCorpId());
            String verifyStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            return verifyStr;
        } catch (Exception e) {
            log.error("work weixin verfiy error:", e);
        }
        return "error";
    }

    /**
     * 指令回调
     *
     * @param msgSignature   加密字符串
     * @param timestamp      时间戳
     * @param nonce          随机数
     * @param body           请求体
     * @param registrationId 社交配置ID
     * @return
     */
    public String notify(String msgSignature, String timestamp, String nonce, String body, String registrationId) {
        CloudSocialSetting setting = cloudSocialSettingMapper.selectByPrimaryKey(registrationId);
        WechatworkExternalSecret wechatworkExternalSecret = JsonUtils.jsonToObject(setting.getAuthSecret(), WechatworkExternalSecret.class);
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(wechatworkExternalSecret.getToken(), wechatworkExternalSecret.getEncodingAESKey(), wechatworkExternalSecret.getClientId());
            String sMsg = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, body);
            decryptMessage(sMsg);
            return "success";
        } catch (Exception e) {
            log.error("get work weixin suite token error:", e);
        }
        return "error";
    }

    /**
     * 解析数据
     * 在这里
     * @param message
     * @throws Exception
     */
    private void decryptMessage(String message) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(message);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);
        Element root = document.getDocumentElement();
        String infoType = getElementTextByTagName("InfoType",root);
        String ticket = getElementTextByTagName("SuiteTicket",root);
        if(StringUtils.hasText(ticket))
            log.info("suite ticket:{}",ticket);
        if(StringUtils.hasText(infoType))
            log.info("info type:{}",infoType);
    }

    /**
     * 根据element获取tagName下面的第一个值
     * @param tagName
     * @param element
     * @return
     */
    private String getElementTextByTagName(String tagName,Element element){
        NodeList nodeList = element.getElementsByTagName(tagName);
        if(nodeList != null && nodeList.getLength()>0){
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
