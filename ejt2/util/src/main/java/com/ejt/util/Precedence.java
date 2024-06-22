package com.ejt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Precedence {

	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Precedence.class);

	private Map<Integer, PrecedenceGroup> groups = null;
	public static final double tolerance = 0.0001;

	public Precedence() {
		groups = new HashMap<Integer, PrecedenceGroup>();
	}

	public void add(int order, PrecedenceGroup precGroup) {
		groups.put(new Integer(order), precGroup);
	}

	public List<Integer> determinePrecedenceList() {
		if (groups.size() > 100) {
			logger.warn("Maximum number of precedence groups is " + 100);
			return null;
		}

		int groupId = 1;
		Integer itemId1 = null;
		Double itemValue1 = null;
		Set<Integer> likeSet = new HashSet<Integer>();
		List<Integer> finalPrecList = new ArrayList<Integer>(), groupPrecList, likeGroupPrecList;

		PrecedenceGroup pg = (PrecedenceGroup) groups.get(new Integer(groupId));
		if (pg == null) {
			return finalPrecList;
		}

		groupPrecList = pg.determinePrecedenceList();
		if (groupPrecList == null || groupPrecList.isEmpty()) {
			return finalPrecList;
		}

		for (Integer itemId : groupPrecList) {
			Double itemValue = pg.get(itemId);
			if (itemValue1 == null) {
				itemId1 = itemId;
				itemValue1 = itemValue;
				likeSet.clear();
				likeSet.add(itemId);
			} else {
				if (Math.abs(itemValue1.doubleValue() - itemValue.doubleValue()) < Precedence.tolerance) {
					likeSet.add(itemId);
				} else {
					if (likeSet.size() > 1) {
						likeGroupPrecList = getLikeGroupPrecedenceList(groupId + 1, likeSet);
						for (Integer likeItemId : likeGroupPrecList) {
							finalPrecList.add(likeItemId);
						}
					} else {
						finalPrecList.add(itemId1);
					}
					itemId1 = itemId;
					itemValue1 = itemValue;
					likeSet.clear();
					likeSet.add(itemId);
				}
			}
		}
		if (likeSet.size() > 1) {
			likeGroupPrecList = getLikeGroupPrecedenceList(groupId + 1, likeSet);
			for (Integer likeItemId : likeGroupPrecList) {
				finalPrecList.add(likeItemId);
			}
		} else {
			finalPrecList.add(itemId1);
		}
		return finalPrecList;
	}

	private List<Integer> getLikeGroupPrecedenceList(int groupId, Set<Integer> inSet) {
		List<Integer> finalPrecList = new ArrayList<Integer>();
		PrecedenceGroup pg = groups.get(new Integer(groupId));
		if (pg == null) {
			for (Integer inSetItem : inSet) {
				finalPrecList.add(inSetItem);
			}
			return finalPrecList;
		}

		Integer itemId1 = null;
		Double itemValue1 = null;
		PrecedenceGroup pgSub = new PrecedenceGroup();
		Set<Integer> likeSet = new HashSet<Integer>();
		List<Integer> groupPrecList, likeGroupPrecList;
		for (Integer itemId : inSet) {
			Double itemValue = pg.get(itemId);
			if (itemValue == null) {
				continue;
			}
			pgSub.add(itemId.intValue(), itemValue.doubleValue());
		}

		if (pgSub.size() < inSet.size()) {
			for (Integer inSetItem : inSet) {
				finalPrecList.add(inSetItem);
			}
			return finalPrecList;
		}

		pgSub.setAscending(pg.isAscending());
		groupPrecList = pgSub.determinePrecedenceList();
		for (Integer itemId : groupPrecList) {
			Double itemValue = pg.get(itemId);
			if (itemValue1 == null) {
				itemId1 = itemId;
				itemValue1 = itemValue;
				likeSet.clear();
				likeSet.add(itemId);
			} else {
				if (Math.abs(itemValue1.doubleValue() - itemValue.doubleValue()) < Precedence.tolerance) {
					likeSet.add(itemId);
				} else {
					if (likeSet.size() > 1) {
						likeGroupPrecList = getLikeGroupPrecedenceList(groupId + 1, likeSet);
						for (Integer itemId2 : likeGroupPrecList) {
							finalPrecList.add(itemId2);
						}
					} else {
						finalPrecList.add(itemId1);
					}
					itemId1 = itemId;
					itemValue1 = itemValue;
					likeSet.clear();
					likeSet.add(itemId);
				}
			}
		}
		if (likeSet.size() > 1) {
			likeGroupPrecList = getLikeGroupPrecedenceList(groupId + 1, likeSet);
			for (Integer itemId : likeGroupPrecList) {
				finalPrecList.add(itemId);
			}
		} else {
			finalPrecList.add(itemId1);
		}
		return finalPrecList;
	}

	public Integer determineHighestPrecedenceItem() {
		List<Integer> precList = determinePrecedenceList();
		if (precList != null && !precList.isEmpty()) {
			return (Integer) precList.get(0);
		}
		return null;
	}

	public static boolean valuesAreNearlyEqual(double value1, double value2) {
		return Math.abs(value1 - value2) < Precedence.tolerance;
	}
}
