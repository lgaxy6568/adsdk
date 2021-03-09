（1）发布文档：https://blog.csdn.net/yechaoa/article/details/68953567
（2）sdk集成文档：
    （1）在项目根目录的build.gradle文件中添加以下代码
        repositories {
                maven { url 'https://jitpack.io' }
        }
    （2）把open_ad_sdk.aar 和 GDTSDK_unionNormal_4_332_1202.aar这两个文件复制到model的libs目录下
    （3）在model的build.gradle文件中添加
        repositories{
            flatDir{
                dirs 'libs'
            }
        }
        dependencies {
            implementation 'com.github.lgaxy6568:adsdk:1.0.4'
            implementation (name: 'open_ad_sdk', ext: 'aar')
            implementation (name: 'GDTSDK_unionNormal_4_332_1202', ext: 'aar')
        }
     (4)添加混淆配置
        -dontwarn cn.yq.ad.**
        -keep class cn.yq.ad.** {*;}
        -keep interface cn.yq.ad.** {*;}
        -keep enum cn.yq.ad.** { *; }

        -dontwarn com.google.**
        -keep class com.google.** {*;}

        -dontwarn com.squareup.**
        -keep class com.squareup.** {*;}

        -dontwarn okio.**
        -keep class okio.** {*;}

        -dontwarn okhttp3.**
        -keep class okhttp3.** {*;}

        -dontwarn com.bumptech.**
        -keep class com.bumptech.** {*;}
        -keep enum com.bumptech.** {*;}
        -keep public class * implements com.bumptech.glide.module.GlideModule {*;}
        -keep class * extends com.bumptech.glide.module.AppGlideModule {*;}
