package cn.yq.ad.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.yq.ad.Adv_Status;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.BaseModel;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.util.AdStringUtils;

public abstract class AdvProxyByKaiPinAbstract extends ADBaseImpl {
    public static final String TARGET_NAME = "开屏";
    public static final String PAGE_NAME = "开屏";
    protected Map<String, String> extraMap;
    public final void setExtraMap(Map<String, String> extraMap) {
        this.extraMap = extraMap;
    }

    public abstract boolean isInited();

    //=============
    public static List<AdRespItem> sortLst(List<AdRespItem> apLst) {
        List<AdRespItem> newLst = new ArrayList<>();
        if (apLst == null || apLst.size() == 0) {
            return newLst;
        }
        int loopCount = 0;
        final int size = apLst.size();
        while (true) {
            AdRespItem ap = selectApBySortType(apLst);
            if (ap == null) {
                break;
            }
            apLst.remove(ap);
            newLst.add(ap);
            int sz = apLst.size();
            if (sz == 0) {
                break;
            }
            loopCount++;
            if (loopCount >= size) {
                break;
            }
        }
        return newLst;
    }

    /**
     * 根据【广告ID的权重】排序
     * @param apLst
     * @return
     */
    private static AdRespItem getApByWeight(List<AdRespItem> apLst) {
        if (apLst == null || apLst.size() == 0) {
            return null;
        }
        if (apLst.size() == 1) {
            return apLst.get(0);
        }
        int total = 0;
        for (AdRespItem ap : apLst) {
            total += ap.getWeight();
        }
        AdRespItem result = null;
        int ran = new Random().nextInt(total) + 1;
        int cur_num = 0;
        for (AdRespItem ap : apLst) {
            cur_num += ap.getWeight();
            if (cur_num >= ran) {
                result = ap;
                break;
            }
        }
        return result;
    }

    /**
     * 根据【广告ID的优先级】排序
     * @param apLst
     * @return
     */
    private static AdRespItem getApBySort(List<AdRespItem> apLst) {
        if (apLst == null || apLst.size() == 0) {
            return null;
        }
        if (apLst.size() == 1) {
            return apLst.get(0);
        }
        Collections.sort(apLst);
        return apLst.get(0);
    }

    public final int REQUEST_TIME_OUT_BY_TOTAL() {
        try {
            int a02 = AdConfigs.getRequestTimeOutByTotal();
            if (a02 > 20) {
                a02 = 20;
            }else if(a02 < 3){
                a02 = 3;
            }
            return a02 * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return 5000;
        }
    }

    private int REQUEST_TIME_OUT_BY_PLATFORM(Adv_Type advType) {
        try {
            int a02 = 5000;
            if(advType == Adv_Type.gdt){
                a02 = AdConfigs.getAdLoadTimeOutByGDT();
            }else if(advType == Adv_Type.tt){
                a02 = AdConfigs.getAdLoadTimeOutByCSJ();
            }
            if (a02 > 6000) {
                a02 = 6000;
            }else if(a02 < 3000){
                a02 = 3000;
            }
            return a02;
        } catch (Exception e) {
            e.printStackTrace();
            return 5000;
        }
    }

    public final int REQUEST_TIME_OUT_BY_GDT(){
        return REQUEST_TIME_OUT_BY_PLATFORM(Adv_Type.gdt);
    }

    public final int REQUEST_TIME_OUT_BY_CSJ(){
        return REQUEST_TIME_OUT_BY_PLATFORM(Adv_Type.tt);
    }

    /** 是否全屏广告 */
    public static boolean adIsFullScreen(BaseModel pm){
        if(pm == null){
            return false;
        }
        String ad_size_type = pm.get(ExtraKey.KP_AD_SIZE_TYPE_KEY);
        if(ExtraKey.KP_AD_SIZE_TYPE_VALUE_BAN_PING.equalsIgnoreCase(ad_size_type)){
            return false;
        }else{
            return true;
        }
    }

    public static String getTypeStr(Adv_Type ai_adv_type, BaseModel pm_adv){
        if (ai_adv_type == Adv_Type.gdt){
            if(adIsFullScreen(pm_adv)){
                return "广点通全屏";
            }else{
                return "广点通半屏";
            }
        }else if (ai_adv_type == Adv_Type.tt){
            if(adIsFullScreen(pm_adv)) {
                return "穿山甲全屏";
            }else{
                return "穿山甲半屏";
            }
        }else if (ai_adv_type == Adv_Type.api_magic_mobile){
            if(adIsFullScreen(pm_adv)) {
                return "API全屏";
            }else{
                return "API半屏";
            }
        }else if(ai_adv_type == Adv_Type.bai_du){
            return "百度全屏";
        }else{
            return ai_adv_type != null ? ai_adv_type.name() : "unknown2";
        }
    }

    public static String buildUmKey(Adv_Type at, Adv_Status status){
        if(at == Adv_Type.gdt){
            if(status == Adv_Status.fail){
                return "gdt_req_num_fail";
            }else if(status == Adv_Status.suc){
                return "gdt_req_num_suc";
            }else if(status == Adv_Status.start){
                return "gdt_req_num_total";
            }else if(status == Adv_Status.show){
                return "gdt_req_num_show";
            }
        }
        if(at == Adv_Type.tt){
            if(status == Adv_Status.fail){
                return "csj_req_num_fail";
            }else if(status == Adv_Status.suc){
                return "csj_req_num_suc";
            }else if(status == Adv_Status.start){
                return "csj_req_num_total";
            }else if(status == Adv_Status.show){
                return "csj_req_num_show";
            }
        }
        return at.name()+"_"+status.name();

    }

    private static AdRespItem selectOne(List<AdRespItem> apLst) {
        int adSdkSortType = AdConfigs.getAdSdkSortType();
        if(adSdkSortType == 1){
            return getApByWeight(apLst);
        }else{
            return getApBySort(apLst);
        }
    }

    public static AdRespItem selectApBySortType(List<AdRespItem> apLst) {
        if (apLst == null || apLst.size() == 0) {
            return null;
        }
        if (apLst.size() == 1) {
            return apLst.get(0);
        }
        Map<String,String> adTypeWeightMap = new LinkedHashMap<>();
        Map<String,List<AdRespItem>> mpLst = new HashMap<>();
        int index = 0;
        int maxWeight = 100;
        int totalSelfWeight = 0;
        for (AdRespItem ap : apLst) {
            //self_1,self_2,sdk
            String kk = ap.getType();
            if(AdStringUtils.isEmpty(kk)){
                continue;
            }
            index ++;
            String adTypeName = ap.getAdv_type_name();
            int tmpWeight;
            if(Adv_Type.self.name().equalsIgnoreCase(adTypeName)){
                kk = kk + "_"+index;
                tmpWeight = ap.getWeight();
                if(tmpWeight == 0){
                    continue;
                }
                totalSelfWeight += tmpWeight;
            }else{
                tmpWeight = Math.max(maxWeight - totalSelfWeight,0);
            }
            adTypeWeightMap.put(kk,String.valueOf(tmpWeight));

            List<AdRespItem> tmpLst = mpLst.get(kk);
            if(tmpLst == null){
                tmpLst = new ArrayList<>();
                tmpLst.add(ap);
                mpLst.put(kk,tmpLst);
            }else{
                tmpLst.add(ap);
            }
        }
        AdRespItem result = null;
        int ran = (int)(Math.random() * 100) + 1;
        int cur_num = 0;
        for (String kk : mpLst.keySet()) {
            int weight = Integer.parseInt(adTypeWeightMap.get(kk));
            cur_num += weight;
            if (cur_num >= ran) {
                List<AdRespItem> childList = mpLst.get(kk);
                if(childList.size() > 1){
                    result = selectOne(childList);
                }else if(childList.size() == 1){
                    result = childList.get(0);
                }else{
                    continue;
                }
                break;
            }
        }
        return result;
    }

}
