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

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class PluginNodeListener implements ReaperProjectListener{

  private List<ReaperPlugin> reaperPlugins = new ArrayList<>();

  public List<ReaperPlugin> getReaperPlugins() {
    return reaperPlugins;
  }

  @Override
  public void enterRoot(ReaperProjectParser.RootContext ctx) {

  }

  @Override
  public void exitRoot(ReaperProjectParser.RootContext ctx) {

  }

  @Override
  public void enterNode(ReaperProjectParser.NodeContext ctx) {

    if (!ctx.name().getText().equals("VST")) {
      return;
    }

    ReaperPlugin plugin = new ReaperPlugin();
    if (ctx.value().size() > 0) {
      plugin.setName(ctx.value(0).getText().replaceAll("\"", ""));
    }
    if (ctx.value().size() > 1) {
      plugin.setFilename(ctx.value(1).getText());
    }
    if (ctx.value().size() > 4) {
      plugin.setRawId(ctx.value(4).getText());
    }
    reaperPlugins.add(plugin);


  }

  @Override
  public void exitNode(ReaperProjectParser.NodeContext ctx) {

  }

  @Override
  public void enterName(ReaperProjectParser.NameContext ctx) {

  }

  @Override
  public void exitName(ReaperProjectParser.NameContext ctx) {

  }

  @Override
  public void enterElement(ReaperProjectParser.ElementContext ctx) {

  }

  @Override
  public void exitElement(ReaperProjectParser.ElementContext ctx) {

  }

  @Override
  public void enterLeaf(ReaperProjectParser.LeafContext ctx) {

  }

  @Override
  public void exitLeaf(ReaperProjectParser.LeafContext ctx) {

  }

  @Override
  public void enterValue(ReaperProjectParser.ValueContext ctx) {

  }

  @Override
  public void exitValue(ReaperProjectParser.ValueContext ctx) {

  }

  @Override
  public void enterProp(ReaperProjectParser.PropContext ctx) {

  }

  @Override
  public void exitProp(ReaperProjectParser.PropContext ctx) {

  }

  @Override
  public void visitTerminal(TerminalNode terminalNode) {

  }

  @Override
  public void visitErrorNode(ErrorNode errorNode) {

  }

  @Override
  public void enterEveryRule(ParserRuleContext parserRuleContext) {

  }

  @Override
  public void exitEveryRule(ParserRuleContext parserRuleContext) {

  }
}
