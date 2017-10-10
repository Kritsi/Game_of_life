package sample;

import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import java.io.*;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kristine B. Skjellestad on 17.03.2017.
 */
public class ReadFile {
    ListBoard lb = new ListBoard();
    private int w;
    private int h;
    private String pat = "";
    private StringBuilder stringPatternSize = new StringBuilder();
    private StringBuilder stringPattern = new StringBuilder();
    Board b = new Board();

    /**
     * Choose a file from your disk.
     * The FileChooser will automatically give you error message.
     * It generates its own message.
     */
    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open text file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.rle")
        );

        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            readFile(new FileReader(file));
        }else {
            pat = null;
        }
    }

    /**
     * Takes in a URL to be read.
     */
    public void readGameBoardURL() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Open URL file");
        dialog.setTitle("Open URL file");
        dialog.setContentText("Enter URL");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String url = result.get();
            URL destination = new URL(url);
            URLConnection conn = null;
            conn = destination.openConnection();
            readFile(new InputStreamReader(conn.getInputStream()));
        }

    }

    /**
     * Reads file that has been selected.
     * Picks lines in file that is relevant for the pattern in gol.
     * @param file reads the file.
     */
    public void readFile(Reader file) throws IOException {
        BufferedReader br= null;
        String line;

        br = new BufferedReader(file);
        stringPattern.setLength(0);
        stringPatternSize.setLength(0);
        while((line = br.readLine()) != null) {
            if(line.indexOf("x =") > -1 || line.indexOf("y =") > -1) {
                stringPatternSize.append(line);
            }
            if(line.indexOf('#') == -1 && line.indexOf('x') == -1 && line.indexOf('y') == -1) {
                stringPattern.append(line);
            }
        }
        readSize();
        readPattern();
    }

    /**
     * Finds the x and y value from the file.
     * Puts the found value in private string to the class.
     */
    public void readSize() {
        Pattern p = Pattern.compile("\\s+(\\d+)");
        String[] line = stringPatternSize.toString().split(",");
        for(int i = 0; i < line.length; i++) {
            if(line[i].contains("x")) {
                String tester = line[i];
                Matcher m = p.matcher(tester);
                if (m.find()) {
                    w = Integer.parseInt(m.group(1));
                }
            }
            if(line[i].contains("y")) {
                String tester = line[i];
                Matcher m = p.matcher(tester);
                if (m.find()) {
                    h = Integer.parseInt(m.group(1));
                }
            }
        }
    }

    /**
     * Interprets the pattern.
     * The ends result is being put in private string to the class.
     */
    public void readPattern() {
        int count = 1;
        Pattern p2 = Pattern.compile("\\d+");
        String foundInt;
        StringBuilder translated = new StringBuilder();

        String[] linje = stringPattern.toString().split("\\$");
        for(int i = 0; i < linje.length; i++) {
            Matcher m = p2.matcher(linje[i]);

            List<String> intV = new ArrayList<String>();
            List<Integer> intPos = new ArrayList<Integer>();

            //Finds the numbers and their index position.
            while(m.find()) {
                foundInt = m.group();
                intV.add(foundInt);
                intPos.add(m.start());
            }

            char[] charArray = linje[i].toCharArray();

            //Loops through chararray to check each letter.
            for(int j = 0; j < charArray.length; j++) {
                if(charArray[j] == 'o') {
                    translated.append('1');
                } else if(charArray[j] == 'b') {
                    translated.append('0');
                }

                //Checks if j exist in intPos.
                if(intPos.contains(j)) {
                    //Finds index to the position
                    int indexj = intPos.indexOf(j);
                    String nr = intV.get(indexj);
                    //Finds the length of the number to know where the next o/b/$ is
                    int nrLength = nr.length();
                    int nrInt = Integer.parseInt(nr);

                    //Checks if j (integer) is in the end of array, if so increase the count to number * the integer
                    //The rest sets as 1/0 dependent of o/b
                    if(j+nrLength-1 == charArray.length-1) {
                            count += Integer.parseInt(intV.get(indexj))-1;
                    } else if(charArray[j+nrLength] == 'o') {
                        for(int x = 0; x < nrInt-1; x++) {
                            translated.append('1');
                        }
                    } else if(charArray[j+nrLength] == 'b') {
                        for(int x = 0; x < nrInt-1; x++) {
                            translated.append('0');
                        }
                    }
                }
            }

            int currentLength = translated.length();

            //Checks if current translated String is as long as width, if not, increase what remains
            if(currentLength != w * count) {
                int rest = (w * count) - currentLength;
                for(int x = 0; x < rest; x++) {
                    translated.append('0');
                }
            }
            count++;
        }
        pat = translated.toString();
    }

    public String getPat() {
        return pat;
    }

    /**
     * Checks if the pattern that is being read fit the board.
     * @throws PatternFormatException if the array board i static and the pattern is too big, it's throws this exception.
     */
    public void checkPatSize() throws PatternFormatException {
        int c = b.getH();
        int r = b.getW();
        if(r < b.getW() || c < b.getH()) {
            throw new PatternFormatException("To large pattern");
        }
    }

    /**
     * Take pattern string and convert it to an 2d array.
     * @return array with pattern from file.
     */
    public List<List<Integer>> pattern() throws PatternFormatException {
        if(h > w) {
            if(h > lb.getSizeH()) {
                int rounded = ((h + 99) / 100 ) * 100;
                lb.setSizeW(rounded);
                lb.setSizeH(rounded);
            }
        } else {
            if(w > lb.getSizeW()) {
                int rounded = ((w + 99) / 100 ) * 100;
                lb.setSizeW(rounded);
                lb.setSizeH(rounded);
            }
        }
        int c = lb.getSizeH();
        int r = lb.getSizeW();

        List<List<Integer>> bp = new ArrayList<List<Integer>>(r);
        for(int i = 0; i < lb.getSizeH(); i++)  {
            bp.add(new ArrayList<Integer>());
        }

        for (int x = 0; x < lb.getSizeH(); x++) {
            for (int y = 0; y < lb.getSizeW(); y++) {
                bp.get(x).add(y, 0);
            }
        }

        int startx;
        int starty;

        checkPatSize();
        //Calculate the start point for the pattern to set in center of the board.
        int yRemains = r-w;
        starty = yRemains/2;
        int xRemains = c-h;
        startx = xRemains/2;

        int count = 0;
        if(pat != null) {
            char[] charArray = pat.toCharArray();
            for (int x = startx; x < startx+h; x++) {
                for (int y = starty; y < starty+w; y++) {
                    if(charArray[count] == '1') {
                        bp.get(x).set(y, 1);
                    } else if(charArray[count] == '0') {
                        bp.get(x).set(y, 0);
                    }
                    count++;
                }
            }
        }
        return bp;
    }
}
