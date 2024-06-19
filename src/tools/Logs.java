/*
 * Copyright (C) 14-may-2024 Miguel González García
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Miguel González García
 */
public class Logs {

    private FileWriter fichero = null;
    private PrintWriter pw = null;
    private String separador = "\n-------------------------------------";

    public Logs(String nombreArchivo, String texto, String algoritmo) {
        try {
            String carpeta = "log/" + algoritmo + "/";
            File directorio = new File(carpeta);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            File file = new File(carpeta + nombreArchivo + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            fichero = new FileWriter(carpeta + nombreArchivo + ".txt");
            pw = new PrintWriter(fichero);

            pw.write(texto + separador);

        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public void escribir(String texto) {
        try {
            fichero.write("\n[INFORMACION]\n" + texto + "\n");
        } catch (IOException e) {
            System.out.println("tools.GuardarLog.escribir()" + e.toString());
        }
    }

    public void escribirNoInfo(String texto) {
        try {
            fichero.write("\n" + texto + "\n");
        } catch (IOException e) {
            System.out.println("tools.GuardarLog.escribir()" + e.toString());
        }
    }

    public void escribirFinal(String texto) {
        try {
            fichero.write(separador + "\n" + texto + "\n");
            fichero.close();
        } catch (IOException e) {
            System.out.println("tools.GuardarLog.escribir()" + e.toString());
        }
    }
}
