package com.sina.videoedit.util;

/**
 * 基本常量类
 * Created by wenbin8 on 2015/7/28 0028.
 */
public class Const {

    public static int DEFAULT_MARK = 0;
    /** 请求分页数据时的默认首页值，0 or 1 */
    public static int DEFAULT_FIRST_PAGE = 1;
    /** 首页关注列表 sincetime 阀值一天 */
    public static int LIMIT_Feed_SINCE_TIME = 1;
    /** 文章列表 sincetime 阀值一天 */
    public static int LIMIT_ARTICLE_LIST_SINCE_TIME = 1;
//    /** 收藏列表 sincetime 阀值一天 */
//    public static int LIMIT_FAVOURITE_LIST_SINCE_TIME = 1;
//    /** 首页推荐列表 sincetime 阀值一天 */
//    public static int LIMIT_RECOMMEND_SINCE_TIME = 1;

    /**
     * 各页面命名序号:
     * 1.代码复用时定位当前页或前一个页面来源
     * 2.统计时定位区分
     **/
    public static final int ATTENTION_NOT_LOGIN = 1;  //关注tab未登录
    public static final int ATTENTION_LOGIN = 2;  //关注tab已登录
    public static final int FIND_PAGE = 3;  //发现页
    public static final int MESSAGE_PAGE = 4;  //消息页
    public static final int ME_PAGE = 5;  //我
    public static final int ATTENTION_ADD = 6;  //添加关注页
    public static final int HOT_USER = 7;  //热门博主页
    public static final int HOT_THEME = 8;  //热门主题页
    public static final int ARTICLE_PAGE = 9;  //文章页

    public static final String UMENG_APPKEY = "532a514656240b414807bb8f";
    public static final String BAIDU_ECOLOGY_APPKEY = "600000025";
    public static final String BAIDU_ECOLOGY_SECRETKEY = "3097eb5976fa31ec07520f69bc25bd33";

    public static final int MAX_DISK_CACHE_SIZE = 40 * 1048576; // 40MB
    public static final String PLATFORM = "android";
    /** 安卓手机 */
    public static final String PLATFORM_PHONE = "aphone";

    //是否关注 0-未关注 1-已关注 2-不显示
    public static final int JSON_DATA_ATTENTION_0 = 0;
    public static final int JSON_DATA_ATTENTION_1 = 1;
    public static final int JSON_DATA_ATTENTION_2 = 2;

    //是否Vip 0否 1黄V 2蓝V
    public static final int USER_VIP_TYPE_NONE = 0;
    public static final int USER_VIP_TYPE_YELLOW = 1;
    public static final int USER_VIP_TYPE_BLUE = 2;

    public static final int SP_TEXTSIZE_XBIG = 4;
    public static final int SP_TEXTSIZE_BIG = 3;
    public static final int SP_TEXTSIZE_NORMAL = 2;
    public static final int SP_TEXTSIZE_SMALL = 1;

    public static final int APP_THEME_DAY = 0;
    public static final int APP_THEME_NIGHT = 1;

    /**
     *
     * 用于 Intent or Bundle 的Key
     */
    public static class BundleKey {
        public static final String PAGE_FROM = "page_from";
        public static final String DATA = "data";
        public static final String ACTION = "action";
        public static final String MESSAGE = "message";
        public static final String CODE = "code";
        public static final String POSITION = "position";

        public static final String LOGIN_USER_ID = "login_uid";
        public static final String ARTICLE_ID = "article_id";
        public static final String ARTICLE_TITLE = "article_title";
        public static final String ARTICLE_DESC = "article_desc";
        public static final String ARTICLE_READ_NUM = "article_read_number";
        public static final String BLOG_UID = "blog_uid";
        public static final String USER_NICK = "user_nick";
        public static final String IS_ATTENTION_LIST = "is_attention_list";
        public static final String IS_ALLOW_COMMENT = "isAllowComment";
        public static final String IS_FAVOURITE = "is_favourite";
        public static final String COLUMN_ID = "column_id";
        public static final String COLUMN_NAME = "column_name";
        public static final String COLUMN_Q= "column_q";

        public static final String FROM = "from";
        public static final String IMAGE = "image";
        public static final String URL = "url";
        public static final String DATA_LIST = "data_list";
        public static final String PUSH = "push";
        public static final String JUMP = "jump";
        /**个人中心跳转key：公开、私密、草稿**/
        public static final String USER_CENTER_JUMP = "user_center_jump";

        public static final String COMMENT = "COMMENT";  //评论内容
        public static final String MID = "mid"; //评论id
    }

    public static class SPKey {
        /** 启动配置信息的key */
        public static final String APP_LAUNCH_CONFIG = "app_launch_config";
        /** 覆盖安装删除旧模版 */
        public static final String NEED_INIT_THEME_DEL_HTML_TEMPLATE = "need_init_theme_del_html_template";
        /** 初始化栏目数据 */
        public static final String NEED_INIT_THEME_CHANNEL_DATA = "need_init_theme_channel_data_v418";
        /** 安装主题版本首次进入先到发现页 */
        public static final String FIRST_THEME_TO_FIND_PAGE = "first_theme_to_find_page";
        /** 焦点图的json数据 */
        public static final String DATA_FOCUS_JSON = "data_focus_json";
        /** 本地请求推荐的标志位mark_start */
        public static final String RECOMMEND_LIST_MARK_START = "recommend_list_mark_start_new";
        /** 本地请求推荐的标志位mark_end */
        public static final String RECOMMEND_LIST_MARK_END = "recommend_list_mark_end_new";
        /** 本地请求推荐的时间 */
        public static final String RECOMMEND_LIST_REQUEST_TIME = "recommend_list_request_time";
        /** Feed流Header头图 */
        public static final String FEED_LIST_HEADER_IMAGE = "feed_list_header_image";
        /** 本地请求Feed流的标志位mark_start */
        public static final String FEED_LIST_MARK_START = "feed_list_mark_start";
        /** 本地请求Feed流的标志位mark_end */
        public static final String FEED_LIST_MARK_END = "feed_list_mark_end";
        /** 本地请求Feed流的时间 */
        public static final String FEED_LIST_REQUEST_TIME = "feed_list_request_time";
        /** 当前模板配置信息 */
        public static final String CURRENT_TEMPLATE = "current_template";
        /** 栏目未登录显示 */
        public static final String PROGRAM_LOGIN = "program_login";
        /** 登陆同步未登陆设置的栏目ids */
        public static final String PROGRAM_SYNC_PIDS = "program_sync_pids";

        public static final String RECOMMEND_THEME_MARK_START = "recommend_theme_mark_start";
        public static final String RECOMMEND_THEME_MARK_END = "recommend_theme_mark_end";
        public static final String RECOMMEND_USER_MARK_START = "recommend_user_mark_start";
        public static final String RECOMMEND_USER_MARK_END = "recommend_user_mark_end";

        /** 已登陆关注页，是否显示文章列表 */
        public static final String ATTENTION_ARTICLE = "attention_article";

        //------------  个人主页
        /** 本地请求个人主页标志位 mark_start*/
        public static final String USER_ARTICLE_LIST_MARK_START = "user_article_list_mark_start";
        /** 本地请求个人主页标志位 mark_end*/
        public static final String USER_ARTICLE_LIST_MARK_END = "user_article_list_mark_end";
        /** 本地请求个人主页的时间 */
        public static final String USER_ARTICLE_LIST_REQUEST_TIME = "user_article_list_request_time";
        /** 自己头像更新的 本地版本 标志 */
        public static final String SELF_AVATAR_VERSION = "self_avatar_version";
        /** 自己头像更新的 服务端 版本号 */
        public static final String REMOTE_AVATAR_VERSION = "remote_avatar_version";
        /** 保存字号的sp文件 */
        public static final String SP_FILE_TEXTSIZE = "sp_file_textsize";
        /** 保存微博账户信息的sp文件 */
        public static final String SP_FILE_WEIBO_INFO = "sp_file_weibo_info";
        /** 字号 */
        public static final String SP_KEY_TEXTSIZE = "sp_key_textsize";


        /** apk使用系统DownloadManager时的id */
        public static final String APK_DOWNLOAD_ID = "apk_download_id";
        /** app检测更新时的时间 */
        public static final String APK_CHECK_UPDATE_TIME = "apk_check_update_time";

        /** 记录到本地的Push_Aid的Key值 */
        public static final String PUSH_AID = "push_aid";
        /** Push开关的Key值 */
        public static final String PUSH_SERVICE_ENABLED = "push_service_enabled";
        /** 上传PushToken的结果 */
        public static final String UPLOAD_PUSH_TOKEN_RESULT = "upload_push_token_result";

        /** 图片只在WIFI下下载的开关 */
        public static final String IMAGE_LOAD_ONLY_WIFI = "image_load_only_wifi";

        //user info
        public static final String SP_KEY_USER_NICK = "sp_key_user_nick";
        public static final String SP_KEY_USER_AVATAR_URL = "sp_key_user_avatar_url";
        public static final String SP_KEY_USER_AVATAR_URL_BIG = "sp_key_user_avatar_url_big";
        public static final String SP_KEY_USER_BG_PIC = "sp_key_user_bg_pic";
        public static final String SP_KEY_USER_ATTENTION_COUNT = "sp_key_user_attention_count";
        public static final String SP_KEY_USER_ATTENTION_BY_COUNT = "sp_key_user_attention_by_count";
        public static final String SP_KEY_USER_VIP_TYPE = "sp_key_user_vip_type";
        public static final String SP_KEY_USER_FAV_COUNT = "sp_key_user_fav_count";
        public static final String SP_KEY_USER_RESUME = "sp_key_user_resume";
        public static final String SP_KEY_USER_ARTICLE_COUNT = "sp_key_user_article_count";
        public static final String SP_KEY_USER_ARTICLE_PUBLIC_NUM = "sp_key_user_article_public_num";
        public static final String SP_KEY_USER_ARTICLE_SECRET_NUM = "sp_key_user_article_secret_num";
        public static final String SP_KEY_USER_ARTICLE_DRAFT_NUM = "sp_key_user_article_draft_num";
        public static final String SP_KEY_USER_THEME_COUNT = "sp_key_user_theme_count";
        public static final String SP_KEY_USER_ATTENTION_THEME_COUNT = "sp_key_user_attention_theme_count";
        public static final String SP_KEY_USER_MAX_THEME_NUM = "sp_key_user_max_theme_num";

        //usersetting
        public static final String SP_KEY_USER_BIND_WEIBO = "sp_key_user_bind_weibo";
        public static final String SP_KEY_USER_PUSH_WEIBO = "sp_key_user_push_weibo";
        public static final String SP_KEY_USER_WEIBO_NICK = "sp_key_user_weibo_nick";
        public static final String SP_KEY_USER_WEIBO_UID = "sp_key_user_weibo_uid";
        public static final String SP_KEY_USER_PIC_VERSION = "sp_key_user_pic_version";

//        public static final String SP_KEY_SHOW_GUIDE_LAYOUT = "sp_key_show_guide_layout";
        public static final String SP_KEY_SHOW_GUIDE_LAYOUT = "sp_key_show_guide_theme_layout";

        //秒拍token等
        public static final String SP_KEY_MIAOPAI_TOKEN = "sp_key_miaopai_token";
        public static final String SP_KEY_MIAOPAI_MPID = "sp_key_miaopai_MPID";

        /**
         * app主题
         */
        public static final String APP_THEME_MODE = "app_theme_mode";

    }

    public static class ThemeDay {
        public static final int COLOR_BACKGROUND = 0xffffffff;
        public static final int COLOR_BACKGROUND2 = 0xffededed;
        public static final int COLOR_TEXT = 0xff8c8c8c;
        public static final int COLOR_TEXT2 = 0xff333333;
        public static final int COLOR_TEXT3 = 0xff434343;
        public static final int COLOR_TEXT4 = 0xff000000;
        public static final int COLOR_DIVIDER = 0xffeeeeee;
        public static final int COLOR_DIVIDER2 = 0xff666666;
        public static final int COLOR_OTHER = 0xffff7043;
        public static final float ALPHA_VIEW = 1;
        public static final float ALPHA_IMG = 1;
    }

    public static class ThemeNight {
        public static final int COLOR_BACKGROUND = 0xff191919;
        public static final int COLOR_BACKGROUND2 = 0xff121212;
        public static final int COLOR_TEXT = 0xff808080;
        public static final int COLOR_TEXT2 = 0xff666666;
        public static final int COLOR_DIVIDER = 0xff2e2e2e;
        public static final int COLOR_DIVIDER2 = 0xff262626;
        public static final int COLOR_OTHER = 0xff574437;
        public static final int COLOR_OTHER2 = 0xff75331e;
        public static final int COLOR_OTHER3 = 0xff262626;
        public static final int COLOR_OTHER4 = 0xff704f39;
        public static final int COLOR_OTHER5 = 0xffa34424;
        public static final float ALPHA_VIEW = 0.4f;
        public static final float ALPHA_IMG = 0.6f;
    }

}
