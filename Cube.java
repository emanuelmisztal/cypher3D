/*
Cypher3D
Author: Emanuel Misztal
Desription: 3D extension of Playfair cypher
 */
package cypher3d;
import java.util.Random; //for pseudo-random generations - in future change to secure-random

public class Cube {
    //variables
    private char[][] matrix; //standard Playfair matrix
    private char[][][] cube; //symbols cube
    
    //constructor
    public Cube(String pass){
        matrix = new char[5][5];
        cube = new char[5][5][10];
        if(pass.length() % 2 != 0) pass += "x"; //adds 'x' if password is not even
        else if(pass.length() == 1) pass += "x"; //same
        else if(pass.length() == 0) pass = "defaultx"; //puts default password for empty string
        insertMatrix(pass); //passing pass into matrix
        generateCube(); //generating symbols cube
    }
    
    //function passing password into matrix and filling rest of the space with random characters
    private void insertMatrix(String pass){
        boolean fl; //just a flag
        int i = 0;
        char c;
        Random r = new Random(); //creating random seed
        
        //putting password into matrix
        for(int x = 0; x < 5; x++){
            for(int y = 0; y < 5; y++){
                fl = false;
                while (!fl){
                    if(i < pass.length()){
                        if(!inMatrix(pass.charAt(i))){
                            if(pass.charAt(i) == 'j'){
                                i++;
                            }
                            else{
                                matrix[x][y] = pass.charAt(i);
                                i++;
                                fl = true;
                            }
                        }
                        else i++;
                    }
                    else{
                        do{ //putting random character into matrix
                            c = (char)(r.nextInt('z' + 1 - 'a') + 'a');
                            if(c == 'j') c = 'i';
                        }while(inMatrix(c));
                        matrix[x][y] = c;
                        fl = true;
                    }
                }
            }
        }
    } 
    
    //function generating the cube
    private void generateCube(){
        char tmpChar;
        Random r = new Random(); //creating random seed
        
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                for(int k = 0; k < 10; k++){
                    do{ //filling cube with random characters
                        tmpChar = (char)(r.nextInt(256));
                    }while(inCube(tmpChar));
                    cube[i][j][k] = tmpChar;
                } 
            }
        }
    } 
    
    //function to check if char is already in matrix
    private boolean inMatrix(char c){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(matrix[i][j] == c) return true;
            }
        }
        return false;
    }
    
    //function to check if char is already in cube
    private boolean inCube(char c){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                for(int k = 0; k < 10; k++) if(cube[i][j][k] == c) return true;
            }
        }
        return false;
    }
    
    //returns character in front of symbol
    private char fromCube(char c){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                for(int k = 0; k < 10; k++){
                    if( c == cube[i][j][k]) return matrix[i][j];
                }
            }
        }
        return 'x';
    }
    
    //print matrix
    public String printMatrix(){
        String output = "";
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++) output += matrix[i][j] + "    ";
            output += "\n";
        }
        return output;
    }
    
    //print line from cube behind matrix[x][y]
    public String printCubeRow(int x, int y){
        String output = "";
        for(int i = 0; i < 10; i++) output += cube[x][y][i] + " ";
        return output;
    }
    
    //shift char left up corner
    private int adjustLeftUp(int pos){
	return (pos + 4) % 5;
    }

    //shift char right down corner
    private int adjustRightDown(int pos){
	return (pos + 1) % 5;
    }

    //main function of ciphering (mode = false) and deciphering (mode = true) String
    public String coder(String in, boolean mode){
	String out = "", init = "";
	char f = in.charAt(0), s = in.charAt(1);
	int fr = 0, fc = 0, sr = 0, sc = 0;
        
        //search for character in matrix
	if(mode == false){
            if(f == 'j') f = 'i';
            if(s == 'j') s = 'i';
            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 5; j++){
                    if(f == matrix[i][j]){
                        fr = i;
                        fc = j;
                    }
                    if(s == matrix[i][j]){
                        sr = i;
                        sc = j;
                    }
                }
            }
        }
        else{
            f = fromCube(f);
            s = fromCube(s);
            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 5; j++){
                    if(f == matrix[i][j]){
                        fr = i;
                        fc = j;
                    }
                    if(s == matrix[i][j]){
                        sr = i;
                        sc = j;
                    }
                }
            }
        }
        
       //actual coding/decoding
	if(fr == sr){
            if (mode == true) out = init + matrix[fr][adjustRightDown(fc)] + matrix[sr][adjustRightDown(sc)];
            else if (mode == false) out = init + cube[fr][adjustLeftUp(fc)][modulator(adjustLeftUp(fc), fr, matrix[fr][adjustLeftUp(fc)])] + cube[sr][adjustLeftUp(sc)][modulator(adjustLeftUp(sc), sr, matrix[sr][adjustLeftUp(sc)])];
	}
	else if(fc == sc){
            if(mode == true) out = init + matrix[adjustRightDown(fr)][fc] + matrix[adjustRightDown(sr)][sc];
            else if(mode == false) out = init + cube[adjustLeftUp(fr)][fc][modulator(fc, adjustLeftUp(fr), matrix[adjustLeftUp(fr)][fc])] + cube[adjustLeftUp(sr)][sc][modulator(sc, adjustLeftUp(sr), matrix[adjustLeftUp(sr)][sc])];
	}
        else if(mode == true) out = init + matrix[fr][sc] + matrix[sr][fc];
        else out = init + cube[fr][sc][modulator(sc, fr, matrix[fr][sc])] + cube[sr][fc][modulator(fc, sr, matrix[sr][fc])];
        
	return out;
    }
    
    //modulator function - decides wich symbol to pic from behind the letter
    private int modulator(int x, int y, char c){
        return x * y * c  % 10;
    }
}
