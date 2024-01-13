/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.parsers.reaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReaperProjectGrammarTest {

  @Test
  public void parseSimpleReaperLikeFile() throws IOException {

    File file = new File(this.getClass().getClassLoader()
            .getResource("reaper/simpleReaperFile.rpp").getFile());

    InputStream is = new FileInputStream(file);

    ReaperProjectLexer lexer = new ReaperProjectLexer(CharStreams.fromStream(is));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ReaperProjectParser parser = new ReaperProjectParser(tokens);

    ParseTree pt = parser.node();

  }

  @Test
  public void parseReaper7File() throws IOException {

    File file = new File(this.getClass().getClassLoader()
            .getResource("reaper/reaper7.rpp").getFile());

    InputStream is = new FileInputStream(file);

    ReaperProjectLexer lexer = new ReaperProjectLexer(CharStreams.fromStream(is));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ReaperProjectParser parser = new ReaperProjectParser(tokens);

    ParseTree pt = parser.node();

  }
}
