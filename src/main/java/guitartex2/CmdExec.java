/* 
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package guitartex2;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class CmdExec{
	public CmdExec () {
		
	}
	
	public String execute (String cmdline) {
		String ausgabe = "";
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmdline);
                    try (BufferedReader input = new BufferedReader
                                                (new InputStreamReader(p.getInputStream()))) {
                        while ((line = input.readLine()) != null) {
				ausgabe = ausgabe + line;
			}
                    }
		}catch (Exception err) {
        	new InfoBox("ERR: " + err);
		}
		return ausgabe;
	}
}