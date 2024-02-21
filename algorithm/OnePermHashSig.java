/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algorithm;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.concurrent.Callable;

public class OnePermHashSig implements Callable<int[]>{
    private final int numSubnodes;
    private final int numSupernodes;
    public IntArrayList[] insideSupernode;
    public IntArrayList snList;
    private final int[][] edges;
    private final OnePermHashSig onePermHashSig;

    public OnePermHashSig(int numSupernodes, int numSubnodes, IntArrayList snList, IntArrayList[] insideSupernode, int[][] edges, OnePermHashSig onePermHashSig){
        this.numSupernodes = numSupernodes;
        this.numSubnodes = numSubnodes;
        this.snList = snList;
        this.insideSupernode = insideSupernode;
        this.edges = edges;
        this.onePermHashSig = onePermHashSig;
    }

    public OnePermHashSig(int numEdges) {
        onePermHashSig = new OnePermHashSig(numEdges);
        this.numSubnodes = 1;
        this.numSupernodes = 1;
        this.snList = new IntArrayList(1);
        this.insideSupernode = new IntArrayList[0];
        this.edges = new int[][]{{0,0}};
    }

    @Override
    public int[] call() throws Exception {
        int[] signature = new int[numSupernodes];
        for(int i=0; i<numSupernodes; i++){
            int[][] adjMatrix = new int[numSubnodes][numSubnodes];
            int[] nodeMap = insideSupernode[i].toArray(signature);
            for(int j=0; j<numSubnodes; j++){
                for(int k=0; k<numSubnodes; k++){
                    if(nodeMap[j] != -1 && nodeMap[k] != -1){
                        adjMatrix[j][k] = edges[nodeMap[j]][nodeMap[k]];
                    }
                }
            }
            signature[i] = onePermHashSig.getSignature(adjMatrix);
        }
        return signature;
    }

    private int getSignature(int[][] adjMatrix){
        int signature = 0;
        for(int i=0; i<numSubnodes; i++){
            int[] row = adjMatrix[i];
            int minHash = row[0];
            for(int j=1; j<numSubnodes; j++){
                if(row[j] < minHash){
                    minHash = row[j];
                }
            }
            signature = signature * 31 + minHash;
        }
        return signature;
    }
}
