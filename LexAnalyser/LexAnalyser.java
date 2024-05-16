package LexAnalyser;

import Exception.Lexical_Exception;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexAnalyser {
    private List<Token> tokens;
    private String fileName;

    // Constructor
    public LexAnalyser(String fileName) {
        tokens = new ArrayList<>();
        this.fileName = fileName;
    }

    

    // Method to scan the input file and tokenize it
    public List<Token> scanner () {

        try (FileReader fileReader = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {

                lineCount++;
                try {
                    tokenize_Line(line, lineCount);
                } catch (Lexical_Exception e) {

                    System.out.println("Error while reading line " + lineCount + ": " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {

            System.out.println("File not found in the current directory");

        }catch(IOException e){

            System.out.println("IOException !!!");
        }

        return screener(tokens);
    }

    // Method to tokenize each line of input
    private void tokenize_Line(String line, int lineCount) throws Lexical_Exception {

        StringBuilder current_Token = new StringBuilder();

        for (int index_var = 0; index_var < line.length(); index_var++) {

            char current_Charector = line.charAt(index_var);

            // ###################### Handling identifiers and keywords ##########################
            if (Character.isLetter(current_Charector)) {

                current_Token.append(current_Charector);

                while (index_var + 1 < line.length()
                        && (Character.isLetterOrDigit(line.charAt(index_var + 1))
                                || line.charAt(index_var + 1) == '_')) {

                    current_Token.append(line.charAt(++index_var));

                }
                if (isKeyword(current_Token.toString())) {

                    tokens.add(new Token(TokenType.KEYWORD, current_Token.toString(), lineCount));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, current_Token.toString(), lineCount));
                }

                current_Token.setLength(0); // Clearing the token buffer
            }

            //########################### Handling integers  ##############################

            else if (Character.isDigit(current_Charector)) {
                current_Token.append(current_Charector);

                while (index_var + 1 < line.length() && Character.isDigit(line.charAt(index_var + 1))) {

                    current_Token.append(line.charAt(++index_var));
                }

                tokens.add(new Token(TokenType.INTEGER, current_Token.toString(), lineCount));
                current_Token.setLength(0);
            }

            //################################ Handling operators  ##############################

            else if (isOperator(current_Charector)) {

                current_Token.append(current_Charector);
                if (current_Charector == '-' && index_var + 1 < line.length() && line.charAt(index_var + 1) == '>') {

                    current_Token.append(line.charAt(++index_var)); // Append '>'
                }

                tokens.add(new Token(TokenType.OPERATOR, current_Token.toString(), lineCount));
                current_Token.setLength(0);
            }

            //############################### Handling string literals ###############################

            else if (current_Charector == '\'') {
                current_Token.append(current_Charector);

                while (index_var + 1 < line.length() && line.charAt(index_var + 1) != '\'') {
                    current_Token.append(line.charAt(++index_var));
                }

                if (index_var + 1 < line.length() && line.charAt(index_var + 1) == '\'') {
                    current_Token.append(line.charAt(++index_var)); // Include closing '
                    tokens.add(new Token(TokenType.STRING, current_Token.toString(), lineCount));
                    current_Token.setLength(0);
                } else {

                    throw new Lexical_Exception("Undetermined string literal at line " + lineCount);
                }

            }

            //################################# Handling punctuation  ################################

            else if (isPunctuation(current_Charector)) {

                tokens.add(new Token(TokenType.PUNCTUATION, String.valueOf(current_Charector), lineCount));
            }

            // Handling unrecognized tokens
            else if (!Character.isWhitespace(current_Charector)) {

                throw new Lexical_Exception("Unrecognized token at line " + lineCount + ": " + current_Charector);
            }
        }
    }

    // ######################## Method to check if a token is a keyword  #####################

    private boolean isKeyword(String token) {
        List<String> keywords = List.of(
                "let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls",
                "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec");

        return keywords.contains(token);
    }

    //####################### Method to check if a character is a punctuation mark ###################
    private boolean isPunctuation(char character) {

        String punctuation = "(),;";
        return punctuation.indexOf(character) != -1;
    }

    //###################### Method to check if a character is an operator ######################
    private boolean isOperator(char character) {

        String operators = "+-*/<>&.@/:=~|$!#%^_[]{}\"`?";
        return operators.indexOf(character) != -1;
    }

    

    // ################## Method to filter out whitespace and comments ##########################
    public List<Token> screener(List<Token> tokens) {

        List<Token> filteredTokens = new ArrayList<>();
        for (Token token : tokens) {

            if (token.type != TokenType.WHITESPACE && token.type != TokenType.COMMENT) {

                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

}
