package test;
import org.junit.Test;
import sample.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by kristine B. Skjellestad on 15.03.2017.
 */
public class BoardTest2 {
    @Test
    public void nextGen() throws Exception {
        int[][] b = {
                { 0, 0, 0, 0, 0},
                { 0, 0, 1, 0, 0},
                { 1, 0, 1, 0, 0},
                { 0, 1, 1, 0 ,0},
                { 0, 0, 0, 0 ,0}
        };
        Board board = new Board();
        board.setBoard(b);
        System.out.println(Arrays.deepToString(b));
        System.out.println(board.toString());
        board.nextGen(); /*"0000000000011100000000000"*/
        assertEquals(board.toString(),"0000001000001100110000000");
    }

    @Test
    public void rules() {
        int[][] b = {
                { 0, 0, 0, 0,0 },
                { 0, 0, 1, 0,0 },
                { 0, 0, 1, 0,0 },
                { 0, 0, 1, 0 ,0},
                { 0, 0, 0, 0 ,0}
        };
        int[][] c = {
                {0, 1, 1, 1, 0},
                {0, 2, 1, 2, 0},
                {0, 3, 2, 3, 0},
                {0, 2, 1, 2, 0},
                {0, 1, 1, 1, 0}
        };

        int[][] res = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };
        int[][] next = new int[b.length][b[0].length];
        Rules r = new Rules();
        r.rules(c, b, next);
        System.out.println(Arrays.deepToString(next));
        assertArrayEquals(res, next);
    }

    @Test
    public void testNextGenListBoard() {
        List<List<Integer>> b = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 1, 1, 1, 1));
            add(Arrays.asList(0, 1, 1, 1, 1));
            add(Arrays.asList(0, 0, 0, 0, 0));
        }};
        ListBoard lb = new ListBoard();
        lb.setBoardList(b);
        lb.nextGen();
        assertEquals(lb.toString(), "0000000110010010100100110");
    }

    @Test
    public void testRulesListBoard() {
        List<List<Integer>> b = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 0, 1, 1, 1));
            add(Arrays.asList(0, 1, 1, 1, 0));
            add(Arrays.asList(0, 0, 0, 0, 0));
        }};
        List<List<Integer>> c = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 1, 2, 3, 2));
            add(Arrays.asList(0, 3, 4, 4, 2));
            add(Arrays.asList(1, 2, 4, 4, 3));
            add(Arrays.asList(1, 2, 3, 1, 1));
        }};
        List<List<Integer>> res = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(0, 0, 0, 0, 0));
            add(Arrays.asList(0, 0, 0, 1, 0));
            add(Arrays.asList(0, 1, 0, 0, 1));
            add(Arrays.asList(0, 1, 0, 0, 1));
            add(Arrays.asList(0, 0, 1, 0, 0));
        }};
        Rules r = new Rules();

        List<List<Integer>> next = new ArrayList<List<Integer>>(b.size());
        int h = b.size();
        int w = b.get(0).size();

        for(int i = 0; i < h; i++)  {
            next.add(new ArrayList<Integer>(w));
        }

        r.rulesList(c, b, next);
        assertEquals(Arrays.deepToString(res.toArray()), Arrays.deepToString(next.toArray()));
    }

    @Test
    public void readFileTest() throws IOException {
        ReadFile rf = new ReadFile();
        URL destination = new URL("http://www.conwaylife.com/patterns/glider.rle");
        URLConnection conn = destination.openConnection();
        rf.readFile(new InputStreamReader(conn.getInputStream()));
        rf.readSize();
        rf.readPattern();
        assertEquals(rf.getPat(), "010001111");
    }

    @Test
    public void testNextGenOriginal() throws IOException, PatternFormatException {
        ListBoard lb = new ListBoard();
        Rules r = new Rules();
        ReadFile rf = new ReadFile();

        String url = "http://www.conwaylife.com/patterns/infinitelwsshotel.rle";
        URL destination = new URL(url);
        URLConnection conn = null;
        conn = destination.openConnection();
        rf.readFile(new InputStreamReader(conn.getInputStream()));
        lb.setBoardList(rf.pattern());

        List<List<Integer>> next = new ArrayList<List<Integer>>(lb.getBoardList().size());
        int h = lb.getBoardList().size();
        int w = lb.getBoardList().get(0).size();

        for(int i = 0; i < h; i++)  {
            next.add(new ArrayList<Integer>(w));
        }

        long start = System.currentTimeMillis();
        r.rulesList(lb.countNeighbours(), lb.getBoardList(), next);
        for(int i = 0; i < 1000; i++) {
            lb.nextGen();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Counting time (ms): " + elapsed);
    }

    @Test
    public void testNextGenThread() throws IOException, PatternFormatException {
        ListBoard lb = new ListBoard();
        Rules r = new Rules();
        ReadFile rf = new ReadFile();

        String url = "http://www.conwaylife.com/patterns/infinitelwsshotel.rle";
        URL destination = new URL(url);
        URLConnection conn = null;
        conn = destination.openConnection();
        rf.readFile(new InputStreamReader(conn.getInputStream()));
        lb.setBoardList(rf.pattern());

        List<List<Integer>> next = new ArrayList<List<Integer>>(lb.getBoardList().size());
        int h = lb.getBoardList().size();
        int w = lb.getBoardList().get(0).size();

        for(int i = 0; i < h; i++)  {
            next.add(new ArrayList<Integer>(w));
        }

        long start = System.currentTimeMillis();
        r.rulesList(lb.countNeighbours(), lb.getBoardList(), next);
        for(int i = 0; i < 1000; i++) {
            lb.nextNewGen();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Counting time (ms): " + elapsed);
    }
}