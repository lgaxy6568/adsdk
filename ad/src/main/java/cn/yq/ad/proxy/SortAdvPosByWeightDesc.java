package cn.yq.ad.proxy;

import java.util.Comparator;

import cn.yq.ad.proxy.model.AdRespItem;


public class SortAdvPosByWeightDesc implements Comparator<AdRespItem> {

	@Override
	public int compare(AdRespItem o1, AdRespItem o2) {
		if(o1.getWeight() < o2.getWeight()){
			return 1;
		}else if(o1.getWeight() > o2.getWeight()){
			return -1;
		}else{
			return 0;
		}
	}
}
