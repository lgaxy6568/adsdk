package cn.yq.ad.proxy;

import java.util.Comparator;

import cn.yq.ad.proxy.model.AdResponse;


public class SortAdvPosByPri implements Comparator<AdResponse> {

	@Override
	public int compare(AdResponse o1, AdResponse o2) {
		if(o1.getSort() < o2.getSort()){
			return 1;
		}else if(o1.getSort() > o2.getSort()){
			return -1;
		}else{
			return 0;
		}
	}
}
