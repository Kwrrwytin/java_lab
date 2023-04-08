package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.*;
import hust.cs.javacourse.search.util.Config;

import java.io.*;
import java.util.*;

/**
 * 测试索引构建
 */
public class TestBuildIndex {
    /**
     *  索引构建程序入口
     * @param args : 命令行参数
     */
    public static void main(String[] args){
        System.out.println("--------创建倒排索引，创建模式--------");
        System.out.println("\t1. 从文本文档目录读取文档内容进行创建");
        System.out.println("\t2. 从已有的序列化索引文件反序列化进行创建");
        System.out.println("\t3. 退出程序");
        System.out.println("----------------------------------");
        System.out.println("请输入数字：");
        Scanner scan = new Scanner(System.in);
        int opt = scan.nextInt();
        AbstractIndex index;
        switch (opt){
            case 1:
                AbstractIndexBuilder indexBuilder = new IndexBuilder(new DocumentBuilder());
                index = indexBuilder.buildIndex(Config.DOC_DIR);
                if (index.getDictionary().isEmpty()){
                    System.out.println("Warning: 索引表为空！");
                }
                else{
                    System.out.println("-------------------------------------------------------");
                    System.out.print("Dictionary: ");
                    for(AbstractTerm term: index.getDictionary()){
                        System.out.print(term.getContent()+" ");
                    }
                }
                System.out.println("\n----------------------DocPath：------------------------");
                System.out.println(Config.DOC_DIR);
                System.out.println("---------------------PostingList：---------------------");
                System.out.println(index.toString());
                break;
            case 2:
                index=new Index();
                index.load(new File(Config.INDEX_DIR+"index.data"));
                System.out.println("倒排索引内容如下：");
                System.out.println(index.toString());
                break;
            default:
                System.out.println("输入格式错误!");
                break;
        }
    }
}
