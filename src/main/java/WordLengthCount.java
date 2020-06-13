import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WordLengthCount {
    public static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("WordLengthCount").setMaster("local[4]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        String file1 = WordCount.class.getClassLoader().getResource("file4.txt").getPath();
        JavaRDD<String> textFile = sc.textFile(file1);

        String file2 = WordCount.class.getClassLoader().getResource("file5.txt").getPath();
        JavaRDD<String> textFile2 = sc.textFile(file2);

        JavaRDD<String> words = textFile.union(textFile2).flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                return Arrays.asList(SPACE.split(s)).iterator();
            }
        });

        JavaPairRDD<Integer, Integer> ones = words.mapToPair(
                new PairFunction<String, Integer, Integer>() {
                    public Tuple2<Integer, Integer> call(String s) throws Exception {
                        return new Tuple2<Integer, Integer>(s.length(), 1);
                    }
                });

        JavaPairRDD<Integer, Integer> counts = ones.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    public Integer call(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                });

        List<Tuple2<Integer, Integer>> output = counts.sortByKey().collect();
        for(Tuple2<?, ?> tuple: output){
            System.out.println(tuple._1() + ": " + tuple._2());
        }
    }
}
