package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.*;
import java.util.*;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load((new File(indexFile)));
        this.index.optimize();
    }
    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postingList= index.search(queryTerm);
        if(postingList==null) return null;
        List<AbstractHit> hitArr=new ArrayList<>();
        for(int i=0;i<postingList.size();i++){
            AbstractPosting posting= postingList.get(i);
            String docPath=this.index.getDocName(posting.getDocId());
            Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
            map.put(queryTerm,posting);
            AbstractHit hit=new Hit(posting.getDocId(),docPath,map);
            hit.setScore((sorter.score(hit)));
            hitArr.add(hit);
        }
        sorter.sort(hitArr);
        return (AbstractHit[]) hitArr.toArray(new AbstractHit[0]);
    }
    /**
     * 根据两个相邻检索词进行搜索
     *
     * @param queryTerm1 ：第一个检索词
     * @param queryTerm2 ：第二个检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2,Sort sorter) {
        //先获取两个term的postinglist
        AbstractPostingList postingList1=this.index.search(queryTerm1);
        AbstractPostingList postingList2=this.index.search(queryTerm2);
        ArrayList<AbstractHit> hitArrayList=new ArrayList<>();
        int i=0,j=0;
        int size1=0,size2=0;
        if(postingList1!=null) size1=postingList1.size();
        if(postingList2!=null) size2=postingList2.size();
        while(i<size1&&j<size2){

            AbstractPosting posting1= postingList1.get(i);
            AbstractPosting posting2= postingList2.get(j);

            if(posting1.getDocId()==posting2.getDocId()) {//说明两个搜索词指向的文件是同一个文件
                //System.out.println(posting1.getDocId()+" "+posting2.getDocId());
                int pos1 = 0, pos2 = 0;
                int cur1 = -1, cur2 = -1;
                while (pos1 < posting1.getPositions().size() && pos2 < posting2.getPositions().size()) {
                    //System.out.println(pos1+" "+pos2);
                    cur1 = posting1.getPositions().get(pos1);
                    cur2 = posting2.getPositions().get(pos2);
                    //System.out.println(cur1+" "+cur2);
                    if (cur1 + 1 == cur2 || cur1 - 1 == cur2) {
                        String docPath = this.index.getDocName(posting1.getDocId());
                        Map<AbstractTerm, AbstractPosting> map = new HashMap<>();
                        map.put(queryTerm1, posting1);
                        map.put(queryTerm2, posting2);
                        AbstractHit hit = new Hit(posting1.getDocId(), docPath, map);
                        hit.setScore(sorter.score(hit));
                        hitArrayList.add(hit);
                        break;
                    } else if (cur1 > cur2) {
                        pos2++;
                    } else pos1++;
                }
                i++;j++;
            }
            else if (posting1.getDocId()<posting2.getDocId()){
                i++;
            }else{  // posting1.getDocId> posting2.getDocId()
                j++;
            }
        }
        sorter.sort(hitArrayList);
        return (AbstractHit[]) hitArrayList.toArray(new Hit[0]);
    }
    /**
     *
     * 根据二个检索词进行搜索
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter ：    排序器
     * @param combine ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        //先获取两个term的postinglist
        AbstractPostingList postingList1=this.index.search(queryTerm1);
        AbstractPostingList postingList2=this.index.search(queryTerm2);
        ArrayList<AbstractHit> hitArrayList=new ArrayList<>();
        int i=0,j=0;
        int size1=0,size2=0;
        if(postingList1!=null) size1=postingList1.size();
        if(postingList2!=null) size2=postingList2.size();

        while(i<size1&&j<size2){

            AbstractPosting posting1= postingList1.get(i);
            AbstractPosting posting2= postingList2.get(j);

            if(posting1.getDocId()==posting2.getDocId()){//说明两个搜索词指向的文件是同一个文件
                String docPath=this.index.getDocName(posting1.getDocId());
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm1,posting1);
                map.put(queryTerm2,posting2);
                AbstractHit hit=new Hit(posting1.getDocId(),docPath,map);
                hit.setScore(sorter.score(hit));
                hitArrayList.add(hit);
                i++;j++;
            }else if (posting1.getDocId()<posting2.getDocId()){
                if(combine==LogicalCombination.OR){
                    String docPath=this.index.getDocName(posting1.getDocId());
                    Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                    map.put(queryTerm1,posting1);
                    AbstractHit hit=new Hit(posting1.getDocId(),docPath,map);
                    hit.setScore(sorter.score(hit));
                    hitArrayList.add(hit);
                }
                i++;
            }else{  // posting1.getDocId> posting2.getDocId()
                if(combine==LogicalCombination.OR){
                    String docPath=this.index.getDocName(posting2.getDocId());
                    Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                    map.put(queryTerm2,posting2);
                    AbstractHit hit=new Hit(posting2.getDocId(),docPath,map);
                    hit.setScore(sorter.score(hit));
                    hitArrayList.add(hit);
                }
                j++;
            }
        }
        if(combine==LogicalCombination.OR){ //读取剩余的命中结果
            while(i<size1){
                AbstractPosting posting1=postingList1.get(i);
                String docPath=this.index.getDocName(posting1.getDocId());
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm1,posting1);
                AbstractHit hit=new Hit(posting1.getDocId(),docPath,map);
                hit.setScore(sorter.score(hit));
                hitArrayList.add(hit);
                i++;
            }
            while(j<size2){
                AbstractPosting posting2=postingList2.get(j);
                String docPath=this.index.getDocName(posting2.getDocId());
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm2,posting2);
                AbstractHit hit=new Hit(posting2.getDocId(),docPath,map);
                hit.setScore(sorter.score(hit));
                hitArrayList.add(hit);
                j++;
            }
        }
        sorter.sort(hitArrayList);
        return (AbstractHit[]) hitArrayList.toArray(new Hit[0]);
    }
}
