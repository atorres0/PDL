package Sintactico;

import java.util.ArrayList;
import Lexico.Token;
import TablaDeSimbolos.tablaDeSimbolos;

public class AnalizadorSintactico {
    ArrayList<Token> tokens;
    ArrayList<Integer> listaParse = new ArrayList<>();
    tablaDeSimbolos TS;
    int i = 0;
    int desplazamiento = 0;
    int desplazamientoAux = 0;
    int contFunc=0;
    boolean func=false;
    int posFuncActual = -1;
    ArrayList<String> numParam = new ArrayList<>();
    String tipoAux = "void";

    public AnalizadorSintactico(ArrayList<Token> tokens, tablaDeSimbolos TS) {
        this.tokens = tokens;
        this.TS = TS;
    }

    public String analizar() {
        String parse = "descendente ";
        P();
        for (int index = 0; index < listaParse.size(); index++) {
            parse += " " + listaParse.get(index);
        }
        return parse;
    }

    private void P() {
        if (esToken("FOR") || esToken("VAR") || esToken("IF") || esToken("ID") || 
            esToken("INPUT") || esToken("OUTPUT") || esToken("RETURN")) {
            listaParse.add(1);
            B();
            P();
        } else if (esToken("FUNC")) {
            listaParse.add(2);
            F();
            P();
        } else if (esToken("EOF")) {
            listaParse.add(3);
        } else {
            error("FOR|VAR|IF|ID|INPUT|OUTPUT|RETURN|FUNC|EOF");
        }
    }
    //Producciones 4-7
    private String B() {
        if (esToken("FOR")) {
            listaParse.add(4);
            consumir("FOR");
            consumir("PARIZQ");
            D();
            consumir("PYC");
            String tipoE = E();
            if (!tipoE.equals("logico")) {
            	errorSemantico("Condicion no es logica");
            }
            consumir("PYC");
            AUX();
            consumir("PARDER");
            consumir("LLAVIZQ");
            C();
            consumir("LLAVDER");
            return "void";
        } else if (esToken("VAR")) {
            listaParse.add(5);
            consumir("VAR");
            String tipo = T();
            String id = tokens.get(i).getAtributo();
            // REPETIDO
            // Comprobar si `id` ya está como parámetro de la función
            
        	 //System.out.println("Tipo: "+ TS.buscaTipo(id));
             //System.out.println("Tipo: "+ TS.getId(Integer.parseInt(id)));
             if(!TS.buscaTipo(id).equals("None")) {
            	 //System.out.println(TS.buscaTipo(id));
            	 //System.out.println(TS.posTS(id));
            	String lexema = TS.getId(Integer.parseInt(id));
            	//System.out.println(lexema);
            	if(func && lexema.length() > 1 && lexema.charAt(1) == '&') {
            		lexema = lexema.substring(2);
            	}
             	errorSemantico("se intenta inicializar dos veces el mismo id: " + lexema );
             	
             }else {
            	 if(func) {
                 	contFunc++;
                 	TS.addTipoyDespl(id, tipo, desplazamientoAux);
                     actualizarDesplazamientoAux(tipo);
                 }else {
                 	TS.addTipoyDespl(id, tipo, desplazamiento);
                 	actualizarDesplazamiento(tipo);
                 }
             }	
            
            consumir("ID");
            consumir("PYC");
            return tipo;
        } else if (esToken("IF")) {
            listaParse.add(6);
            consumir("IF");
            consumir("PARIZQ");
            String tipoE = E();
            //System.out.println("tipo if: "+ tipoE);
            if (!tipoE.equals("logico")) {
            	errorSemantico("Condicion no es logica");
            }
            consumir("PARDER");

            // Verifica si hay llaves o no NO ESTA EN LA GRAMATICA
            if (esToken("LLAVIZQ")) {
                consumir("LLAVIZQ");
                C();  // Bloque de múltiples instrucciones
                consumir("LLAVDER");
            } else {
                S();  // Una única instrucción sin llaves
            }

            return "void";
        } else {
            listaParse.add(7);
            return S();
        }
    }
    //Producciones 8-10
    private void D() {
        if (esToken("VAR")) {
            listaParse.add(8);
            consumir("VAR");
            String tipo = T();
            String id = tokens.get(i).getAtributo();
            if(func) {
            	contFunc++;
            	TS.addTipoyDespl(id, tipo, desplazamientoAux);
                actualizarDesplazamientoAux(tipo);
            }else {
            	TS.addTipoyDespl(id, tipo, desplazamiento);
            	actualizarDesplazamiento(tipo);
            }
            
            consumir("ID");
        } else if (esToken("ID")) {
            listaParse.add(9);
            consumir("ID");
            consumir("IG");
            String tipoE = E();
            String tipoID = TS.buscaTipo(tokens.get(i - 3).getAtributo());
            //System.out.println("ID: "+TS.getId(Integer.parseInt(tokens.get(i - 3).getAtributo())) + " tipo 1:" + tipoID);
            //System.out.println("tipo 2:" + tipoE);
            if (!tipoE.equals(tipoID)) {
            	errorSemantico("Tipos incompatibles en asignación");
            }
        } else {
            listaParse.add(10);
        }
    }
    //Producciones 11-13
    private String T() {
        if (esToken("INT")) {
            listaParse.add(11);
            consumir("INT");
            return "entero";
        } else if (esToken("BOOLEAN")) {
            listaParse.add(12);
            consumir("BOOLEAN");
            return "logico";
        } else if (esToken("STRING")) {
            listaParse.add(13);
            consumir("STRING");
            return "cadena";
        } else {
            error("INT|BOOLEAN|STRING");
            return null;
        }
    }
  //Producciones 14-17
    private String S() {
        if (esToken("ID")) {
            listaParse.add(14);
            
            String tipo = TS.buscaTipo(tokens.get(i).getAtributo());
            if(tipo.equals("None") ) {
            	tipo="entero";
            	TS.esGlobal();
            	TS.addTipoyDespl(tokens.get(i).getAtributo(), tipo, desplazamiento);
            	actualizarDesplazamiento(tipo);
            }
         
            consumir("ID");
            S1(tipo);
            return tipo;
        } else if (esToken("INPUT")) {
            listaParse.add(15);
            consumir("INPUT");
            consumir("ID");
            consumir("PYC");
            return "void";
        } else if (esToken("OUTPUT")) {
            listaParse.add(16);
            consumir("OUTPUT");
            E();
            consumir("PYC");
            return "void";
        } else if (esToken("RETURN")) {
            listaParse.add(17);
            consumir("RETURN");
            tieneReturn = true;
            String tipoRetorno = X();  // Evaluar la expresión de retorno
            consumir("PYC");
            return tipoRetorno;  // Devuelve el tipo de retorno
        } else {
            error("ID|INPUT|OUTPUT|RETURN");
            return "error";
        }
    }
  //Producciones 18-20
    private void S1(String tipo) {
    	if (esToken("IG")) {
            listaParse.add(18);
            consumir("IG");
            String tipoE = E(); 
            if (!tipo.equals(tipoE)) {
            	errorSemantico("Tipos incompatibles en asignación");
            }
            consumir("PYC");
        } else if (esToken("ASIGDIV")) {
            listaParse.add(19);
            consumir("ASIGDIV");
            String tipoE = E();
            if (!tipo.equals("entero") || !tipoE.equals("entero")) {
            	errorSemantico("División solo permitida entre enteros");
            }
            consumir("PYC");
        } else if (esToken("PARIZQ")) {
            listaParse.add(20);
            //System.out.println("Funcion: "+ TS.getId(Integer.parseInt(tokens.get(i - 1).getAtributo())));
            if (!TS.esFunc(tokens.get(i - 1).getAtributo())) {
            	String lexema = TS.getId(Integer.parseInt(tokens.get(i - 1).getAtributo()));
            	if(func && lexema.length() > 1 && lexema.charAt(1) == '&') {
            		lexema = lexema.substring(2);
            	}
                errorSemantico("El identificador '" + lexema  + "' no es una función");
            }
            consumir("PARIZQ");
            if (TS.esFunc(tokens.get(i - 2).getAtributo())) {
	            // revision parametros
	            String nombreFunc = TS.getId(Integer.parseInt(tokens.get(i - 2).getAtributo()));  // Obtener el lexema
	            ArrayList<String> tiposEsperados = TS.getParamFunc(nombreFunc);  // Tipos esperados de la función
	            ArrayList<String> tiposArgumentos = L();  // Recoge los tipos de los argumentos desde `L()`
	            //System.out.println("nombre func: "+ nombreFunc);
	            //System.out.println("tipos esperados: "+ tiposEsperados);
	            //System.out.println("tipos argumentos: "+ tiposArgumentos);
            
            	// Comparar número de argumentos
                if (tiposEsperados.size() != tiposArgumentos.size()) {
                    errorSemantico("Número de argumentos incorrecto para la función '" + nombreFunc + "'.");
                } else {
    	            for (int j = 0; j < tiposEsperados.size(); j++) {
    	                if (!tiposEsperados.get(j).equals(tiposArgumentos.get(j))) {
    	                    errorSemantico("Tipo de argumento incorrecto en posición " + (j + 1) + " para la función '" + nombreFunc + "'.");
    	                }
    	            }
                }
            }else {
            	L();
            }
            consumir("PARDER");
            consumir("PYC");
        } else {
            error("IG|ASIGDIV|PARIZQ");
        }
    }
  //Producciones 21-22
    private ArrayList<String> L() {
        ArrayList<String> tiposArgumentos = new ArrayList<>();
        if (esToken("ID") || esToken("ENT") || esToken("CAD") || esToken("PARIZQ") || esToken("NEG")) {
            listaParse.add(21);
            tiposArgumentos.add(E());  // Añade el tipo del primer argumento
            Q(tiposArgumentos);  // Agrega los siguientes argumentos, si los hay
        } else {
            listaParse.add(22);  // Lambda permitido (sin argumentos)
        }
        return tiposArgumentos;  // Devuelve la lista de tipos de argumentos
    }
  //Producciones 23-24
    private void Q(ArrayList<String> tiposArgumentos) {
        if (esToken("COMA")) {
            listaParse.add(23);
            consumir("COMA");
            tiposArgumentos.add(E());  // Añade el siguiente argumento
            Q(tiposArgumentos);  // Sigue con más argumentos, si los hay
        } else {
            listaParse.add(24);  // Lambda permitido
        }
    }
  //Producciones 25-26
    private String X() {
        if (esToken("ID") || esToken("ENT") || esToken("CAD") || esToken("PARIZQ") || esToken("NEG")) {
            listaParse.add(25);
            return E();  // Devuelve el tipo de la expresión evaluada
        } else {
            listaParse.add(26);
            return "void";  // Si es `return;`, se asume que el tipo es `void`
        }
    }
  //Producciones 27-33
    private void F() {
        listaParse.add(27);
        consumir("FUNC");
        String tipoRetorno = H();
        String id = tokens.get(i).getAtributo();
        String nombreFuncion=TS.getId(Integer.parseInt(tokens.get(i).getAtributo())); 
        //System.out.println("nombreFunc: "+ nombreFuncion);
        posFuncActual = TS.posTS(nombreFuncion);
        //System.out.println("posFunc: "+ posFuncActual);
        TS.addTipoyDespl(id, "funcion", 0);
        //System.out.println("id funcion: "+ id);
        //System.out.println("tipo funcion: "+ tipoRetorno);
        TS.addTipoRet(id, tipoRetorno);
        consumir("ID");
        consumir("PARIZQ");
        A();
        TS.setParamFunc(id, numParam);
        contFunc=numParam.size();
        //System.out.println(" tipos parametros: "+ TS.getTiposParametros(TS.getId(Integer.parseInt(id))));
        consumir("PARDER");
        consumir("LLAVIZQ");
        func=true;
        String tipoCuerpo = C();
        tieneReturn=false;
        tipoC1fin="void";
        //System.out.println("tipo cuerpo : " + tipoCuerpo );
        if (!tipoRetorno.equals(tipoCuerpo)) {
        	errorSemantico("El tipo de retorno no coincide con la función");
        }
        consumir("LLAVDER");
        //System.out.println("numero de parametros dentro de func : " + contFunc );
        func = false;
        TS.setNumP(contFunc);
        TS.addTipoyDespl(id, "funcion", 0);
        desplazamientoAux=0;
        numParam.clear();
    }

    private String H() {
        if (esToken("VOID")) {
            listaParse.add(29);
            consumir("VOID");
            return "void";
        } else {
            listaParse.add(28);
            return T();
        }
    }

    private void A() {
        if (esToken("VOID")) {
            listaParse.add(31);
            consumir("VOID");
        } else {
            listaParse.add(30);
            String tipo = T();
            String id = tokens.get(i).getAtributo();
            TS.addTipoyDespl(id, tipo, desplazamientoAux);
            actualizarDesplazamientoAux(tipo);
            TS.setParam(id);
            numParam.add(id);
            consumir("ID");
            K();
        }
    }

    private void K() {
        if (esToken("COMA")) {
            listaParse.add(32);
            consumir("COMA");
            String tipo = T();
            String id = tokens.get(i).getAtributo();
            TS.addTipoyDespl(id, tipo, desplazamientoAux);
            actualizarDesplazamientoAux(tipo);
            TS.setParam(id);
            numParam.add(id);
            consumir("ID");
            K();
        } else {
            listaParse.add(33);
        }
    }
    boolean tieneReturn = false;  // Inicialmente no se ha encontrado un return
    String tipoC1fin="void";
 // Producción C -> B {C.tipo := B.tipo} C1
    private String C() {
        listaParse.add(34);
        String tipoB = B();  // Evalúa el tipo de la primera sentencia B

        if (esToken("RETURN")) {
            tieneReturn = true;  // Si la sentencia es un return, marcamos que se ha encontrado un return
            //System.out.println("tiene return???:  ENTRAAA " );
        }

        if (tipoB.equals("error")) {
            errorSemantico("Tipo incorrecto en el cuerpo de la función.");
            return "error";
        }

        String tipoC1 = C1(tieneReturn, tipoB);  // Pasa el tipo de B y si hay return
        //System.out.println("tipofinC1:   " + tipoC1);
        
        if(tieneReturn) {
        	tipoC1fin=tipoC1;
        	//System.out.println("tiene return???:   " + tieneReturn);
        	tieneReturn=false;
        }
        //System.out.println("tipofin:   " + tipoC1fin);
        
        return tipoC1fin;  // Devuelve "void" si no hubo return, o el tipo del return si hubo
    }

    // Producción C1 -> C {C1.tipo := C.tipo}
    private String C1(boolean tieneReturn, String tipoAnterior) {
        if (esToken("FOR") || esToken("VAR") || esToken("IF") || esToken("ID") || 
            esToken("INPUT") || esToken("OUTPUT") || esToken("RETURN")) {
            listaParse.add(35);
            if (esToken("RETURN")) {
                tieneReturn = true;  // Marcar cuando encontramos un return
                //System.out.println("tiene return???:  ENTRAAA " );
            }

            String tipoC = C();  // Evaluar las siguientes sentencias
            return  tipoC;
        } else {
            listaParse.add(36);  // Producción C1 -> lambda
            return tieneReturn ? tipoAnterior : "void";  // Si no hay más sentencias y no hubo return, retorna "void".
        }
    }


 // Producciones 37-39
    private String E() {
        listaParse.add(37);
        String tipoR = R(); // Tipo del operando izquierdo
        String tipoE1 = E1(tipoR); // Resultado de la comparación
        return tipoE1; // Devuelve el tipo de la expresión completa
    }

    private String E1(String tipo) {
        if (esToken("DIST")) { // Operador !=
            listaParse.add(38);
            consumir("DIST");
            String tipoR = R(); // Tipo del operando derecho
            if (!tipo.equals(tipoR)) {
            	errorSemantico("Tipos incompatibles en comparación");
            }
            // El tipo de una comparación lógica es siempre "logico"
            return "logico";
        } else {
            listaParse.add(39); // Producción lambda
            return tipo; // Devuelve el tipo original si no hay comparación
        }
    }

    private String R() {
        listaParse.add(40);
        String tipoM = M();
        R1(tipoM);
        return tipoM;
    }

    private void R1(String tipo) {
        if (esToken("DIV")) {
            listaParse.add(41);
            consumir("DIV");
            String tipoM = M();
            if ( (tipo.equals("entero") || tipo.equals("None")) && (tipoM.equals("entero") || tipoM.equals("None")) ) {
            	R1(tipo);
            }else {
            	errorSemantico("División solo permitida entre enteros");
            }
        } else {
            listaParse.add(42);
        }
    }

    private String M() {
        if (esToken("NEG")) {
            listaParse.add(43);
            consumir("NEG");
            String tipoM = M();
            if (!tipoM.equals("logico")) {
            	errorSemantico("Operador de negación requiere lógico");
            }
            return "logico";
        } else {
            listaParse.add(44);
            return U();
        }
    }

    private String U() {
    	if (esToken("ID")) {
            listaParse.add(45);
            String id = tokens.get(i).getAtributo();
            String tipo = TS.buscaTipo(id);
            if (tipo.equals("None")) {
            	tipo="entero";
            	TS.esGlobal();
            	TS.addTipoyDespl(id, tipo, desplazamiento);
            	actualizarDesplazamiento(tipo);
            }
            consumir("ID");
            U1();
            return tipo;
        } else if (esToken("ENT")) {
            listaParse.add(47);
            consumir("ENT");
            return "entero";
        } else if (esToken("CAD")) {
            listaParse.add(48);
            consumir("CAD");
            return "cadena";
        } else if (esToken("PARIZQ")) {
            listaParse.add(46);
            consumir("PARIZQ");
            String tipoE = E();
            consumir("PARDER");
            return tipoE;
        } else {
            error("ID|ENT|CAD|PARIZQ");
            return null;
        }
    }

    private void U1() {
        if (esToken("PARIZQ")) {  // Producción U1 -> ( L )
            listaParse.add(49);
            consumir("PARIZQ");
 
            // Obtener el identificador de la función
            String idFunc = tokens.get(i - 2).getAtributo();  // El token antes del paréntesis
            String nombreFunc = TS.getId(Integer.parseInt(idFunc));  // Obtener el lexema
            ArrayList<String> tiposEsperados = TS.getParamFunc(nombreFunc);  // Tipos esperados de la función
            //System.out.println(nombreFunc);

            if (tiposEsperados == null) {
                errorSemantico("'" + nombreFunc + "' no es una función.");
            } else {
                ArrayList<String> tiposArgumentos = L();  // Recoge los tipos de los argumentos desde `L()`

                // Comparar número de argumentos
                if (tiposEsperados.size() != tiposArgumentos.size()) {
                    errorSemantico("Número de argumentos incorrecto para la función '" + nombreFunc + "'.");
                } else {
                    // Comparar tipos de argumentos
                    for (int j = 0; j < tiposEsperados.size(); j++) {
                        if (!tiposEsperados.get(j).equals(tiposArgumentos.get(j))) {
                            errorSemantico("Tipo de argumento incorrecto en posición " + (j + 1) + " para la función '" + nombreFunc + "'.");
                        }
                    }
                }
            }

            consumir("PARDER");
        } else {  // Producción U1 -> LAMBDA
            listaParse.add(50);
        }
    }

    private void AUX() {
        if (esToken("ID")) {
            listaParse.add(51);
            consumir("ID");
            AUX1();
        } else {
            listaParse.add(52);
        }
    }

    private void AUX1() {
        if (esToken("IG")) {
            listaParse.add(53);
            consumir("IG");
            E();
        } else if (esToken("ASIGDIV")) {
            listaParse.add(54);
            consumir("ASIGDIV");
            E();
        } else if (esToken("PARIZQ")) {
            listaParse.add(55);
            consumir("PARIZQ");
            L();
            consumir("PARDER");
        } else {
            error("IG|ASIGDIV|PARIZQ");
        }
    }

    private void actualizarDesplazamiento(String tipo) {
        if (tipo.equals("entero")) {
            desplazamiento += 4;
        } else if (tipo.equals("cadena")) {
            desplazamiento += 64;
        } else if (tipo.equals("logico")) {
            desplazamiento += 1;
        }
    }
    private void actualizarDesplazamientoAux(String tipo) {
        if (tipo.equals("entero")) {
            desplazamientoAux += 4;
        } else if (tipo.equals("cadena")) {
        	desplazamientoAux += 64;
        } else if (tipo.equals("logico")) {
        	desplazamientoAux += 1;
        }
    }

    private void consumir(String codigoEsperado) {
        if (tokens.get(i).getCodigo().equals(codigoEsperado)) {
            i++;
        } else {
            error(codigoEsperado);
        }
    }

    private boolean esToken(String codigoEsperado) {
        return tokens.get(i).getCodigo().equals(codigoEsperado);
    }

    private void error(String esperado) {
        System.out.println("Error Sintáctico en línea " + tokens.get(i).getLinea() + ": se esperaba " + esperado + ", se encontró " + tokens.get(i).getCodigo());
    }
    private void errorSemantico(String mensaje) {
        System.out.println("Error Semántico en línea " + tokens.get(i).getLinea() + ": " + mensaje);
    }
}