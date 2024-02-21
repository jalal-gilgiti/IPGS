/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lsh;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author toshiba
 */
public class LSH implements Callable<int[]> {
    public int numSuperNodes, numNodes, numHashTables, numHashFunctions;
    public IntArrayList superList;
    public IntArrayList[] insideSupernode;
    public long[] edges;
    public List<int[]> hashTables;

    public LSH(int numSuperNodes, int numNodes, int numHashTables, int numHashFunctions, IntArrayList superList, IntArrayList[] insideSupernode, long[] edges, List<int[]> hashTables){
        this.numSuperNodes = numSuperNodes;
        this.numNodes = numNodes;
        this.numHashTables = numHashTables;
        this.numHashFunctions = numHashFunctions;
        this.superList = superList;
        this.insideSupernode = insideSupernode;
        this.edges = edges;
        this.hashTables = hashTables;
    }

    // Generate random hash functions
    private List<HashFunction> generateHashFunctions() {
        List<HashFunction> hashFunctions = new ArrayList<>();
        for (int i = 0; i < numHashFunctions; i++) {
            hashFunctions.add(new HashFunction());
        }
        return hashFunctions;
    }

    // Shingle calculation
    @Override
    public int[] call() throws Exception {
        int[] superAns = new int[numSuperNodes];

        // Generate hash functions
        List<HashFunction> hashFunctions = generateHashFunctions();

        // Hash nodes to buckets
        for (int[] hashTable : hashTables) {            //It iterates through each hash table in the array of hash tables using a for-each loop.
            Arrays.fill(hashTable, -1);  //It fills each hash table with -1, which indicates that no item has been hashed yet.
            for (int i = 0; i < numNodes; i++) { //For each item in the set, the code generates an array of hashes using multiple hash functions.
  //  Here, numHashFunctions is the number of hash functions to be used, and hashFunctions is a list of hash functions. 
  //The hash function used to generate the j-th hash value for the i-th item is obtained from hashFunctions.get(j).
  //The hash method of the hash function is called with the item index i as the argument to generate the hash value.            
                
                int[] hashes = new int[numHashFunctions];
                for (int j = 0; j < numHashFunctions; j++) {
                    hashes[j] = hashFunctions.get(j).hash(i);
                }
                
                //The code then calculates the bucket in the hash table 
//to which the item should be added based on the hash 
//values generated in the previous step. 
//This is done by taking the modulo of the 
//hash value array with the length of the hash table array.

                int bucket = Arrays.hashCode(hashes) % hashTable.length;


//Finally, the code checks if the bucket is empty or if the minimum hash value for the item is less 
//than the minimum hash value already stored in the bucket. If either of these conditions is true, 
//the minimum hash value for the item is stored in the bucket.

                if (hashTable[bucket] == -1 || hashes[0] < hashTable[bucket]) {
                    hashTable[bucket] = i;
                }
            }
        }

        // Find minimum hash values for each supernode
        for (int i = 0; i < numSuperNodes; i++) {
            int supernode = superList.getInt(i);
            superAns[i] = Integer.MAX_VALUE;
            for (int j : insideSupernode[supernode]) {
                for (int[] hashTable : hashTables) {
                    int bucket = Arrays.hashCode(hashFunctions.stream().mapToInt(h -> h.hash(j)).toArray()) % hashTable.length;
                    int hashValue = hashTable[bucket];
                    if (hashValue != -1 && hashValue < superAns[i]) {
                        superAns[i] = hashValue;
                    }
                }
            }
        }

        return superAns;
    }
    
    
    private class HashFunction {
    private final int a;
    private final int b;
    private final int p;

    public HashFunction() {
        this.a = ThreadLocalRandom.current().nextInt();
        this.b = ThreadLocalRandom.current().nextInt();
        this.p = BigInteger.valueOf(2).pow(31).subtract(BigInteger.ONE).intValue(); ///prime number generation 2 power 31-1
    }

    public int hash(int x) {
        return Math.floorMod(a * x + b, p); // hash fuction h(1) = a*x+b mode p
    }

    //it checks if the values of a, b, and p of the two objects are equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashFunction that = (HashFunction) o;
        return a == that.a &&
                b == that.b &&
                p == that.p;
    }
    
    //the Objects.hash method to compute the hash code based on the values of a, b, and p.

    @Override
    public int hashCode() {
        return Objects.hash(a, b, p);
    }
}
}

