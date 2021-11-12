package classes;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class AprioriFunctions<I> {

	//fonction permet de retourner une liste qui contient les "items"(transactionList) depuis un fichier(DATASET),
	//Note: (La forme du fichier "DataSet")chaque transaction est dans un ligne, et les items sont séparer par des vergules. 
	//transactionList: [[A, C, D], [B, C, E], [A, B, C, E], [B, E]]
	public List<Set<I>> transactionListInitialiation(String file) throws IOException {
		
		File f = new File(file);
		try {
			checkFileIsEmbty(f);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		List<Set<I>> transactionList = new ArrayList<>();
		
		if (f.isFile() && f.canRead()) {
			Scanner myReader = new Scanner(f);
			
			while (myReader.hasNextLine()) {
				
			String line = myReader.nextLine();
			I[] item= (I[]) line.split(",");// split by "," to take the items 
			Set<I> items= new HashSet<>();
			for (I it : item) {
				items.add(it);
				
			}
			transactionList.add(items);
			
			}
			dis.close();
			fis.close();
		
		} else {
			System.out.println("error !!, please check the file name ! ");
		}
		System.out.println("transactionList: "+ transactionList);
		return transactionList;
	}

	
	
	// fonction permet de génèrer les données de jeu d'éléments fréquents,
	//prend en parametre (transactionList) la liste des transactions à extraire.
	//aussi le minimum support
	public FrequentItems<I> generate(List<Set<I>> transactionList, double minimumSupport) {
		
		checkValidationOfSupportMinimum(minimumSupport);
		if (transactionList.isEmpty()) {
			return null;
		}

		
		//Mappe chaque ensemble d'éléments à son nombre de supports.
		//Le nombre de supports est simplement le nombre de fois qu'un ensemble d'éléments apparaît dans la liste des transactions.
		Map<Set<I>, Integer> supportCountMap = new HashMap<>();

		// prendre la liste des 1-itemsets qui sont fréquents.
		List<Set<I>> frequentItemList = getFrequentItemsList(transactionList, supportCountMap, minimumSupport);

		// Mappe chaque « k » à la liste des ensembles d'éléments k fréquents.
		Map<Integer, List<Set<I>>> map = new HashMap<>();
		map.put(1, frequentItemList);
		System.out.println(" Apres dans map: "+map);

		// 'k' désigne la cardinalité des ensembles d'éléments traités à chaque itération de la boucle suivante.
		int k = 1;

		do {
			++k;
			System.out.println("k: "+k);
			// First generate the candidates.
			List<Set<I>> candidateList = generateCandidates(map.get(k - 1));
			System.out.println("candidateList: "+candidateList);
			System.out.println("transaction list :"+ transactionList);
			for (Set<I> transaction : transactionList) {
				List<Set<I>> candidateList2 = subset(candidateList, transaction);
				System.out.println("candidateList2: "+candidateList2);
				for (Set<I> itemset : candidateList2) {
					supportCountMap.put(itemset, supportCountMap.getOrDefault(itemset, 0) + 1);
					
				}
			}
			System.out.println("supportCountMap after merge:"+supportCountMap);

			map.put(k, getFrequentItemSetCandidates(candidateList, supportCountMap, minimumSupport, transactionList.size()));
			System.out.println("map: "+map);
			
		} while (!map.get(k).isEmpty());
		
		return new FrequentItems<>(allFrequentItemSets(map), supportCountMap, minimumSupport,
				transactionList.size());
	}

	
	
	// fonction permet de concatèner simplement toutes les listes de jeux d'éléments fréquents en une seule liste.
	//return la liste de tous les itemsets fréquents.
	public List<Set<I>> allFrequentItemSets(Map<Integer, List<Set<I>>> map) {
		List<Set<I>> ret = new ArrayList<>();

		for (List<Set<I>> itemsetList : map.values()) {
			ret.addAll(itemsetList);
		}
		System.out.println("ret: "+ret);

		return ret;
	}

	
	
	//fonction permet de rassembler tous les ensembles d'éléments fréquents candidats  dans une seule liste.
	//prend en parametre:
	//(candidateList) la liste des ensembles d'éléments candidats.
	//(supportCountMap) Map qui contient l ensemble d'éléments avec son nombre de supports.
	//(minimumSupport) le support minimum.
	//(transactions) le nombre total de transactions.
	//return une liste de candidats d'items fréquents.
	
	public List<Set<I>> getFrequentItemSetCandidates(List<Set<I>> candidateList, Map<Set<I>, Integer> supportCountMap,
			double minimumSupport, int transactions) {
		List<Set<I>> candidates = new ArrayList<>(candidateList.size());

		for (Set<I> itemset : candidateList) {
			if (supportCountMap.containsKey(itemset)) {
				int supportCount = supportCountMap.get(itemset);
				// double support = 1.0 * supportCount / transactions;
				double support = supportCount;

				if (support >= minimumSupport) {
					candidates.add(itemset);
				}
			}
		}

		return candidates;
	}

	
	//fonction permet de retourner list of itemssets dans candidate list qui sont  dans transaction list
	public List<Set<I>> subset(List<Set<I>> candidateL, Set<I> trans) {
		List<Set<I>> list = new ArrayList<>(candidateL.size());

		for (Set<I> candidate : candidateL) {
			if (trans.containsAll(candidate)) {
				list.add(candidate);
			}
		}
		System.out.println("subset: "+list);
		return list;
	}

	
	
	//fonction permet de Génèrer les candidats suivants. 
	//prend en parametre (itemsetListe) la liste des ensembles d'éléments source, chacun de taille k.
	//return la liste des candidats chacun de taille k+1.
	public List<Set<I>> generateCandidates(List<Set<I>> itemsetList) {
		System.out.println("--------------- from generateCandidate--");
		
		List<List<I>> list = new ArrayList<>(itemsetList.size());
		
		for (Set<I> itemset : itemsetList) {
			List<I> l = new ArrayList<>(itemset);
			System.out.println("List :"+l);
			Collections.<I>sort(l, ITEM_COMPARATOR);
			list.add(l);
			
		}
		System.out.println("list.add(l):"+list);
		int listSize = list.size();

		List<Set<I>> candidateList = new ArrayList<>(listSize);
		System.out.println("list to merge:"+list);
		for (int i = 0; i < listSize; ++i) {
			for (int j = i + 1; j < listSize; ++j) {
//				System.out.println("list.get(i):"+list.get(i));
//				System.out.println("list.get(j):"+list.get(j));
				
				Set<I> candidate = combinNewtItemSetCandidate(list.get(i), list.get(j));

				if (candidate != null) {
					candidateList.add(candidate);
				}
			}
		}
		System.out.println("candidateList: " +candidateList);
		System.out.println("--------------- fin  generateCandidate--");
		return candidateList;
	}

	
	//fonction permet de construire le prochain itemset candidat.
	//prend en parametre:
	//itemset1 la liste des éléments du premier itemset.
	//itemset2 la liste des éléments dans le deuxième itemset.
	
	
	//return un ensemble d'éléments candidat fusionné ou  null
	//s'il ne peut pas être construit à partir des ensembles d'éléments d'entrée.
	public Set<I> combinNewtItemSetCandidate(List<I> itemset1, List<I> itemset2) {
		int length = itemset1.size();
		//System.out.println("length: "+length);
		for (int i = 0; i < length - 1; ++i) {
			if (!itemset1.get(i).equals(itemset2.get(i))) {
				return null;
			}
		}

		if (itemset1.get(length - 1).equals(itemset2.get(length - 1))) {
			return null;
		}

		Set<I> combinItemSet = new HashSet<>(length + 1);

		for (int i = 0; i < length - 1; ++i) {
			combinItemSet.add(itemset1.get(i));
		}

		combinItemSet.add(itemset1.get(length - 1));
		combinItemSet.add(itemset2.get(length - 1));
		//System.out.println(combinItemSet);
		return combinItemSet;
	}

	private static final Comparator ITEM_COMPARATOR = new Comparator() {

		public int compare(Object o1, Object o2) {
			return ((Comparable) o1).compareTo(o2);
		}

	};

	
	
	//fonction permet de Calculer les itemsets fréquents de taille 1.
	//prend en parametre (transactionList) c'est la base de données complète des transactions.
	//Aussi (supportCountM) Map dans la quelle on va ecrire le support de chaque élément.
	//(minSup)  le support minimum .
	//return la liste la liste des ensembles d'éléments 1-fréquents.
	
	public List<Set<I>> getFrequentItemsList(List<Set<I>> transactionList, Map<Set<I>, Integer> supportCountM,double minSup) {
		Map<I, Integer> map = new HashMap<>();

		// Count the support counts of each item. EXP: {A=2, B=4, C=2, D=1, E=3}
		for (Set<I> itemset : transactionList) {
			for (I item : itemset) {
				Set<I> tmp = new HashSet<>(1);
				
				tmp.add(item);

				if (supportCountM.containsKey(tmp)) {
					supportCountM.put(tmp, supportCountM.get(tmp) + 1);
				} else {
					supportCountM.put(tmp, 1);
				}
				map.put(item, map.getOrDefault(item, 0) + 1);
				
				
			}
		}
		System.out.println("supportCountM: "+supportCountM);
		System.out.println("Map: "+map);
		// Fin Count the support counts of each item. 

		
		//get frenquent Itemset that are greater than minimum support
		List<Set<I>> frequentItemsetList = new ArrayList<>();

		for (Map.Entry<I, Integer> entry : map.entrySet()) {
			// if (1.0 * entry.getValue() / map.size() >= minimumSupport) {
			if (entry.getValue() >= minSup) {
				Set<I> itemset = new HashSet<>(1);
				itemset.add(entry.getKey());
				frequentItemsetList.add(itemset);
			}
		}
		
		System.out.println("frequentItemsetList:"+frequentItemsetList);

		return frequentItemsetList;
	}
	
	
	
	public void checkFileIsEmbty(File file) throws Exception {
		
        if (file.length() == 0)
            throw new Exception("File is empty!!!");
        
	}
	

	
	//fonction permet de verifier si le support minimum est valide (not null et supperieur a 0)
	public void checkValidationOfSupportMinimum(double support) {
		if (Double.isNaN(support)) {
			throw new IllegalArgumentException("The input support is null.");
		}

		if (support < 0.0) {
			throw new IllegalArgumentException(
					"The input support is too small: " + support + ", " + "should be at least 0.0");
		}
	}
}
