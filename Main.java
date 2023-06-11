import org.antlr.v4.runtime.*;


import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        File myswiftout = new File("./src/out.swift");
        myswiftout.createNewFile();
        CharStream in = CharStreams.fromFileName("./src/text.kt");
        KotlinLexer lexer = new KotlinLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KotlinParser parser = new KotlinParser(tokens);
        KotlinVisitor visitor = new KotlinVisitor();
        String str = visitor.visitKotlinFile(parser.kotlinFile());
        PrintWriter writer = new PrintWriter("./src/out.swift");
        writer.print("");
        writer.print(str);
        writer.close();
    }
}