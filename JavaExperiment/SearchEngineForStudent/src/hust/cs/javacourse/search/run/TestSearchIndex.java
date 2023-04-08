package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.*;
import java.util.*;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args) throws IOException {
        IndexSearcher searcher = new IndexSearcher();
        searcher.open(Config.INDEX_DIR + "index.data");
        Sort freqSorter = new SimpleSorter();
        //查询单词
        String req;
        System.out.println("---------------倒排索引查询，输入格式---------------");
        System.out.println("\t1. oneWord");
        System.out.println("\t2. word combine word");
        System.out.println("combine :\n\tor: +,|\n\tand: &,*");
        System.out.println("3. firstWord secondWord");
        System.out.println("4. 输入quitSearch退出查询");
        System.out.print("请输入需要查询的单词: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while((req=br.readLine())!=null && !req.equals("quitSearch")){
            String[] reqs=req.split("[\\s]+");//?
            List<String> stopWords=new ArrayList<String>(Arrays.asList(StopWords.STOP_WORDS));
            for(String s:reqs){
                if(stopWords.contains(s)){
                    System.out.println("\033[31mWarning:停用词:"+s+"\033[0m");
                }
            }
            if(req.length()<1){
                System.out.println("请输入一个单词");
            }
            else if(reqs.length==1){
                AbstractHit[] hits=searcher.search(new Term(req),freqSorter);
                System.out.println("-----------------------单个单词-------------------------");
                if(hits==null) System.out.println("未搜索到任何结果");
                else {

                    for(int i=0;i< hits.length;i++){
                        System.out.println("DocId:"+hits[i].getDocId()+" DocPath:"+hits[i].getDocPath());
                    }
                    System.out.println("-------------------------------------------------------");
                    for(int i= 0;i< hits.length;i++){
                        System.out.println(hits[i].toString());
                    }
                    System.out.print("请输入需要查询的单词: ");
                }
            }
            else if(reqs.length==2){
                AbstractHit[] hits=searcher.search(new Term(reqs[0]),new Term(reqs[1]),freqSorter);
                System.out.println("-----------------------相邻单词------------------------");
                if(hits==null) System.out.println("未搜索到任何结果");
                else {

                    for(int i=0;i< hits.length;i++){
                        System.out.println("DocId:"+hits[i].getDocId()+" DocPath:"+hits[i].getDocPath());
                    }
                    System.out.println("-------------------------------------------------------");
                    for(int i= 0;i< hits.length;i++){
                        System.out.println(hits[i].toString());
                    }
                    System.out.print("请输入需要查询的单词: ");
                }
            }
            else if(reqs.length==3){
                if(reqs[1].equals("&") || reqs[1].equals("*")){     // 与
                    AbstractHit[] hits = searcher.search(new Term(reqs[0]), new Term(reqs[2]),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.ADN);
                    System.out.println("-----------------------单词组合-------------------------");
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else{

                        for(int i=0;i< hits.length;i++){
                            System.out.println("DocId:"+hits[i].getDocId()+" DocPath:"+hits[i].getDocPath());
                        }
                        System.out.println("-------------------------------------------------------");
                        for(int i= 0;i< hits.length;i++){
                            System.out.println(hits[i].toString());
                        }
                    }

                } else if(reqs[1].equals("|") || reqs[1].equals("+")){  // 或
                    AbstractHit[] hits = searcher.search(new Term(reqs[0]), new Term(reqs[2]),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.OR);
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else{
                        System.out.println("-------------------------------------------------------");
                        for(int i=0;i< hits.length;i++){
                            System.out.println("DocId:"+hits[i].getDocId()+" DocPath:"+hits[i].getDocPath());
                        }
                        System.out.println("-------------------------------------------------------");
                        for(int i= 0;i< hits.length;i++){
                            System.out.println(hits[i].toString());
                        }
                    }

                } else {
                    System.out.println("\033[31m逻辑关系解析失败\033[0m");
                    System.out.println("输入格式： word combine word");
                    System.out.println("combine :   or: +,|  and: &,*");
                }
                System.out.print("请输入需要查询的单词: ");
            }
            else {
                System.out.println("输入单词数过多");
                System.out.print("请输入需要查询的单词: ");
            }
        }
    }
}
