package cn.yq.ad;

import java.util.ArrayList;
import java.util.List;

public class ShareRequestParam {
    private ShareRequestParam() {
    }
    private static volatile ShareRequestParam spp = null;
    public static ShareRequestParam getInstance(){
        if(spp == null){
            synchronized (ShareRequestParam.class){
                if(spp == null){
                    spp = new ShareRequestParam();
                }
            }
        }
        return spp;
    }

    /**
     *
     // 用户维度：用户性别，取值：0-unknown，1-male，2-female
     .addExtra(ArticleInfo.USER_SEX, "1")

     // 用户维度：收藏的小说ID，最多五个ID，且不同ID用'/分隔'
     .addExtra(ArticleInfo.FAVORITE_BOOK, "这是小说的名称1/这是小说的名称2/这是小说的名称3")

     // 内容维度：小说、文章的名称
     .addExtra(ArticleInfo.PAGE_TITLE, "测试书名")

     // 内容维度：小说、文章的ID
     .addExtra(ArticleInfo.PAGE_ID, "10930484090")

     // 内容维度：小说分类，一级分类和二级分类用'/'分隔
     .addExtra(ArticleInfo.CONTENT_CATEGORY, "一级分类/二级分类")

     // 内容维度：小说、文章的标签，最多10个，且不同标签用'/分隔'
     .addExtra(ArticleInfo.CONTENT_LABEL, "标签1/标签2/标签3")

     */

    private int sex;

    private String pageTitle;

    private String pageId;

    private String contentCategory;

    private String contentLabel;

    private List<String> favoriteBook;

    public int getSex() {
        return sex;
    }

    public ShareRequestParam setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public ShareRequestParam setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public String getPageId() {
        return pageId;
    }

    public ShareRequestParam setPageId(String pageId) {
        this.pageId = pageId;
        return this;
    }

    public String getContentCategory() {
        return contentCategory;
    }

    public ShareRequestParam setContentCategory(String contentCategory) {
        this.contentCategory = contentCategory;
        return this;
    }

    public String getContentLabel() {
        return contentLabel;
    }

    public ShareRequestParam setContentLabel(String contentLabel) {
        this.contentLabel = contentLabel;
        return this;
    }

    public List<String> getFavoriteBook() {
        return favoriteBook;
    }

    public ShareRequestParam setFavoriteBook(List<String> favoriteBook) {
        this.favoriteBook = favoriteBook;
        return this;
    }

    public ShareRequestParam putFavoriteBookId(String bookID){
        if(bookID == null || bookID.trim().length() == 0){
            return this;
        }
        if(favoriteBook == null){
            favoriteBook = new ArrayList<>();
        }
        if(favoriteBook.size() >= 5){
            favoriteBook.remove(0);
        }
        favoriteBook.add(bookID);
        return this;
    }
}
