all:
    javac -classpath . LexAnalyser/*.java Parser_RPAL/*.java
    java Parser_RPAL.Parser_Tester