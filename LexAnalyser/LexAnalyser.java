package LexAnalyser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;

public class LexAnalyser {
    private List<Token> tokens;
    private String fileName;

    public LexAnalyser(String fileName) {
        tokens = new ArrayList<>();
        this.fileName = fileName;

    }

    public List<Token> scanner() {
        try (FileReader fileReader = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                try {
                    Tokenizer(line, lineCount);
                } catch (Exception e) {
                    System.out.println("Error while reading line " + lineCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return screener(tokens);
    }

    private void Tokenizer(String line, int lineCount) throws Lexical_Exception {

        // Tokenization patterns
        String Letter = "[a-zA-Z]";
        String Digit = "[0-9]";
        Pattern Identifier_Pattern = Pattern.compile(Letter + "(" + Letter + "|" + Digit + "|" + "_)*");
        Pattern Integer_Pattern = Pattern.compile(Digit + "+");
        Pattern Operator_Symbol_Pattern = Pattern.compile("[+\\-*/<>&.@/:=~|$!#%^_\\[\\]{}\"`\\?]");
        Pattern escape = Pattern.compile("(\\\\'|\\\\t|\\\\n|\\\\\\\\)");
        Pattern operatorPattern = Pattern.compile(Operator_Symbol_Pattern + "+");
        Pattern Punctuation_Pattern = Pattern.compile("[(),;]");
        Pattern Spaces_Pattern = Pattern.compile("\\s+");
        Pattern String_Pattern = Pattern
                .compile("\\''(" + Letter + "|" + Digit + "|" + Operator_Symbol_Pattern + "|" + escape + "|"
                        + Punctuation_Pattern + "|" + Spaces_Pattern + ")*\\''");
        Pattern Comment_Pattern = Pattern.compile("//.*");
        List<String> keywords = List.of(
                "let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls",
                "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec");

        int position_index = 0;
        while (position_index < line.length()) {// go throught the whole line to capture tokens
            char currentChar = line.charAt(position_index);
            String remaining = line.substring(position_index);
            Matcher matcher;

            // find comments
            matcher = Comment_Pattern.matcher(remaining);
            if (matcher.lookingAt()) {
                tokens.add(new Token(TokenType.COMMENT, matcher.group(), lineCount));
                position_index += matcher.group().length();// increase index by the found token length so after that
                                                           // token we can continue with matching
                continue;
            }

            // find whitespace characters
            matcher = Spaces_Pattern.matcher(remaining);
            if (matcher.lookingAt()) {
                tokens.add(new Token(TokenType.WHITESPACE, matcher.group(), lineCount));
                position_index += matcher.group().length();
                continue;
            }

            // Identifier or Keyword
            matcher = Identifier_Pattern.matcher(remaining);
            if (matcher.lookingAt()) {
                String identifier = matcher.group();
                // distinguish Keywords and Identifiers based on list of keywords stored
                if (keywords.contains(identifier))
                    tokens.add(new Token(TokenType.KEYWORD, matcher.group(), lineCount));
                else
                    tokens.add(new Token(TokenType.IDENTIFIER, matcher.group(), lineCount));
                position_index += matcher.group().length();
                continue;
            }

            // Operator
            matcher = operatorPattern.matcher(remaining);
            if (matcher.lookingAt()) {
                tokens.add(new Token(TokenType.OPERATOR, matcher.group(), lineCount));
                position_index += matcher.group().length();
                continue;
            }

            // Integer
            matcher = Integer_Pattern.matcher(remaining);
            if (matcher.lookingAt()) {
                tokens.add(new Token(TokenType.INTEGER, matcher.group(), lineCount));
                position_index += matcher.group().length();
                continue;
            }

            // String
            matcher = String_Pattern.matcher(remaining);
            if (matcher.lookingAt()) {
                tokens.add(new Token(TokenType.STRING, matcher.group(), lineCount));
                position_index += matcher.group().length();
                continue;
            }

            // Punctuation
            matcher = Punctuation_Pattern.matcher(Character.toString(currentChar));
            if (matcher.matches()) {
                tokens.add(new Token(TokenType.PUNCTUATION, Character.toString(currentChar), lineCount));
                position_index++;
                continue;
            }

            // If any Unrecognized token
            throw new Lexical_Exception("Unrecognized token at line " + lineCount + ": " + currentChar);
        }
    }

    // screener function is defined for remove unnessary tokens like whitespace and
    // comments.
    public List<Token> screener(List<Token> tokens) {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            if (token.type == TokenType.WHITESPACE || token.type == TokenType.COMMENT) {
                iterator.remove(); // Use iterator's remove method to avoid ConcurrentModificationException
            }
        }
        return tokens;
    }

    // Custom exception for lexical errors
    static class Lexical_Exception extends Exception {
        Lexical_Exception(String message) {
            super(message);
        }
    }

}
