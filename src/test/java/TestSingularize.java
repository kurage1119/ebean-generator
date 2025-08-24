import net.kurage.generator.ModelGenerator;
import net.kurage.generator.EnglishSingularizer;

public class TestSingularize {
    public static void main(String[] args) {
        // テストケース
        String[] testCases = {
            "expenses",      
            "responses", 
            "courses",
            "classes",       
            "addresses",
            "users",         
            "categories",    
            "companies",
            "countries",
            "boxes",         
            "dishes",        
            "watches",       
            "buzzes",        
            "children",      
            "people",
            "men",
            "women",
            "sheep",         
            "indices",       
            "matrices",
            "analyses",
            "criteria",
            "phenomena"
        };
        
        System.out.println("=== EnglishSingularizer 直接テスト ===");
        for (String testCase : testCases) {
            String singular = EnglishSingularizer.singularize(testCase);
            System.out.println(testCase + " -> " + singular);
        }
        
        System.out.println();
        System.out.println("=== ModelGenerator クラス名生成テスト ===");
        for (String testCase : testCases) {
            String className = ModelGenerator.toClassName(testCase);
            System.out.println(testCase + " -> " + className + "Gen");
        }
    }
}
