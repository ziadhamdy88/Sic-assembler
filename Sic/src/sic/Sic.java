/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author Lenovo
 */
public class Sic {

    static String[] op_TAB = { "ADD", "ADDF", "ADDR", "AND", "CLEAR", "COMP", 
                               "COMPF", "COMPR", "DIV", "DIVF", "DIVR","FIX", "FLOAT", 
                               "HIO", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS",
                               "LDT", "LDX", "LPS", "MUL", "MULF", "MULR", "NORM",
                               "OR", "RD", "RMO", "RSUB", "SHIFTL", "SHIFTR",
                               "SIO", "SSK", "STA", "STB", "STCH", "STF", "STI", 
                               "STL", "STS", "STSW", "STT", "STX", "SUB", "SUBF",
                               "SUBR", "SVC", "TD", "TIO", "TIX", "TIXR", "WD" };
        
        
    static String[] opCode = { "18", "58", "90", "40", "B4", "28", "88", "A0", "24", "64", "9C", "C4", "C0", "F4", "3C",
                               "30", "34", "38", "48", "00", "68", "50", "70", "08", "6C", "74", "04", "E0", "20", "60", "98", "C8",
                               "44", "D8", "AC", "4C", "A4", "A8", "F0", "EC", "0C", "78", "54", "80", "D4", "14", "7C", "E8", "84",
                               "10", "1C", "5C", "94", "B0", "E0", "F8", "2C", "B8", "DC" };
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        
        

        
        String OP;
        String Sym;

        
        int Address=0;
        //int [] StartAddress = {};
        int EndAddress = 0;
        int i;
        int l=0;
        int counter = 0;
 
       ArrayList<String> SymbolTable= new ArrayList<String>();
       ArrayList<String> SymbolAddress = new ArrayList<String>();
       ArrayList<Integer> StartAddress = new ArrayList<Integer>();

        
        int count = 0;
        File file = new File ("Instructions.txt");
        try (Scanner scanner = new Scanner (file))
        {
            
            while (scanner.hasNext() && count < 50)
            {
                String Line = scanner.nextLine();
                String [] Arr = new String [4];
                Arr = Line.split("\\t");
                
               //System.out.println("1");
                
                if (Arr[1].equals("START"))
                {
                    String temp="";
                    if (Arr[0].length()<6)
                    {
                        
                        temp = Arr[0];
                        for (i=0;i<=6-temp.length();i++)
                        {
                            temp=temp+"X";
                        }
                    }
                    SymbolTable.add(temp);
                    SymbolAddress.add(Arr[2]);
                    StartAddress.add(Integer.parseInt(Arr[2],16));
                    Address = Integer.parseInt(Arr[2],16);
                }
                else if (IsInstruc(Arr[0]))
                {
                        Address = Address + 3;
                       
                        
                        
                }
                else if (IsInstruc(Arr[1])||Arr[1].equals("BYTE")||Arr[1].equals("WORD"))
                {                       
                        
                        SymbolAddress.add(Integer.toString(Address));
                        SymbolTable.add(Arr[0]);
                        Address = Address + 3;
                        
                        
                        
                }
                
                else if (Arr[1].equals("RESW"))
                {
                    
                    SymbolAddress.add(Integer.toString(Address));
                    SymbolTable.add(Arr[0]);
                    Address = Address + Integer.parseInt(Arr[2])*3;
                   
                    
                }
                else if (Arr[1].equals("RESB"))
                {
                    
                    SymbolAddress.add(Integer.toString(Address));
                    SymbolTable.add(Arr[0]);
                    Address = Address + Integer.parseInt(Arr[2]);
                    
                }
                else if (Arr[0].equals("END"))
                {
                    EndAddress = Address;
                    
                    
                }
               
                else
                    Address = 0;
                
                count++;
                
                if (count%10==0)
                    StartAddress.add(Address);
            }
            scanner.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
       
        
        try (Scanner scanner1 = new Scanner (file))
        {
            
            String temp=String.format("%02X",(EndAddress-StartAddress.get(0)));
            if (temp.length()<6)
                    {
                        int n = temp.length();
                        for (i=0;i<6-n;i++)
                        {
                            temp="0"+temp;
                        }
                    }
            i=0;   
            FileWriter Writer = new FileWriter(new File("HTERECORD.txt"));
            Writer.write("H."+SymbolTable.get(0)+"."+SymbolAddress.get(0)+"."+temp );
            while (scanner1.hasNextLine())
            {

                if ((counter==0)||(counter==10)||(counter==20))
                {
                    temp=String.format("%02X", StartAddress.get(l));
                    if (temp.length()<6)
                    {
                        int n = temp.length();
                        for (i=0;i<6-n;i++)
                        {
                            temp="0"+temp;
                        }
                    }
                    
                    System.out.println(counter);
                    Writer.write("\nT." +temp );
                    l++;
                }
                //string.format("%02X, thenumber)
                String Line = scanner1.nextLine();
                String[] Arr = Line.split("\\t");
                if (IsInstruc(Arr[0]))
                {
                    OP = CheckOP(Arr[0]);
                    Sym = SymToAddress(Arr[1], SymbolAddress, SymbolTable);
                    if (Arr[1].contains(","))
                    {
                        Sym = Sym + 32768;
                    }
                    Writer.write("."+OP+String.format("%02X", Integer.parseInt(Sym)));
                    counter++;
                }
                else if (IsInstruc(Arr[1]))
                {
                    OP = CheckOP(Arr[1]);
                    Sym = SymToAddress(Arr[2], SymbolAddress, SymbolTable);
                    if (Arr[2].contains(","))
                    {
                        Sym = Sym + 32768;
                    }
                    Writer.write("."+OP+String.format("%02X", Integer.parseInt(Sym)));
                    counter++;
                }
                else if(IsInstruc(Arr[1])==false&&!(Arr[1].equals("START"))&&!(Arr[1].equals("END")))
                {
                    try
                    {
                        OP = "00";
                        System.out.println(Arr[2]);
                        Sym = Arr[2];
                        Writer.write("."+OP+String.format("%02X", Integer.parseInt(Sym)));
                        counter++;
                    }
                   catch (ArrayIndexOutOfBoundsException e){
                         e.printStackTrace();
        }
               
                }
                else if(Arr[0].equals("END"))
                {
                    Writer.write("\n" + "E."+String.format("%02X", EndAddress));
                }
                
                
                
                
            }
            Writer.close();
            scanner1.close();
            
        }     
                
    }
    public static boolean IsInstruc (String Instruc )
        { 
            for (int i=0; i<59; i++)
            {

                if (Instruc.equals(op_TAB[i]))
                { 
                    return true;
                }
            }
            return false;
        }
    public static String CheckOP (String Instruc)
    {
        int i=0;
        boolean flag = false;
        while(flag == false)
        {
            if (Instruc.equals(op_TAB[i]))
                flag = true;
            else
                i++;
        }
        return opCode [i];
    }
    
    public static String SymToAddress(String Sym, ArrayList<String> Address, ArrayList<String> Symbol )
    {
        for ( int i=0; i<=Symbol.size(); i++ )
        {
            if(Sym.equals(Symbol.get(i)))
                return Address.get(i);
        }  
        return "NotFound" ;
    }
}
