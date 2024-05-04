all:
    javac -classpath . LexAnalyser/*.java Parser_RPAL/*.java
    java myrpal.java