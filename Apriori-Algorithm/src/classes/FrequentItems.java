package classes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrequentItems<I> {

	private List<Set<I>> frequentItemsetList;
	private Map<Set<I>, Integer> supCountMap;
	private double minSup;
	private int numberOfTransactions;

	FrequentItems(List<Set<I>> frequentItemsetList, Map<Set<I>, Integer> supportCountMap, double minimumSupport,int transactionNumber) {
		this.frequentItemsetList = frequentItemsetList;
		this.supCountMap = supportCountMap;
		this.minSup = minimumSupport;
		this.numberOfTransactions = transactionNumber;
	}

	public List<Set<I>> getFrequentItemsetList() {
		return frequentItemsetList;
	}

	public Map<Set<I>, Integer> getSupportCountMap() {
		return supCountMap;
	}

	public double getMinimumSupport() {
		return minSup;
	}

	public int getTransactionNumber() {
		return numberOfTransactions;
	}

	public double getSupport(Set<I> itemset) {
		// return 1.0 * supportCountMap.get(itemset) / numberOfTransactions;
		return supCountMap.get(itemset);
	}
}