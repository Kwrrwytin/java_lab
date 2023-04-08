package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.*;

import java.io.*;
import java.util.*;

import static hust.cs.javacourse.search.util.Config.IGNORE_CASE;

public class TermTupleScanner extends AbstractTermTupleScanner {
    private Queue<AbstractTermTuple> tuples=new LinkedList<>();
    private int pos;
    private StringSplitter splitter;

    public TermTupleScanner(){}
    public TermTupleScanner(BufferedReader bufferedReader){
        super(bufferedReader);
        splitter=new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        pos=0;
    }
    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        if(tuples.size()==0){
            try{
                String str= input.readLine();
                while(str!=null&&str.equals("")){//跳过文件开头的空行
                    str=input.readLine();
                }
                if(str!=null){
                    List<String> words= splitter.splitByRegex(str);//将读到的句子进行切分
                    for(String word:words){
                        AbstractTermTuple tuple=new TermTuple();
                        tuple.curPos=pos++;
                        word=word.toLowerCase(Locale.ROOT);
                        tuple.term=new Term(word);
                        tuples.add(tuple);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return tuples.poll();
    }
}
