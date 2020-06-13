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
import java.util.regex.Pattern;

public class WordByLength {
    public static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("WordByLength").setMaster("local[4]");
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

        JavaPairRDD<Integer, String> ones = words.mapToPair(
                new PairFunction<String, Integer, String>() {
                    public Tuple2<Integer, String> call(String s) throws Exception {
                        return new Tuple2<Integer, String>(s.length(), s);
                    }
                });

        JavaPairRDD<Integer, String> counts = ones.reduceByKey(
                new Function2<String, String, String>() {
                    public String call(String s1, String s2) throws Exception {
                        return s1 + ", " +s2;
                    }
                });

        List<Tuple2<Integer, String>> output = counts.sortByKey().collect();
        for(Tuple2<?, ?> tuple: output){
            System.out.println(tuple._1() + ": " + tuple._2());
        }
    }
}
