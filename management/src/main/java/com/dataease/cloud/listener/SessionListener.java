package com.dataease.cloud.listener;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * 监听session变化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 3:10 PM
 **/
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        HttpSessionListener.super.sessionCreated(se);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        HttpSessionListener.super.sessionDestroyed(se);
    }
}
