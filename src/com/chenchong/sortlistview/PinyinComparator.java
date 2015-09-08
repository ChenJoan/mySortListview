package com.chenchong.sortlistview;

import java.util.Comparator;

/**
 * 
 * 类名PinyinComparator
 *	实现的主要功能。拼音排序
 *	创建日期2015-9-6
 *	创建人 chenchong <br/>
 *	联系QQ:695933593
 */
public class PinyinComparator implements Comparator<SortModel> {

	public int compare(SortModel o1, SortModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
