package Lexico;

import java.util.ArrayList;
import TablaDeSimbolos.tablaDeSimbolos;

public class AnalizadorLexico {
	ArrayList<Token> tokens = new ArrayList<>();
	tablaDeSimbolos TS = new tablaDeSimbolos(0);
	int numTSAUX=0;
	boolean func=false;
	boolean zona_decl=false;
	boolean params=false;
	int contadorLlavesFunc = 0;
	ArrayList<String> parametrosFuncionActual;  // Lista para guardar los nombres de los parámetros de la función
	
	public AnalizadorLexico (ArrayList<Token> tokens, tablaDeSimbolos TS) {
		this.tokens = tokens;
		this.TS = TS;
	}
	
	public void analizar (String texto){
		String lexema = "";
		ArrayList<String> lineas = new ArrayList<>();
		lineas = separar (texto,'\n');


		for(int i=0; i < lineas.size() ; i++) {
			for(int j=0; j < lineas.get(i).length(); j++) {
				int pos;
				pos = lineas.get(i).codePointAt(j);
				
				//UNA LETRA
				if((pos > 96 && pos < 123) || (pos > 64 && pos < 91)) { 
					while ((lineas.get(i).codePointAt(j) > 96 && lineas.get(i).codePointAt(j)< 123) || 
							(lineas.get(i).codePointAt(j) > 64 && lineas.get(i).codePointAt(j) < 91) ||
							(lineas.get(i).codePointAt(j) > 47 && lineas.get(i).codePointAt(j) < 58) ||
							lineas.get(i).codePointAt(j) == 95) {		
						lexema = lexema + lineas.get(i).charAt(j);
						j++;
					}
					if (j < lineas.get(i).length()-1 && (lineas.get(i).codePointAt(j) != 32))
						j--;
					if(lexema.equals("boolean")) {
						tokens.add(new Token("BOOLEAN", "", i+1)); 
					}else if(lexema.equals("for")) {
						tokens.add(new Token("FOR", "", i+1));
					}else if(lexema.equals("function")) {
						tokens.add(new Token("FUNC", "", i+1));
						numTSAUX++;
						func=true;
						params=true;
						parametrosFuncionActual = new ArrayList<>();
					}else if(lexema.equals("if")) {
						tokens.add(new Token("IF", "", i+1));
					}else if(lexema.equals("input")) {
						tokens.add(new Token("INPUT", "", i+1));
					}else if(lexema.equals("int")) {
						tokens.add(new Token("INT", "", i+1));
					}else if(lexema.equals("output")) {
						tokens.add(new Token("OUTPUT", "", i+1));
					}else if(lexema.equals("return")) {
						tokens.add(new Token("RETURN", "", i+1));
					}else if(lexema.equals("string")) {
						tokens.add(new Token("STRING", "", i+1));
					}else if(lexema.equals("var")) {
							tokens.add(new Token("VAR", "", i+1));
					}else if(lexema.equals("void")) {
							tokens.add(new Token("VOID", "", i+1));
					}else {
						if(func && zona_decl&&params) {
						parametrosFuncionActual.add(lexema);
						TS.add(numTSAUX+"&"+lexema);
						Integer n = TS.posTS(numTSAUX+"&"+lexema);
						tokens.add(new Token("ID",  n.toString(), i+1));
						}else if(func) {
							if (parametrosFuncionActual.contains(lexema)) {
	                            // Si es un parámetro de la función, se agrega con el prefijo `numTSAUX&`
	                            TS.add(numTSAUX + "&" + lexema);
	                            Integer n = TS.posTS(numTSAUX + "&" + lexema);
	                            tokens.add(new Token("ID", n.toString(), i + 1));
	                        } else {
	                            // Si no es un parámetro, se añade como variable normal
	                            TS.add(lexema);
	                            Integer n = TS.posTS(lexema);
	                            tokens.add(new Token("ID", n.toString(), i + 1));
	                        }
						}
						else {
							TS.add(lexema);
							Integer n = TS.posTS(lexema);
							tokens.add(new Token("ID",  n.toString(), i+1));
							
						}
						
						
					}
					
				//UN NUMERO
				}else if (pos > 47 && pos < 58) {
				    
				    while (j < lineas.get(i).length() && lineas.get(i).codePointAt(j) > 47 && lineas.get(i).codePointAt(j) < 58) {		
				        lexema = lexema + lineas.get(i).charAt(j);
				        j++;
				    }
				    
				    try {
				        int valor = Integer.parseInt(lexema);
				        // Verificar si el número está dentro del rango de -32768 a 32767
				        if (valor < -32768 || valor > 32767) {
				            System.out.println("Error Léxico 2 en la linea " + (i + 1) + ": Número fuera de rango en la línea ");
				        } else {
				            tokens.add(new Token("ENT", lexema, i + 1));
				        }
				    } catch (NumberFormatException e) {
				        System.out.println("Error Léxico 2 en la linea " + (i + 1) + ": Número fuera de rango en la línea " + (i + 1));
				    }

				    if (j < lineas.get(i).length() - 1 && lineas.get(i).codePointAt(j) != 32) {
				        j--;
				    }
				
				//UNA CADENA
				}else if(pos == 39) {					
					j++;
					while ((j < lineas.get(i).length()-1) && (lineas.get(i).codePointAt(j) != 39)) {
						// Verificar si hay un salto de línea en medio de la cadena
						if (lineas.get(i).charAt(j) == '\n') {
						    System.out.println("Error Léxico 1 en la linea " + (i + 1) + ": Salto de línea en medio de una cadena en la línea " + (i + 1));
						    break;
						}
						lexema = lexema + lineas.get(i).charAt(j);
						j++;
					}
					if(lexema.length() - 1 <= 64)
						tokens.add(new Token("CAD", "\"" + lexema + "\"", i+1));
					else {
						System.out.println("Error Léxico 3 en la linea " + (i + 1) + ": La cadena " + lexema + " supera el tamaño máximo permitido" );
						break;
					}
				
				//COMENT , ASIGDIV y DIV
				}else if(pos == 47) {  // 47 es el código ASCII para '/'
				    try {
				        int siguientePos = lineas.get(i).codePointAt(j + 1);
				        if (siguientePos == 47) {  // Si es un segundo '/', es un comentario
				            while (j < lineas.get(i).length() - 1) {
				                lexema = lexema + lineas.get(i).charAt(j);
				                j++;
				            }
				            // Comentario ignorado
				        } else if (siguientePos == 61) {  // Si el siguiente es '=', es "/="
				            tokens.add(new Token("ASIGDIV", "", i + 1));
				            j++; // Avanza para saltar el '='
				        } else {  // Si no es "//" ni "/=", entonces es solo "/"
				            tokens.add(new Token("DIV", "", i + 1));
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				 
				// NEGACION Y DISTINTO
				}else if(pos == 33) {  // Código ASCII para '!'
				    try {
				        if (j + 1 < lineas.get(i).length() && lineas.get(i).codePointAt(j + 1) == 61) {  // Verifica si es '!='
				            tokens.add(new Token("DIST", "", i + 1));
				            j++;  // Salta el siguiente símbolo '='
				        } else {
				            tokens.add(new Token("NEG", "", i + 1));  // Si no es `!=`, se toma como `!`
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				
				// IGUAL
				}else if(pos == 61) {  // Código ASCII para '='
				    try {
				        if (j + 1 < lineas.get(i).length() && lineas.get(i).codePointAt(j + 1) == 61) {  // Verifica si es '=='
				            System.out.println("Error Léxico 5 en la linea " + (i + 1) + ": token '==' no reconocido");
				            j++;  // Salta el siguiente símbolo '='
				        } else {
				            tokens.add(new Token("IG", "", i + 1));  // Solo se toma `=` como asignación
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				
				//COMA
				}else if(pos == 44) { 
					tokens.add(new Token("COMA", "", i+1));
				
				//PUNTO Y COMA
				}else if(pos == 59) { 
					tokens.add(new Token("PYC", "", i+1));
				
				//PARENTESIS IZQUIERDO
				}else if(pos == 40) {
					tokens.add(new Token("PARIZQ", "", i+1));
					zona_decl=true;
				
				//PARENTESIS DERECHO
				}else if(pos == 41) {
					tokens.add(new Token("PARDER", "", i+1));
					zona_decl=false;
					params=false;
				
				//CORCHETE IZQUIERDO
				}else if(pos == 123) {
					tokens.add(new Token("LLAVIZQ", "", i+1));
					 if (func) {
						 contadorLlavesFunc++;
					 }
				
				//CORCHETE DERECHO
				}else if(pos == 125) {
					tokens.add(new Token("LLAVDER", "", i+1));
					if (func) {
	                    contadorLlavesFunc--;  // Decrementamos al cerrar un bloque
	                    if (contadorLlavesFunc == 0) {  // Si ya no quedan bloques abiertos
	                        func = false;  // Salimos de la función
	                    }
	                }
				
				//Caracter no reconocido
				}else {
					if(pos > 33 && pos < 126)
						System.out.println("Error Léxico 5 en la linea " + (i + 1) + ": el caracter " + lineas.get(i).charAt(j) + " no se reconoce como un token");
				}
				lexema = "";
			}
		}
		tokens.add(new Token("EOF", "", lineas.size()));
	}
	
	public ArrayList<String> separar(String texto, char separador) {
		ArrayList<String> cadenas = new ArrayList<>();
		String linea = "";
		for (int i=0; i < texto.length() ; i++) {
			if (texto.charAt(i) != separador){
				linea = linea + String.valueOf(texto.charAt(i));
			}else{
				cadenas.add(linea);
				linea="";
			}
			if(i == texto.length()-1 && texto.charAt(i) != separador) {
				linea = linea + '\n';
				cadenas.add(linea);
			}
		}
		return cadenas;
	}
}
