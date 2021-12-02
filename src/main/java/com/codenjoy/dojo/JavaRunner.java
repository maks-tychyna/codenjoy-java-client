package com.codenjoy.dojo;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.OneCommandSolver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.utils.PrintUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codenjoy.dojo.client.runner.ReflectLoader.loadJavaBoard;
import static com.codenjoy.dojo.client.runner.ReflectLoader.loadJavaSolver;
import static com.codenjoy.dojo.utils.PrintUtils.Color.INFO;

public class JavaRunner {

    private static Logger log = LoggerFactory.getLogger(JavaRunner.class);

    // Select your game
    private String game = "clifford";

    // Paste here board page url from browser after registration,
    // or put it as command line parameter.
    private String url =
            "https://dojorena.io/codenjoy-contest/board/player/dojorena981?code=6913910346978543810";
//    private String url =
//        "http://127.0.0.1:8080/codenjoy-contest/board/player/onz3db1kk8x47g448w88?code=8319177470455860273";


    public void run(String[] args) {
        System.out.println("+-----------------+");
        System.out.println("| Starting runner |");
        System.out.println("+-----------------+");

        if (args != null && args.length == 2) {
            game = args[0];
            url = args[1];
            printInfo("Environment");
        } else {
            printInfo("Runner");
        }

        WebSocketRunner.runClient(url, loadJavaSolver(game), loadJavaBoard(game));
    }

    private void printInfo(String source) {
        PrintUtils.printf(
                "Got from %s:\n" +
                "\t 'GAME': '%s'\n" +
                "\t 'URL':  '%s'\n",
                INFO,
                source,
                game,
                url);
    }

    public <B extends ClientBoard> void send(String command, B board) {
        WebSocketRunner.runClient(null, url, new OneCommandSolver<>(command), board);
    }

    public static void main(String[] args) {
        new JavaRunner().run(args);
    }
}
