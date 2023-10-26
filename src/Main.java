public class Main {
    public static void main(String[] args) {
        String pathToWordsFile = "";
        String pathToMistakesFile = "";
        EnglishHelper englishHelper = new EnglishHelper(pathToWordsFile, pathToMistakesFile, true);
        englishHelper.start();
    }
}