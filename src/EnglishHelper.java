import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class EnglishHelper {
    class FileNotInCorrectFormat extends Exception {
        public FileNotInCorrectFormat(String str, int rowPosition, int characterPosition) {
            super(str + " (" + rowPosition + " row, " + characterPosition + " column)");
        }

        public FileNotInCorrectFormat(String str) {
            super(str);
        }

    }

    class FileIsEmptyException extends Exception {
        public FileIsEmptyException() {
            super("Specified file is empty");
        }

        public FileIsEmptyException(String str) {
            super(str);
        }
    }


    private final String pathToWordsFile;
    private final String pathToMistakesFile;

    private final boolean considerMistakes;
    private Map<String, String> dictionary = new HashMap<>(50);
    private HashMap<String, String> mistakesDictionary = new HashMap<>(10);

    private List<String> wordsList;

    public EnglishHelper(String pathToWordsFile, String pathToMistakesFile, boolean considerMistakes) {
        this.pathToWordsFile = pathToWordsFile;
        this.pathToMistakesFile = pathToMistakesFile;
        this.considerMistakes = considerMistakes;
    }

    public EnglishHelper(String pathToWordsFile, String pathToMistakesFile) {
        this.pathToWordsFile = pathToWordsFile;
        this.pathToMistakesFile = pathToMistakesFile;
        this.considerMistakes = false;
    }

    /**
     * Method returns a dictionary from specified file in special format
     * @param pathToWordsFile path to file
     * @return dictionary from specified file
     * @throws IOException
     * @throws FileNotInCorrectFormat
     * @throws FileIsEmptyException
     */

     private HashMap<String, String> returnDictionary(String pathToWordsFile) throws IOException,
             FileNotInCorrectFormat, FileIsEmptyException {
         HashMap<String, String> dictionary = new HashMap<>(50);

         try (final FileReader fileReader = new FileReader(pathToWordsFile)) {
             int c = fileReader.read();
             if(c == -1) {
                 return dictionary;
             }

             int rowPosition = 1;

             while(c != -1) {
                 StringBuffer word = new StringBuffer();
                 StringBuffer translation = new StringBuffer();
                 int characterPosition = 1;

                 while((char) c != ';') {
                     if((char) c == '\n') {
                         throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                                 "between words and translations", rowPosition, characterPosition);
                     }
                     if(characterPosition > 30) {
                         // //File is not in correct format: it hasn't the delimiter ';' " +
                         //        //                "between words and translations: " + rowPosition + " row, "  + character
                         throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                                 "between words and translations", rowPosition, characterPosition);
                     }

                     word.append((char) c);
                     c = fileReader.read();
                     characterPosition++;
                 }
                 c = fileReader.read();

                 if((char) c == '\n') {
                     throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                             "between words and translations", rowPosition, characterPosition);
                 }

                 while((char) c != '\n' || !Character.isWhitespace(c)) {
                     if(characterPosition > 60) {
                         throw new FileNotInCorrectFormat("File is not in correct format: it hasn't new lines after " +
                                 "each pair", rowPosition, characterPosition);
                     }
                     translation.append((char) c);
                     c = fileReader.read();
                     characterPosition++;
                 }
                 dictionary.put(word.toString(), translation.toString());
                 c = fileReader.read();
                 rowPosition++;
             }
         }
         return dictionary;
     }

    private HashMap<String, String> returnDictionary(String pathToWordsFile, List<String> wordsList) throws IOException,
            FileNotInCorrectFormat,
            FileIsEmptyException {
        HashMap<String, String> dictionary = new HashMap<>(50);

        wordsList = new ArrayList<>(50);

        try (final FileReader fileReader = new FileReader(pathToWordsFile)) {
            int c = fileReader.read();
            if(c == -1) {
                throw new FileIsEmptyException();
            }

            int rowPosition = 1;

            while(c != -1) {
                StringBuffer word = new StringBuffer();
                StringBuffer translation = new StringBuffer();
                int characterPosition = 1;

                while((char) c != ';') {
                    if((char) c == '\n') {
                        throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                                "between words and translations", rowPosition, characterPosition);
                    }
                    if(characterPosition > 30) {
                        // //File is not in correct format: it hasn't the delimiter ';' " +
                        //        //                "between words and translations: " + rowPosition + " row, "  + characterPosition + " character"
                        throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                                "between words and translations", rowPosition, characterPosition);
                    }

                    word.append((char) c);
                    c = fileReader.read();
                    characterPosition++;
                }
                c = fileReader.read();

                if((char) c == '\n') {
                    throw new FileNotInCorrectFormat("File is not in correct format: it hasn't the delimiter ';' " +
                            "between words and translations", rowPosition, characterPosition);
                }

                while((char) c != '\n' && !Character.isWhitespace(c)) {
                    if(characterPosition > 60) {
                        throw new FileNotInCorrectFormat("File is not in correct format: it hasn't new lines after " +
                                "each pair", rowPosition, characterPosition);
                    }
                    translation.append((char) c);
                    c = fileReader.read();
                    characterPosition++;
                }
                wordsList.add(word.toString());
                dictionary.put(word.toString(), translation.toString());
                c = fileReader.read();
                rowPosition++;
            }
        }

        this.wordsList = wordsList;
        return dictionary;
    }


    private void printDictionary(Map<String, String> dictionary) {
        for (Map.Entry<String, String> mapEntry : dictionary.entrySet()) {
            System.out.println(mapEntry.getKey() + ": " + mapEntry.getValue());
        }
    }

    public void printMistakeDictionary() {
        printDictionary(mistakesDictionary);
    }

    public void printWordsDictionary() {
        printDictionary(dictionary);
    }


    public void clearMistakes() {
        try(final FileWriter fileWriter = new FileWriter(pathToMistakesFile)) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteMistakeFromFile(String mistake, String pathToMistakesFile) {
        StringBuffer fileStrings = new StringBuffer();

        try(Scanner scanner = new Scanner(new FileReader(pathToMistakesFile))) {
            String str = scanner.nextLine();
            int counter = 0;

            while(scanner.hasNext()) {
                String strBeforeSemicolon = str.split(";")[0];
                if(!strBeforeSemicolon.equals(mistake)) {
                    fileStrings.append(str + "\n");
                }
                str = scanner.nextLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

         try (FileWriter fileWriter = new FileWriter(pathToMistakesFile)) {
             fileWriter.write(fileStrings.toString());
         } catch (IOException ex) {
             ex.printStackTrace();
         }
    }

    private void writeMistakeIntoFile(String mistake, String pathToMistakesFile) {
        try(final FileWriter fileWriter = new FileWriter(pathToMistakesFile, true)) {
            fileWriter.append(mistake);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void start() {
        int correctGuessCounter = 0;
        System.out.println("English Helper v. 0.8 has been started");

        try {
            dictionary = returnDictionary(pathToWordsFile, wordsList);
            mistakesDictionary = returnDictionary(pathToMistakesFile);
        } catch (IOException | FileIsEmptyException | FileNotInCorrectFormat ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println("Size of dictionary: " + wordsList.size() + " words");
        System.out.println("Type \"-\" or press \"Enter\" to get the translation immediately");

        List<String> mistakesList = new ArrayList<>();


        if(!mistakesDictionary.isEmpty()) {
            for(Map.Entry<String, String> entry : mistakesDictionary.entrySet()) {
                String mistake = entry.getKey();

                wordsList.add(mistake);
                mistakesList.add(mistake);
            }
        }


        for (int i = 0; i < wordsList.size(); i++) {
            System.out.println("_________________");
            if(correctGuessCounter > 0) {
                System.out.print("Correct words: " + correctGuessCounter + " from " + i + ". ");
                System.out.printf("General percent: %.1f%s", ((double) correctGuessCounter / i * 100), "%\n");
            }

            int randomIndex = new Random().nextInt(wordsList.size());
            String randomWord = wordsList.get(randomIndex);
            String answer = dictionary.get(randomWord);
            String mistakeWord = "";

            Scanner scanner = new Scanner(System.in);
            System.out.println("https://dictionary.cambridge.org/ru/%D1%81%D0%BB%D0%BE%D0%B2%D0%B0%D1%80%D1%8C/%D0%B0%D0%BD%D0%B3%D0%BB%D0%BE-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9/" + randomWord);
            System.out.println("Word: " + randomWord);
            while(true) {
                System.out.print("Translation: ");

                String response = scanner.nextLine();
                if(response.equals("-") || response.isEmpty() || response.isBlank()) {
                    System.out.println("Translated as: " + answer);
                    if(considerMistakes) {
                        writeMistakeIntoFile(randomWord + ";" + answer + "\n", pathToMistakesFile);
                        mistakeWord = randomWord;
                        mistakesList.add(mistakeWord);
                    }
                    break;
                }
                else if (response.equalsIgnoreCase(answer)) {
                    if(mistakesList.contains(randomWord)) {
                        deleteMistakeFromFile(randomWord, pathToMistakesFile);
                        mistakesList.remove(randomWord);
                        System.out.println("Nice work. Mistake has been corrected!");
                    }
                    else {
                        System.out.println("Good job! Correctly");
                    }
                    correctGuessCounter++;
                    break;
                } else {
                    if(considerMistakes) {
                        writeMistakeIntoFile(randomWord + ";" + answer + "\n", pathToMistakesFile);
                        mistakeWord = randomWord;
                        mistakesList.add(mistakeWord);
                    }
                    System.out.print("Incorrect :( \nContinue with a next word? (yes/anything to try again): ");
                    response = scanner.nextLine();

                    List<String> possibleAnswers = List.of("yes", "y", "да");
                    if (possibleAnswers.contains(response.toLowerCase())) {
                        System.out.println("Translated as: " + answer);
                        break;
                    }
                }
            }
            if(!mistakeWord.isEmpty()) {
                wordsList.add(mistakeWord);
            }
            else {
                wordsList.remove(randomWord);
            }
        }
    }

    public void start(boolean clearMistakes) {
         if(clearMistakes) {
             clearMistakes();
         }

        int correctGuessCounter = 0;
        System.out.println("English Helper v. 0.8 has been started");

        try {
            dictionary = returnDictionary(pathToWordsFile, wordsList);
            mistakesDictionary = returnDictionary(pathToMistakesFile);
        } catch (IOException | FileIsEmptyException | FileNotInCorrectFormat ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println("Size of dictionary: " + wordsList.size() + " words");
        System.out.println("Type \"-\" or press \"Enter\" to get the translation immediately");

        List<String> mistakesList = new ArrayList<>();


        if(!mistakesDictionary.isEmpty()) {
            for(Map.Entry<String, String> entry : mistakesDictionary.entrySet()) {
                String mistake = entry.getKey();

                wordsList.add(mistake);
                mistakesList.add(mistake);
            }
        }


        for (int i = 0; i < wordsList.size(); i++) {
            System.out.println("_________________");
            if(correctGuessCounter > 0) {
                System.out.print("Correct words: " + correctGuessCounter + " from " + i + ". ");
                System.out.printf("General percent: %.1f%s", ((double) correctGuessCounter / i * 100), "%\n");
            }

            int randomIndex = new Random().nextInt(wordsList.size());
            String randomWord = wordsList.get(randomIndex);
            String answer = dictionary.get(randomWord);
            String mistakeWord = "";

            Scanner scanner = new Scanner(System.in);
            System.out.println("https://dictionary.cambridge.org/ru/%D1%81%D0%BB%D0%BE%D0%B2%D0%B0%D1%80%D1%8C/%D0%B0%D0%BD%D0%B3%D0%BB%D0%BE-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9/" + randomWord);
            System.out.println("Word: " + randomWord);
            while(true) {
                System.out.print("Translation: ");

                String response = scanner.nextLine();
                if(response.equals("-") || response.isEmpty() || response.isBlank()) {
                    System.out.println("Translated as: " + answer);
                    if(considerMistakes) {
                        writeMistakeIntoFile(randomWord + ";" + answer + "\n", pathToMistakesFile);
                        mistakeWord = randomWord;
                        mistakesList.add(mistakeWord);
                    }
                    break;
                }
                else if (response.equalsIgnoreCase(answer)) {
                    if(mistakesList.contains(randomWord)) {
                        deleteMistakeFromFile(randomWord, pathToMistakesFile);
                        mistakesList.remove(randomWord);
                        System.out.println("Nice work. Mistake has been corrected!");
                    }
                    else {
                        System.out.println("Good job! Correctly");
                    }
                    correctGuessCounter++;
                    break;
                } else {
                    if(considerMistakes) {
                        writeMistakeIntoFile(randomWord + ";" + answer + "\n", pathToMistakesFile);
                        mistakeWord = randomWord;
                        mistakesList.add(mistakeWord);
                    }
                    System.out.print("Incorrect :( \nContinue with a next word? (yes/anything to try again): ");
                    response = scanner.nextLine();

                    List<String> possibleAnswers = List.of("yes", "y", "да");
                    if (possibleAnswers.contains(response.toLowerCase())) {
                        System.out.println("Translated as: " + answer);
                        break;
                    }
                }
            }
            if(!mistakeWord.isEmpty()) {
                wordsList.add(mistakeWord);
            }
            else {
                wordsList.remove(randomWord);
            }
        }
    }
}
