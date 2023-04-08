package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.util.*;
import java.io.*;

public class IndexBuilder extends AbstractIndexBuilder {
    private  int docNum;

    public IndexBuilder(AbstractDocumentBuilder documentBuilder){
        super(documentBuilder);
        this.docNum=1;
    }


    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index=new Index();
        int len=FileUtil.list(rootDirectory).size();
        for(String path: FileUtil.list(rootDirectory)){
            AbstractDocument document=this.docBuilder.build(len-(this.docNum++),path,new File(path));
            index.addDocument(document);
        }
        File file=new File(Config.INDEX_DIR+"index.data");
        if(!file.exists()){
            try{
                if(!file.createNewFile()) throw  new IOException();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        index.save(file);
        return index;
    }
}
