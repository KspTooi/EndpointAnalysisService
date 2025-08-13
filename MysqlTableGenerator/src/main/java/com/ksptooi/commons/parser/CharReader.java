package com.ksptooi.commons.parser;

import java.util.ArrayList;
import java.util.List;

public class CharReader {

    private final String rawText;
    private int cursor = -1;
    private final StringBuilder textBlock = new StringBuilder();
    private final List<Character> textBlockSeparator = new ArrayList<>();

    public CharReader(String rawText){
        this.rawText = rawText;
    }

    public void addTextBlockSeparator(char c){
        textBlockSeparator.add(c);
    }

    public String nextBlock(){

        if(textBlockSeparator.isEmpty()){
            throw new RuntimeException("textBlockSeparator isEmpty");
        }

        StringBuilder block = new StringBuilder();

        while (hasNext()) {

            var nxt = next();

            if(textBlockSeparator.contains(nxt)){
                if(block.isEmpty()){
                    return null;
                }
                return block.toString();
            }

            block.append(nxt);
        }

        if(block.isEmpty()){
            return null;
        }
        return block.toString();
    }


    public char next() {
        cursor++;
        if (cursor >= rawText.length()) {
            throw new RuntimeException("cursor >= rawText.length()");
        }

        var c = rawText.charAt(cursor);
        textBlock.append(c);

        if(isSeparator()){
            textBlock.setLength(0);
        }

        return c;
    }

    public void skip(int count){
        cursor += count;
    }

    public CharReader nxt(){
        next();
        return this;
    }

    public CharReader prv(){
        prev();
        return this;
    }

    public char prev() {
        if (cursor < 0) {
            throw new RuntimeException("cursor < 0");
        }
        return rawText.charAt(cursor--);
    }

    public boolean hasNext() {
        return cursor + 1 < rawText.length();
    }

    public boolean hasPrev() {
        return cursor > 0;
    }

    public int getCursor(){
        return cursor;
    }

    public boolean eq(char c){
        return rawText.charAt(cursor) == c;
    }

    public boolean eq(char... c){
        ensureCursorValid();
        char currentChar = rawText.charAt(cursor);
        for (char target : c) {
            if (currentChar == target) {
                return true;
            }
        }
        return false;
    }

    public boolean eq(String s){
        return textBlock.toString().equals(s);
    }

    public boolean isLineBreak() {
        ensureCursorValid();
        char currentChar = rawText.charAt(cursor);
        return currentChar == '\n' || currentChar == '\r';
    }

    public boolean isSpace() {
        ensureCursorValid();
        return Character.isWhitespace(rawText.charAt(cursor));
    }

    public boolean isUpperCase() {
        ensureCursorValid();
        return Character.isUpperCase(rawText.charAt(cursor));
    }

    public boolean isAlphabet() {
        ensureCursorValid();
        return Character.isAlphabetic(rawText.charAt(cursor));
    }

    public boolean isNumber(){
        ensureCursorValid();
        return Character.isDigit(rawText.charAt(cursor));
    }

    public boolean isSeparator() {
        return textBlockSeparator.contains(rawText.charAt(cursor));
    }

    public void ensureCursorValid(){
        if (cursor < 0 || cursor >= rawText.length()) {
            throw new RuntimeException("Cursor out of bounds");
        }
    }

    public char getCur(){
        return rawText.charAt(cursor);
    }

    public void setCursor(int cursor){
        this.cursor = cursor;
    }

    public void skipNextBlock(){

        while (hasNext()){

            var next = rawText.charAt(cursor + 1);

            //下一个字符是块分隔符
            if(textBlockSeparator.contains(next)){
                next();
            }else {
                return;
            }


        }

    }

    //向后查找文本中是否包含某个字符 返回cursor (这个函数不会改变当前cursor)
    public int findNext(char target) {
        int tempCursor = cursor;
        while (tempCursor + 1 < rawText.length()) {
            tempCursor++;
            if (rawText.charAt(tempCursor) == target) {
                return tempCursor;
            }
        }
        return -1; // 未找到目标字符
    }

    public int findPrev(char target) {
        int tempCursor = cursor;
        while (tempCursor - 1 >= 0) {
            tempCursor--;
            if (rawText.charAt(tempCursor) == target) {
                return tempCursor;
            }
        }
        return -1; // 未找到目标字符
    }

    public String getTextBlock(){
        return textBlock.toString();
    }

    public static void main(String[] args) {

        CharReader cr = new CharReader("public void; PUBLKC SAGFGL;KJH//");
        cr.addTextBlockSeparator(' ');
        cr.addTextBlockSeparator(';');

        while (cr.hasNext()){
            System.out.println(cr.nxt().getTextBlock());
        }

    }


}
