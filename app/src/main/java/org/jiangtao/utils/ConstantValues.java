package org.jiangtao.utils;

import android.os.Environment;

/**
 * Created by mr-jiang on 15-11-13.
 */
public class ConstantValues {
    /**
     * 192.168.1.105旁边寝室
     * 192.168.1.118上课
     */
    /**
     * 保存图片的目录
     */
    public static final String saveImageUri = Environment.getExternalStorageDirectory()
            + "/lifetime/headImage/";
    /**
     * 保存便签的内容
     */
    public static final String saveNoteUri = Environment.getExternalStorageDirectory() + "/lifetime/note/";
    /**
     * 保存缓存的目录
     */
    public static final String saveCacheUri = Environment.getExternalStorageDirectory() + "/lifetime/cache/";

    public static final String url = "http://188.166.253.87:8080/LifeTimeBackstage/";
    //欢迎界面的url
    public static final String welcomeUrl = url + "welcome.action";
    //请求获得验证码
    public static final String verificationCodeUrl = url + "sendvalidate.action";
    //提交用户基本信息，包括用户名，密码等信息
    public static final String registerInformationUrl = url + "registerinformation.action";
    //提交用户头像信息。
    public static final String registerImageUrl = url + "registerImage.action";
    //登陆
    public static final String loginUrl = url + "loginRequest.action";
    //获取用户头像
    public static final String userImageUrl = url + "headImage.action";
    //找回密码
    public static final String retrievePasswordUrl = url + "retrievePassword.action";
    //上传图片文章
    public static final String uploadImageArticleUrl = url + "uploadImageArticleUrl.action";
    //获取所有文章的list列表
    public static final String getAllArticleUrl = url + "getAllArticle.action";
    //获取文章的图片
//    public static final String getArticleImageUrl = url+"getArticleImage.action";
    //get请求加载图片
    public static final String getArticleImageUrl = url + "getArticleImage.action?user_image=";
    //点击用户头像，获取别的用户的信息
    public static final String getOtherUserInfoUrl = url + "obtainUserInfo.action";
    //请求文章的评论
    public static final String CommentOperater = url + "getComment.action";
    //判断用户之间是否为好友
    public static final String isFriendUrl = url + "isFriend.action";
    //用户相互关注
    public static final String attentionUserUrl = url + "attentionUser.action";
    //用户之间取消关注
    public static final String deleteFriendUrl = url + "deleteFriend.action";
    //添加评论信息
    public static final String addCommentUrl = url + "addComment.action";
    //获取所有朋友列表
    public static final String allFriendUrl = url + "allFriend.action";

}
