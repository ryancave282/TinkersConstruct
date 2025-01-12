package net.ryancave282.tconstruct.library.tools.definition.module.material;

import net.ryancave282.tconstruct.library.tools.definition.ToolDefinition;
import net.ryancave282.tconstruct.library.tools.definition.module.ToolHooks;
import net.ryancave282.tconstruct.library.tools.part.IToolPart;

import java.util.List;

/** Hook to get parts from a tool */
public interface ToolPartsHook {
  /** Gets the list of parts on this tool */
  List<IToolPart> getParts(ToolDefinition definition);

  /** Gets the tool parts from the given definition */
  static List<IToolPart> parts(ToolDefinition definition) {
    return definition.getHook(ToolHooks.TOOL_PARTS).getParts(definition);
  }
}
