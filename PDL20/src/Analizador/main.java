package Analizador;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import TablaDeSimbolos.tablaDeSimbolos;
import Lexico.AnalizadorLexico;
import Lexico.Token;
import Sintactico.AnalizadorSintactico;

public class main {
	public static void main (String[] args) {
		String contenido = "";
		ArrayList<Token> tokens = new ArrayList<>();
		tablaDeSimbolos TS = new tablaDeSimbolos(0);
		AnalizadorLexico analizadorLex = new AnalizadorLexico(tokens, TS);
		AnalizadorSintactico analizadorSin;
		try {
			FileReader fr = new FileReader("C:\\Users\\aleja\\eclipse-workspace\\PDL20\\src\\Pruebas\\PIdG20.txt");
			FileWriter fw = new FileWriter("C:\\Users\\aleja\\eclipse-workspace\\PDL20\\src\\Pruebas\\tokens.txt");
			FileWriter fw2 = new FileWriter("C:\\Users\\aleja\\eclipse-workspace\\PDL20\\src\\Pruebas\\tablaDeSimbolos.txt");
			FileWriter fw3 = new FileWriter("C:\\Users\\aleja\\eclipse-workspace\\PDL20\\src\\Pruebas\\parse.txt");
			int c = fr.read();
			while (c!=-1) {
				contenido += (char)c;
				c = fr.read();
			}
			analizadorLex.analizar(contenido);
			fr.close();
			for(int i=0; i<tokens.size(); i++) {
				fw.write(tokens.get(i).toString());
				fw.write('\n');
			}
			fw.close();
			
			analizadorSin = new AnalizadorSintactico(tokens, TS);
			fw3.write(analizadorSin.analizar());
			fw3.close();
			
			fw2.write(TS.toString());
			fw2.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


