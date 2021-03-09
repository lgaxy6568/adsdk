package cn.yq.ad.gdt;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;

import cn.yq.ad.ADStyle;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class ReaderPageForGDT extends ADBaseImplByGDT {
    public ReaderPageForGDT(Activity act, String appId, String adId) {
        super(act, appId, adId);
    }

    public String getTAG(){
        return ReaderPageForGDT.class.getSimpleName();
    }

    @Override
    public View getAdvertEntityView(View view, Object obj) {
        if (!(obj instanceof AdNativeResponse)) {
            return null;
        }
        final AdNativeResponse nar = (AdNativeResponse) obj;
        View vv;
        if (nar.getAdvStyle() == ADStyle.READER_PAGE_VERTICAL) {
            vv = LayoutInflater.from(getContextFromView(view)).inflate(R.layout.layout_adv_for_gdt_pge_ver, null);
            Log.e(getTAG(),"getAdvertEntityView(),vv=layout_adv_for_gdt_pge_ver");
        } else {
            vv = LayoutInflater.from(getContextFromView(view)).inflate(R.layout.layout_adv_for_gdt_pge, null);
            Log.e(getTAG(),"getAdvertEntityView(),vv=layout_adv_for_gdt_pge");
        }
        return vv;
    }

    private  NativeUnifiedADData show_response;
    private NativeAdContainer show_ad_container;
    private View show_click_view;

    @Override
    public void showTj(ShowModel sm) {
        super.showTj(sm);
        boolean tj_upload = false;
        if(show_click_view != null && show_response != null && show_ad_container != null) {
            super.renderAd(act.get(), show_response, show_ad_container, show_click_view);
            tj_upload = true;
        }
        Log.e(getTAG(), "showTj(),tj_upload="+tj_upload);
    }

    @Override
    public void show(View vv, Object obj) {
        if (obj instanceof AdNativeResponse) {
            AdNativeResponse nar = (AdNativeResponse) obj;
            NativeUnifiedADData response = mNativeResponses.get(nar.getImageUrl());
            if(response == null){
                Log.e(getTAG(), "show(),response == null");
                return;
            }
            if(vv == null){
                Log.e(getTAG(), "show(),vv == null");
                return;
            }

            NativeAdContainer adContainer = vv.findViewById(R.id.native_ad_container);
            View click_view = vv.findViewById(R.id.layout_adv_for_tt_all_layout);

            show_response = response;
            show_ad_container = adContainer;
            show_click_view = click_view;
//            super.renderAd(act.get(),response,adContainer,click_view);

            updateBtnText(vv, nar);

            final ImageView iv_image = vv.findViewById(R.id.layout_adv_for_tt_image_view);
            String imgUrl = nar.getImageUrl();
            Log.e(getTAG(), "show(),img_url="+imgUrl);
            if (imgUrl != null && imgUrl.trim().length() > 0) {
//                PicassoUtil.show(iv_image,imgUrl);
            }

            final ImageView iv_icon = vv.findViewById(R.id.layout_adv_for_tt_icon_iv);

            //设置标题
            String str_title = response.getDesc();
            if (str_title == null || str_title.trim().length() == 0) {
                str_title = response.getTitle();
            }
            TextView tv_title = vv.findViewById(R.id.layout_adv_for_tt_tv);
            tv_title.setText(str_title);

            //设置描述
            String str_desc = response.getTitle();
            if (str_desc == null || str_desc.trim().length() == 0) {
                str_desc = DEFAULT_TITLE;
            }
            TextView tv_desc = vv.findViewById(R.id.layout_adv_for_tt_tv_desc);
            tv_desc.setText(str_desc);

            String iconUrl = nar.getIcon();
            if (iconUrl != null && iconUrl.trim().length() > 0) {
//                PicassoUtil.show(iv_icon,iconUrl);
            }
            Log.e(getTAG(), "show(),end,title="+str_title+",desc="+str_desc+",icon_url="+iconUrl);
        }
    }
}
