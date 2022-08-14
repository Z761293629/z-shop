package com.zhaoyg.component;

/**
 * @author zhao
 * @date 2022/8/12
 */
public interface MailService {


    /**
     * 发送简单邮件
     *
     * @param to      接受者
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMail(String to, String subject, String content);
}
